/**
 * BladeX Commercial License Agreement
 * Copyright (c) 2018-2099, https://bladex.cn. All rights reserved.
 * <p>
 * Use of this software is governed by the Commercial License Agreement
 * obtained after purchasing a license from BladeX.
 * <p>
 * 1. This software is for development use only under a valid license
 * from BladeX.
 * <p>
 * 2. Redistribution of this software's source code to any third party
 * without a commercial license is strictly prohibited.
 * <p>
 * 3. Licensees may copyright their own code but cannot use segments
 * from this software for such purposes. Copyright of this software
 * remains with BladeX.
 * <p>
 * Using this software signifies agreement to this License, and the software
 * must not be used for illegal purposes.
 * <p>
 * THIS SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY. The author is
 * not liable for any claims arising from secondary or illegal development.
 * <p>
 * Author: Chill Zhuang (bladejava@qq.com)
 */
package org.springblade.core.tenant;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.update.Update;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.CollectionUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 租户拦截器
 *
 * @author Chill
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BladeTenantInterceptor extends BladeTenantInnerInterceptor {

	/**
	 * 租户配置文件
	 */
	private BladeTenantProperties tenantProperties;
	/**
	 * 超管需要启用租户过滤的表
	 */
	private List<String> adminTenantTables = Arrays.asList("blade_top_menu", "blade_dict_biz");

	@Override
	public void setTenantLineHandler(TenantLineHandler tenantLineHandler) {
		super.setTenantLineHandler(tenantLineHandler);
	}

	/**
	 * select 条件处理
	 */
	@Override
	protected void processPlainSelect(final PlainSelect plainSelect, final String whereSegment) {
		// 判断 mainTable 进行条件追加
		List<Table> mainTables = super.processMainTables(plainSelect, whereSegment);
		if (CollectionUtils.isNotEmpty(mainTables) && !doTenantFilters(mainTables)) {
			super.processPlainSelect(plainSelect, whereSegment);
		}
	}

	/**
	 * insert 条件处理
	 */
	@Override
	protected void processInsert(Insert insert, int index, String sql, Object obj) {
		// 官方已支持租户ID自定义设置，无需再定义租户增强功能
		super.processInsert(insert, index, sql, obj);
	}

	/**
	 * update 条件处理
	 */
	@Override
	protected void processUpdate(Update update, int index, String sql, Object obj) {
		final Table table = update.getTable();
		if (doTenantFilter(table.getName())) {
			// 过滤退出执行
			return;
		}
		super.processUpdate(update, index, sql, obj);
	}

	/**
	 * delete 条件处理
	 */
	@Override
	protected void processDelete(Delete delete, int index, String sql, Object obj) {
		final Table table = delete.getTable();
		if (doTenantFilter(table.getName())) {
			// 过滤退出执行
			return;
		}
		super.processDelete(delete, index, sql, obj);
	}

	/**
	 * 判断当前操作是否需要进行过滤
	 *
	 * @param tableName 表名
	 */
	public boolean doTenantFilter(String tableName) {
		return AuthUtil.isAdministrator() && tenantProperties.getEnhance() && !adminTenantTables.contains(tableName);
	}

	/**
	 * 判断当前操作是否需要进行过滤
	 *
	 * @param tables 表名
	 */
	public boolean doTenantFilters(List<Table> tables) {
		List<String> tableNames = tables.stream().map(Table::getName).collect(Collectors.toList());
		return AuthUtil.isAdministrator() && tenantProperties.getEnhance() && !CollectionUtil.containsAny(adminTenantTables, tableNames);
	}

}
