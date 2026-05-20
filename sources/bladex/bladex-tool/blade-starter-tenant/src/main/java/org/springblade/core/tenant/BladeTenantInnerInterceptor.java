/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springblade.core.tenant;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.ParenthesedFromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.*;

/**
 * 租户基础拦截器，拓展mybatis-plus私有方法便于继承
 *
 * @author Chill、hubin
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BladeTenantInnerInterceptor extends TenantLineInnerInterceptor {

	@Override
	public void setTenantLineHandler(TenantLineHandler tenantLineHandler) {
		super.setTenantLineHandler(tenantLineHandler);
	}

	/**
	 * 获取主表列表
	 */
	protected List<Table> processMainTables(final PlainSelect plainSelect, final String whereSegment) {
		FromItem fromItem = plainSelect.getFromItem();
		List<Table> list = processFromItem(fromItem, whereSegment);
		return new ArrayList<>(list);
	}

	/**
	 * 将父类方法修改为 protected，便于子类继承
	 */
	protected List<Table> processFromItem(FromItem fromItem, final String whereSegment) {
		// 处理括号括起来的表达式
		List<Table> mainTables = new ArrayList<>();
		// 无 join 时的处理逻辑
		if (fromItem instanceof Table) {
			Table fromTable = (Table) fromItem;
			mainTables.add(fromTable);
		} else if (fromItem instanceof ParenthesedFromItem) {
			// SubJoin 类型则还需要添加上 where 条件
			List<Table> tables = processSubJoin((ParenthesedFromItem) fromItem, whereSegment);
			mainTables.addAll(tables);
		} else {
			// 处理下 fromItem
			processOtherFromItem(fromItem, whereSegment);
		}
		return mainTables;
	}

	/**
	 * 将父类方法修改为 protected，便于子类继承
	 */
	protected List<Table> processSubJoin(ParenthesedFromItem subJoin, final String whereSegment) {
		while (subJoin.getJoins() == null && subJoin.getFromItem() instanceof ParenthesedFromItem) {
			subJoin = (ParenthesedFromItem) subJoin.getFromItem();
		}
		List<Table> tableList = processFromItem(subJoin.getFromItem(), whereSegment);
		List<Table> mainTables = new ArrayList<>(tableList);
		if (subJoin.getJoins() != null) {
			processJoins(mainTables, subJoin.getJoins(), whereSegment);
		}
		return mainTables;
	}


	/**
	 * 将父类方法修改为 protected，便于子类继承
	 */
	protected List<Table> processJoins(List<Table> mainTables, List<Join> joins, final String whereSegment) {
		// join 表达式中最终的主表
		Table mainTable = null;
		// 当前 join 的左表
		Table leftTable = null;

		if (mainTables.size() == 1) {
			mainTable = mainTables.get(0);
			leftTable = mainTable;
		}

		//对于 on 表达式写在最后的 join，需要记录下前面多个 on 的表名
		Deque<List<Table>> onTableDeque = new LinkedList<>();
		for (Join join : joins) {
			// 处理 on 表达式
			FromItem joinItem = join.getRightItem();

			// 获取当前 join 的表，subJoint 可以看作是一张表
			List<Table> joinTables = null;
			if (joinItem instanceof Table) {
				joinTables = new ArrayList<>();
				joinTables.add((Table) joinItem);
			} else if (joinItem instanceof ParenthesedFromItem) {
				joinTables = processSubJoin((ParenthesedFromItem) joinItem, whereSegment);
			}

			if (joinTables != null && !joinTables.isEmpty()) {

				// 如果是隐式内连接
				if (join.isSimple()) {
					mainTables.addAll(joinTables);
					continue;
				}

				// 当前表是否忽略
				Table joinTable = joinTables.get(0);

				List<Table> onTables = null;
				// 如果不要忽略，且是右连接，则记录下当前表
				if (join.isRight()) {
					mainTable = joinTable;
					mainTables.clear();
					if (leftTable != null) {
						onTables = Collections.singletonList(leftTable);
					}
				} else if (join.isInner()) {
					if (mainTable == null) {
						onTables = Collections.singletonList(joinTable);
					} else {
						onTables = Arrays.asList(mainTable, joinTable);
					}
					mainTable = null;
					mainTables.clear();
				} else {
					onTables = Collections.singletonList(joinTable);
				}

				if (mainTable != null && !mainTables.contains(mainTable)) {
					mainTables.add(mainTable);
				}

				// 获取 join 尾缀的 on 表达式列表
				Collection<Expression> originOnExpressions = join.getOnExpressions();
				// 正常 join on 表达式只有一个，立刻处理
				if (originOnExpressions.size() == 1 && onTables != null) {
					List<Expression> onExpressions = new LinkedList<>();
					onExpressions.add(builderExpression(originOnExpressions.iterator().next(), onTables, whereSegment));
					join.setOnExpressions(onExpressions);
					leftTable = mainTable == null ? joinTable : mainTable;
					continue;
				}
				// 表名压栈，忽略的表压入 null，以便后续不处理
				onTableDeque.push(onTables);
				// 尾缀多个 on 表达式的时候统一处理
				if (originOnExpressions.size() > 1) {
					Collection<Expression> onExpressions = new LinkedList<>();
					for (Expression originOnExpression : originOnExpressions) {
						List<Table> currentTableList = onTableDeque.poll();
						if (CollectionUtils.isEmpty(currentTableList)) {
							onExpressions.add(originOnExpression);
						} else {
							onExpressions.add(builderExpression(originOnExpression, currentTableList, whereSegment));
						}
					}
					join.setOnExpressions(onExpressions);
				}
				leftTable = joinTable;
			} else {
				processOtherFromItem(joinItem, whereSegment);
				leftTable = null;
			}
		}

		return mainTables;
	}

}
