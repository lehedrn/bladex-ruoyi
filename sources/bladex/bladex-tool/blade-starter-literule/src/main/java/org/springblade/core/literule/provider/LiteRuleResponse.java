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
package org.springblade.core.literule.provider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 规则引擎响应类
 * 封装规则执行的结果信息
 *
 * @param <T> 规则上下文类型
 * @author BladeX
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiteRuleResponse<T extends RuleContext> {
	/**
	 * 是否执行成功
	 */
	private boolean success;

	/**
	 * 错误消息
	 */
	private String message;

	/**
	 * 规则上下文
	 */
	private T context;

	/**
	 * 执行耗时(毫秒)
	 */
	private long executionTime;
}
