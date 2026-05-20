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
 * Author: DreamLu (596392912@qq.com)
 */

package org.springblade.core.xss;


import org.springblade.core.tool.utils.Exceptions;
import org.springblade.core.xss.exception.FromXssException;
import org.springblade.core.xss.exception.JacksonXssException;

/**
 * xss 数据处理类型
 */
public enum XssType {

	/**
	 * 表单
	 */
	FORM() {
		@Override
		public RuntimeException getXssException(String name, String input, String message) {
			return new FromXssException(input, message);
		}
	},

	/**
	 * body json
	 */
	JACKSON() {
		@Override
		public RuntimeException getXssException(String name, String input, String message) {
			return Exceptions.unchecked(new JacksonXssException(name, input, message));
		}
	};

	/**
	 * 获取 xss 异常
	 *
	 * @param name    属性名
	 * @param input   input
	 * @param message message
	 * @return XssException
	 */
	public abstract RuntimeException getXssException(String name, String input, String message);

}
