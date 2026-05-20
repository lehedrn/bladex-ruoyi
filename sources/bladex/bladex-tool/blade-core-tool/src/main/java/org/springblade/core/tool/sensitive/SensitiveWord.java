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
package org.springblade.core.tool.sensitive;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 敏感词分组枚举.
 *
 * @author BladeX
 */
@Getter
public enum SensitiveWord {

	// 空敏感词组
	NONE(List.of()),

	// 安全敏感词组
	SECURE(Arrays.asList(
		// 认证信息类
		"password", "pwd", "token", "secret", "bearer", "key",
		// API相关
		"api_key", "access_token", "refresh_token", "auth_token",
		// 加密相关
		"private_key", "public_key", "salt", "hash",
		// 安全相关
		"security", "certificate", "credentials",
		// 数据库相关
		"connection_string", "jdbc", "sql", "database_url"
	)),

	// 支付相关敏感词
	PAYMENT(Arrays.asList(
		"cvv", "card_number", "expiry", "pin", "payment_token"
	)),

	// 身份验证相关敏感词
	AUTHENTICATION(Arrays.asList(
		"otp", "verification_code", "auth_code", "mfa_token"
	)),

	// 会话相关敏感词
	SESSION(Arrays.asList(
		"session_id", "cookie", "jwt_token", "bearer_token"
	));

	private final List<String> words;

	SensitiveWord(List<String> words) {
		this.words = Collections.unmodifiableList(words);
	}
}
