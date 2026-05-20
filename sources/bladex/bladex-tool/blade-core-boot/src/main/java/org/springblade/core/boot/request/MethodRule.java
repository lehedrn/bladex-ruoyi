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

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求方法规则配置
 *
 * @author Chill
 */
@Data
public class MethodRule {

	/**
	 * 匹配路径模式，支持Ant风格，例如：/api/**, /admin/*
	 */
	private String pattern;

	/**
	 * 允许的请求方法列表
	 */
	private List<HttpMethod> allowMethods = new ArrayList<>();

}
