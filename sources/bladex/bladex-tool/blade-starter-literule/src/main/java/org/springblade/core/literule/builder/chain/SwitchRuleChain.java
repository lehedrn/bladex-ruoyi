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
package org.springblade.core.literule.builder.chain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springblade.core.literule.builder.RuleBuilderExecutor;
import org.springblade.core.literule.exception.RuleException;
import org.springblade.core.literule.provider.Rule;
import org.springblade.core.literule.provider.SwitchRule;
import org.springblade.core.tool.utils.SpringUtil;

import java.util.*;

/**
 * 分支规则链构建器
 * 用于构建条件分支的规则链
 *
 * @author BladeX
 */
@Getter
public class SwitchRuleChain extends AbstractRuleChain {
	private final String conditionRuleId;
	private final List<Object> branchRules = new ArrayList<>();

	/**
	 * 构造函数
	 *
	 * @param conditionRuleId 条件规则ID
	 */
	public SwitchRuleChain(String conditionRuleId) {
		this.conditionRuleId = conditionRuleId;
	}

	/**
	 * 设置规则链ID
	 * @param id 规则链ID
	 * @return 构建器实例
	 */
	public SwitchRuleChain ID(String id) {
		this.id = id;
		return this;
	}

	/**
	 * 添加分支规则ID
	 *
	 * @param ruleIds 规则ID数组
	 * @return 构建器实例
	 */
	public SwitchRuleChain TO(Object... ruleIds) {
		if (ruleIds != null) {
			Collections.addAll(this.branchRules, ruleIds);
		}
		return this;
	}

	/**
	 * 构建规则链
	 *
	 * @return 规则实例
	 */
	@Override
	public RuleChain build() {
		// 预先获取条件规则实例
		final SwitchRule conditionRule = SpringUtil.getBean(conditionRuleId, SwitchRule.class);
		if (conditionRule == null) {
			throw new RuleException("Condition rule not found: " + conditionRuleId);
		}

		// 预先获取所有分支规则实例
		Map<String, Rule> branchRuleInstances = getListRuleInstances(branchRules);

		return new SwitchRuleExecutor(conditionRuleId, conditionRule, branchRuleInstances);
	}

	/**
	 * 分支规则执行器内部类
	 */
	@RequiredArgsConstructor
	public class SwitchRuleExecutor implements RuleChain {
		private final String conditionRuleId;
		private final SwitchRule conditionRule;
		private final Map<String, Rule> branchRuleInstances;

		@Override
		public String id() {
			return getId() == null ? conditionRuleId : getId();
		}

		@Override
		public void execute() throws Exception {
			try {
				// 添加到执行栈
				RuleBuilderExecutor.getExecutionStack().add(conditionRuleId);

				// 执行条件规则
				List<String> nextRules = conditionRule.execute();

				// 执行分支规则
				for (String ruleId : nextRules) {
					// 使用基类方法获取规则实例并执行
					Rule rule = getRuleInstance(ruleId, branchRuleInstances);
					if (rule != null) {
						executeWithStackManagement(ruleId, rule);
					}
				}
			} finally {
				// 从执行栈中移除
				RuleBuilderExecutor.getExecutionStack().remove(conditionRuleId);
			}
		}
	}
}
