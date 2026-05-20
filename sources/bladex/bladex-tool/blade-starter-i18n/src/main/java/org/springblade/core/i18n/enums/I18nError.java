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
 * 国际化错误消息枚举类
 * <p>
 * 包含系统中所有错误相关的国际化消息代码。
 * 通过实现 {@link I18nEnum} 接口，自动获得完整的国际化消息功能。
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 直接获取消息
 * String message = I18nError.USER_NAME_REQUIRED.getMessage();
 *
 * // 带参数获取消息
 * String sizeError = I18nError.USER_NAME_SIZE.getMessage(2, 50);
 *
 * // 指定Locale获取消息
 * String englishMessage = I18nError.ERROR_AUTH_UNAUTHORIZED.getMessage(Locale.ENGLISH);
 *
 * // 带参数和Locale获取消息
 * String permissionError = I18nError.ERROR_PERMISSION_DENIED.getMessage(
 *     new Object[]{"admin"}, Locale.CHINESE);
 *
 * // 使用静态方法
 * String errorMsg = I18nError.of("error.validation.default").getMessage();
 * }</pre>
 *
 * @author BladeX
 */
@Getter
@AllArgsConstructor
public enum I18nError implements I18nEnum {

	// ==================== 验证错误 ====================
	/**
	 * 参数校验失败
	 */
	ERROR_VALIDATION_DEFAULT("error.validation.default"),

	/**
	 * 请输入姓名
	 */
	USER_NAME_REQUIRED("user.name.required"),

	/**
	 * 姓名长度需在{min}到{max}个字符之间
	 */
	USER_NAME_SIZE("user.name.size"),

	/**
	 * 请输入有效的邮箱地址
	 */
	USER_EMAIL_INVALID("user.email.invalid"),

	/**
	 * 描述不能超过{max}个字符
	 */
	USER_DESCRIPTION_SIZE("user.description.size"),

	// ==================== 业务错误 ====================
	/**
	 * 不允许的操作：{0}
	 */
	ERROR_BIZ_DENIED("error.biz.denied"),

	/**
	 * 请先登录
	 */
	ERROR_AUTH_UNAUTHORIZED("error.auth.unauthorized"),

	/**
	 * 您没有访问{0}的权限
	 */
	ERROR_PERMISSION_DENIED("error.permission.denied"),

	/**
	 * {0} ID {1} 不存在
	 */
	ERROR_RESOURCE_NOTFOUND("error.resource.notfound"),

	/**
	 * 不能删除管理员用户
	 */
	ERROR_USER_DELETE_ADMIN("error.user.delete.admin"),

	/**
	 * 未知错误类型：{0}
	 */
	ERROR_UNKNOWN("error.unknown"),

	// ==================== 服务器错误 ====================
	/**
	 * 服务器内部错误，请稍后重试
	 */
	ERROR_SERVER_INTERNAL("error.server.internal"),

	/**
	 * 服务器繁忙，请稍后重试
	 */
	ERROR_SERVER_BUSY("error.server.busy"),

	/**
	 * 系统维护中
	 */
	ERROR_SERVER_MAINTENANCE("error.server.maintenance"),

	// ==================== 数据错误 ====================
	/**
	 * 数据已存在
	 */
	ERROR_DATA_DUPLICATE("error.data.duplicate"),

	/**
	 * 数据格式无效
	 */
	ERROR_DATA_INVALID("error.data.invalid"),

	/**
	 * 数据不存在
	 */
	ERROR_DATA_NOTFOUND("error.data.notfound");

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
	public static I18nError of(String code) {
		if (code == null || code.trim().isEmpty()) {
			return null;
		}
		for (I18nError error : values()) {
			if (error.getCode().equals(code)) {
				return error;
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
