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

import java.util.regex.Pattern;

/**
 * 脱敏类型枚举.
 *
 * @author BladeX
 */
public enum SensitiveType {

	// 空敏感类型
	NONE("无", "", ""),

	// 通用信息类
	GLOBAL("全局", "(.{2}).*(.{2})", "$1****$2"),
	KEYS("密钥", "(.{3}).*(.{3})", "$1************$2"),

	// 身份信息类
	MOBILE("手机号", "(\\d{3})\\d{4}(\\d{4})", "$1****$2"),
	EMAIL("电子邮箱", "(\\w{2})\\w+(@\\w+\\.\\w+)", "$1****$2"),
	ID_CARD("身份证号", "(\\d{4})\\d{10}(\\w{4})", "$1**********$2"),
	PASSPORT("护照号", "([A-Z]{1})\\d{7}", "$1*******"),

	// 金融信息类
	BANK_CARD("银行卡号", "(\\d{4})\\d+(\\d{4})", "$1****$2"),
	CREDIT_CARD("信用卡号", "(\\d{4})\\d+(\\d{4})", "$1****$2"),

	// 账户信息类
	USERNAME("用户名", "(\\w{1})\\w+(\\w{1})", "$1****$2"),
	IP_ADDRESS("IP地址", "(\\d{1,3}\\.\\d{1,3})\\.\\d{1,3}\\.\\d{1,3}", "$1.***.***"),
	MAC_ADDRESS("MAC地址", "([0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}):[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}", "$1:****"),

	// 地址信息类
	ADDRESS("详细地址", "(.{3}).*(.{3})", "$1****$2"),
	GPS("GPS坐标", "(\\d+\\.\\d{2})\\d+,(\\d+\\.\\d{2})\\d+", "$1***,$2***"),
	;

	private final String replacement;
	private final Pattern pattern;

	SensitiveType(String ignore, String regex, String replacement) {
		this.replacement = replacement;
		this.pattern = Pattern.compile(regex);
	}

	/**
	 * 替换文本
	 *
	 * @param content content
	 * @return 替换后的内容
	 */
	public String replaceAll(String content) {
		return this.pattern.matcher(content).replaceAll(this.replacement);
	}
}
