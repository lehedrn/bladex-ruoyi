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
package org.springblade.core.i18n.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 国际化消息枚举类
 * <p>
 * 包含系统中所有业务相关的国际化消息代码。
 * 通过实现 {@link I18nEnum} 接口，自动获得完整的国际化消息功能。
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 直接获取消息
 * String message = I18nMessage.OPERATION_SUCCESS.getMessage();
 *
 * // 带参数获取消息
 * String createMsg = I18nMessage.USER_CREATE_SUCCESS.getMessage("张三");
 *
 * // 指定Locale获取消息
 * String englishMsg = I18nMessage.GREETING_WELCOME.getMessage(Locale.ENGLISH);
 *
 * // 带参数和Locale获取消息
 * String updateMsg = I18nMessage.USER_UPDATE_SUCCESS.getMessage(
 *     new Object[]{"李四", 123}, Locale.CHINESE);
 *
 * // 使用静态方法
 * String msg = I18nMessage.of("greeting.welcome").getMessage();
 * }</pre>
 *
 * @author BladeX
 */
@Getter
@AllArgsConstructor
public enum I18nMessage implements I18nEnum {

	// ==================== 问候消息 ====================
	/**
	 * 你好，{0}！
	 */
	GREETING_MESSAGE("greeting.message"),

	/**
	 * 欢迎使用我们的应用
	 */
	GREETING_WELCOME("greeting.welcome"),

	// ==================== 用户消息 ====================
	/**
	 * 用户 {0} 创建成功
	 */
	USER_CREATE_SUCCESS("user.create.success"),

	/**
	 * ID为 {1} 的用户 {0} 更新成功
	 */
	USER_UPDATE_SUCCESS("user.update.success"),

	/**
	 * ID为 {0} 的用户删除成功
	 */
	USER_DELETE_SUCCESS("user.delete.success"),

	/**
	 * 用户未找到
	 */
	USER_NOTFOUND("user.notfound"),

	// ==================== 成功消息 ====================
	/**
	 * 操作成功完成
	 */
	OPERATION_SUCCESS("operation.success"),

	/**
	 * 数据保存成功
	 */
	SAVE_SUCCESS("save.success"),

	/**
	 * 数据删除成功
	 */
	DELETE_SUCCESS("delete.success");

	/**
	 * 国际化消息代码
	 */
	private final String code;

	// ==================== 静态工具方法 ====================

	/**
	 * 根据消息代码创建枚举实例的静态工厂方法
	 * <p>
	 * 用于动态创建枚举实例，适用于消息代码在运行时确定的场景
	 * </p>
	 *
	 * @param code 消息代码
	 * @return 匹配的枚举实例，如果没有匹配则返回null
	 */
	public static I18nMessage of(String code) {
		if (code == null || code.trim().isEmpty()) {
			return null;
		}
		for (I18nMessage message : values()) {
			if (message.getCode().equals(code)) {
				return message;
			}
		}
		return null;
	}

	/**
	 * 返回国际化消息
	 * <p>
	 * 重写toString方法，使枚举实例可以直接在字符串拼接或日志输出时
	 * 自动显示国际化后的消息内容。
	 * </p>
	 *
	 * @return 当前语言环境下的国际化消息
	 */
	@Override
	public String toString() {
		return getMessage();
	}
}
