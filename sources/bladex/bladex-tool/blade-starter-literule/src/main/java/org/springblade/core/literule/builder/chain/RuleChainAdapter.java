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

import lombok.RequiredArgsConstructor;
import org.springblade.core.literule.provider.Rule;

/**
 * 规则链适配器
 * 将规则链包装为Rule接口的实现
 *
 * @author BladeX
 */
@RequiredArgsConstructor
public class RuleChainAdapter implements Rule {

	/**
	 * 规则链
	 */
	private final RuleChain ruleChain;

	/**
	 * 执行规则链
	 *
	 * @throws Exception 执行过程中可能抛出的异常
	 */
	@Override
	public void execute() throws Exception {
		ruleChain.execute();
	}
} 