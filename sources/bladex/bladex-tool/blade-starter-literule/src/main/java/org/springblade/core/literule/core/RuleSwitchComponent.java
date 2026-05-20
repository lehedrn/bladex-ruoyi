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
package org.springblade.core.literule.core;

import lombok.extern.slf4j.Slf4j;
import org.springblade.core.literule.context.RuleContextManager;
import org.springblade.core.literule.provider.RuleContext;
import org.springblade.core.literule.provider.SwitchRule;

import java.util.List;

/**
 * 分支规则抽象实现
 * 提供分支规则执行的模板方法，处理执行前后的通用逻辑
 *
 * @author BladeX
 */
@Slf4j
public abstract class RuleSwitchComponent extends AbstractRuleComponent implements SwitchRule {

	@Override
	public final List<String> execute() throws Exception {
		String ruleId = getRuleId();
		long startTime = System.currentTimeMillis();

		try {
			// 执行具体分支规则逻辑
			List<String> nextRules = process();

			// 记录执行时间
			if (getConfig().isEnableTimeMonitor()) {
				long cost = System.currentTimeMillis() - startTime;
				recordExecutionTime(ruleId, cost);
			}

			return nextRules;

		} catch (Exception e) {
			// 记录错误日志
			if (getConfig().isEnableLogging()) {
				log.error("Rule Switch execute error: {}", ruleId, e);
			}
			throw e;
		}
	}

	/**
	 * 记录执行时间
	 *
	 * @param ruleId 规则ID
	 * @param cost   执行耗时
	 */
	protected void recordExecutionTime(String ruleId, long cost) {
		RuleContext context = RuleContextManager.getContext();
		if (context instanceof RuleContextComponent) {
			((RuleContextComponent) context).recordExecutionTime(ruleId, cost);
		}

		if (getConfig().isPrintExecutionTime()) {
			log.info("Rule Switch [{}] execute cost: {}ms", ruleId, cost);
		}
	}

	/**
	 * 执行规则，由子类实现
	 *
	 * @return 下一个要执行的规则ID列表
	 * @throws Exception 执行过程中可能抛出的异常
	 */
	protected abstract List<String> process() throws Exception;
}
