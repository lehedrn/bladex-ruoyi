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
import org.springblade.core.literule.provider.Rule;
import org.springblade.core.tool.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 顺序规则链构建器
 * 用于构建顺序执行的规则链
 *
 * @author BladeX
 */
@Getter
public class ThenRuleChain extends AbstractRuleChain {
	private final List<Object> rules = new ArrayList<>();

	/**
	 * 设置规则链ID
	 *
	 * @param id 规则链ID
	 * @return 构建器实例
	 */
	public ThenRuleChain ID(String id) {
		this.id = id;
		return this;
	}

	/**
	 * 添加规则
	 *
	 * @param ruleIds 规则ID数组
	 * @return 构建器实例
	 */
	public ThenRuleChain THEN(String... ruleIds) {
		this.rules.addAll(Arrays.asList(ruleIds));
		return this;
	}

	/**
	 * 添加规则链
	 *
	 * @param rule 规则实例
	 * @return 构建器实例
	 */
	public ThenRuleChain THEN(RuleChain rule) {
		this.rules.add(rule);
		return this;
	}

	/**
	 * 构建规则链
	 *
	 * @return 规则实例
	 */
	@Override
	public RuleChain build() {
		// 预先获取所有规则实例
		Map<String, Rule> ruleInstances = getListRuleInstances(rules);
		return new ThenRuleExecutor(ruleInstances);
	}

	/**
	 * 顺序规则执行器内部类
	 */
	@RequiredArgsConstructor
	private class ThenRuleExecutor implements RuleChain {
		private final Map<String, Rule> ruleInstances;

		@Override
		public String id() {
			return getId();
		}

		@Override
		public void execute() throws Exception {
			// 依次执行所有规则
			for (Object rule : rules) {
				if (rule instanceof String ruleId) {
					Rule ruleBean = getRuleInstance(ruleId, ruleInstances);
					executeWithStackManagement(ruleId, ruleBean);
				} else if (rule instanceof RuleChain ruleChain) {
					String id = StringUtil.isNotBlank(ruleChain.id()) ? ruleChain.id() : ruleChain.getClass().getSimpleName();
					Rule ruleBean = getRuleInstance(id, ruleInstances);
					executeWithStackManagement(id, ruleBean);
				}
			}
		}
	}
}
