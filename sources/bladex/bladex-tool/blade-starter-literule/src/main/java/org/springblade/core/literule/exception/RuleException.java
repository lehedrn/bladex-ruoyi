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
package org.springblade.core.literule.exception;

import java.io.Serial;

/**
 * 规则引擎异常类
 * 用于规则执行过程中的异常处理
 *
 * @author BladeX
 */
public class RuleException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 构造函数
	 *
	 * @param message 错误消息
	 */
	public RuleException(String message) {
		super(message);
	}

	/**
	 * 构造函数
	 *
	 * @param message 错误消息
	 * @param cause   原始异常
	 */
	public RuleException(String message, Throwable cause) {
		super(message, cause);
	}
}
