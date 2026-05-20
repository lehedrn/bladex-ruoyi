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
package org.springblade.core.literule.builder;

import org.springblade.core.literule.builder.chain.WhenRuleChain;
import org.springblade.core.literule.builder.chain.SwitchRuleChain;
import org.springblade.core.literule.builder.chain.ThenRuleChain;

/**
 * 规则构建器
 * 提供流式API用于构建规则链
 *
 * @author BladeX
 */
public class LiteRule {

	/**
	 * 创建顺序规则链
	 *
	 * @param ruleIds 规则ID数组
	 * @return 顺序规则链构建器
	 */
	public static ThenRuleChain THEN(String... ruleIds) {
		return new ThenRuleChain().THEN(ruleIds);
	}

	/**
	 * 创建分支规则链
	 *
	 * @param conditionRuleId 条件规则ID
	 * @return 分支规则链构建器
	 */
	public static SwitchRuleChain SWITCH(String conditionRuleId) {
		return new SwitchRuleChain(conditionRuleId);
	}

	/**
	 * 创建并行规则链
	 *
	 * @param ruleIds 规则ID数组
	 * @return 并行规则链构建器
	 */
	public static WhenRuleChain WHEN(String... ruleIds) {
		return new WhenRuleChain().WHEN(ruleIds);
	}
}
