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
package org.springblade.core.boot.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * HTTP请求方法枚举
 *
 * @author Chill
 */
@Getter
@RequiredArgsConstructor
public enum HttpMethod {

	/**
	 * GET请求
	 */
	GET("GET"),

	/**
	 * POST请求
	 */
	POST("POST"),

	/**
	 * PUT请求
	 */
	PUT("PUT"),

	/**
	 * DELETE请求
	 */
	DELETE("DELETE"),

	/**
	 * PATCH请求
	 */
	PATCH("PATCH"),

	/**
	 * HEAD请求
	 */
	HEAD("HEAD"),

	/**
	 * OPTIONS请求
	 */
	OPTIONS("OPTIONS"),

	/**
	 * TRACE请求
	 */
	TRACE("TRACE");

	/**
	 * 方法名称
	 */
	private final String name;

	/**
	 * 根据方法名称解析枚举
	 *
	 * @param method 方法名称
	 * @return HttpMethod
	 */
	public static HttpMethod resolve(String method) {
		if (method == null) {
			return null;
		}
		for (HttpMethod httpMethod : values()) {
			if (httpMethod.name.equalsIgnoreCase(method)) {
				return httpMethod;
			}
		}
		return null;
	}

	/**
	 * 判断是否匹配
	 *
	 * @param method 方法名称
	 * @return boolean
	 */
	public boolean matches(String method) {
		return this.name.equalsIgnoreCase(method);
	}

}
