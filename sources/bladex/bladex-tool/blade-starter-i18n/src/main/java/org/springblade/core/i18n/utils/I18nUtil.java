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
package org.springblade.core.i18n.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.i18n.service.I18nService;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringPool;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * I18n静态工具类
 *
 * @author BladeX
 */
@Slf4j
@UtilityClass
public class I18nUtil {

	/**
	 * I18nService服务类
	 */
	private static volatile I18nService i18nService;

	/**
	 * 获取I18nService实例
	 *
	 * @return I18nService实例
	 */
	private static I18nService getI18nService() {
		if (i18nService == null) {
			synchronized (I18nUtil.class) {
				if (i18nService == null) {
					i18nService = SpringUtil.getBean(I18nService.class);
					if (i18nService == null) {
						throw new IllegalStateException("I18nService not available. Please ensure I18n is properly configured.");
					}
				}
			}
		}
		return i18nService;
	}

	// ==================== 基础获取方法 ====================

	/**
	 * 获取消息（最常用）
	 *
	 * @param code 消息代码
	 * @return 国际化消息
	 */
	public static String get(String code) {
		return Optional.ofNullable(code)
			.map(c -> getI18nService().getMessage(c))
			.orElse(StringPool.EMPTY);
	}

	/**
	 * 获取消息，带参数
	 *
	 * @param code 消息代码
	 * @param args 消息参数数组
	 * @return 国际化消息
	 */
	public static String get(String code, Object[] args) {
		return Optional.ofNullable(code)
			.map(c -> getI18nService().getMessage(c, args))
			.orElse(StringPool.EMPTY);
	}

	/**
	 * 获取消息，带参数（List形式）
	 *
	 * @param code 消息代码
	 * @param args 消息参数列表
	 * @return 国际化消息
	 */
	public static String get(String code, List<Object> args) {
		return Optional.ofNullable(code)
			.map(c -> getI18nService().getMessage(c, args != null ? args.toArray() : null))
			.orElse(StringPool.EMPTY);
	}

	// ==================== 指定Locale的获取方法 ====================

	/**
	 * 获取消息，指定Locale
	 *
	 * @param code   消息代码
	 * @param locale 区域设置
	 * @return 国际化消息
	 */
	public static String get(String code, Locale locale) {
		return Optional.ofNullable(code)
			.map(c -> getI18nService().getMessage(c, null, null, locale))
			.orElse(StringPool.EMPTY);
	}

	/**
	 * 获取消息，带参数和Locale
	 *
	 * @param code   消息代码
	 * @param args   消息参数数组
	 * @param locale 区域设置
	 * @return 国际化消息
	 */
	public static String get(String code, Object[] args, Locale locale) {
		return Optional.ofNullable(code)
			.map(c -> getI18nService().getMessage(c, args, null, locale))
			.orElse(StringPool.EMPTY);
	}

	/**
	 * 获取消息，带参数和Locale（List形式）
	 *
	 * @param code   消息代码
	 * @param args   消息参数列表
	 * @param locale 区域设置
	 * @return 国际化消息
	 */
	public static String get(String code, List<Object> args, Locale locale) {
		return Optional.ofNullable(code)
			.map(c -> getI18nService().getMessage(c, args != null ? args.toArray() : null, null, locale))
			.orElse(StringPool.EMPTY);
	}

	// ==================== 带默认值的获取方法 ====================

	/**
	 * 获取消息，如果未找到则返回默认值
	 *
	 * @param code         消息代码
	 * @param defaultValue 默认值
	 * @return 国际化消息或默认值
	 */
	public static String getOrDefault(String code, String defaultValue) {
		return Optional.ofNullable(code)
			.map(c -> {
				String message = getI18nService().getMessage(c);
				return (message != null && !message.equals(c)) ? message : defaultValue;
			})
			.orElse(defaultValue);
	}

	/**
	 * 获取消息，如果未找到则返回默认值，带参数
	 *
	 * @param code         消息代码
	 * @param args         消息参数数组
	 * @param defaultValue 默认值
	 * @return 国际化消息或默认值
	 */
	public static String getOrDefault(String code, Object[] args, String defaultValue) {
		return Optional.ofNullable(code)
			.map(c -> {
				String message = getI18nService().getMessage(c, args);
				return (message != null && !message.equals(c)) ? message : defaultValue;
			})
			.orElse(defaultValue);
	}

	/**
	 * 获取消息，如果未找到则返回默认值，带参数（List形式）
	 *
	 * @param code         消息代码
	 * @param args         消息参数列表
	 * @param defaultValue 默认值
	 * @return 国际化消息或默认值
	 */
	public static String getOrDefault(String code, List<Object> args, String defaultValue) {
		return Optional.ofNullable(code)
			.map(c -> {
				String message = getI18nService().getMessage(c, args != null ? args.toArray() : null);
				return (message != null && !message.equals(c)) ? message : defaultValue;
			})
			.orElse(defaultValue);
	}

	/**
	 * 获取消息，如果未找到则返回默认值，指定Locale
	 *
	 * @param code         消息代码
	 * @param locale       区域设置
	 * @param defaultValue 默认值
	 * @return 国际化消息或默认值
	 */
	public static String getOrDefault(String code, Locale locale, String defaultValue) {
		return Optional.ofNullable(code)
			.map(c -> {
				String message = getI18nService().getMessage(c, null, null, locale);
				return (message != null && !message.equals(c)) ? message : defaultValue;
			})
			.orElse(defaultValue);
	}

	/**
	 * 获取消息，如果未找到则返回默认值，带参数和Locale
	 *
	 * @param code         消息代码
	 * @param args         消息参数数组
	 * @param locale       区域设置
	 * @param defaultValue 默认值
	 * @return 国际化消息或默认值
	 */
	public static String getOrDefault(String code, Object[] args, Locale locale, String defaultValue) {
		return Optional.ofNullable(code)
			.map(c -> {
				String message = getI18nService().getMessage(c, args, null, locale);
				return (message != null && !message.equals(c)) ? message : defaultValue;
			})
			.orElse(defaultValue);
	}

	/**
	 * 获取消息，如果未找到则返回默认值，带参数和Locale（List形式）
	 *
	 * @param code         消息代码
	 * @param args         消息参数列表
	 * @param locale       区域设置
	 * @param defaultValue 默认值
	 * @return 国际化消息或默认值
	 */
	public static String getOrDefault(String code, List<Object> args, Locale locale, String defaultValue) {
		return Optional.ofNullable(code)
			.map(c -> {
				String message = getI18nService().getMessage(c, args != null ? args.toArray() : null, null, locale);
				return (message != null && !message.equals(c)) ? message : defaultValue;
			})
			.orElse(defaultValue);
	}

	// ==================== 检查方法 ====================

	/**
	 * 检查消息是否存在
	 *
	 * @param code 消息代码
	 * @return 如果消息存在返回true，否则返回false
	 */
	public static boolean exists(String code) {
		if (code == null || code.trim().isEmpty()) {
			return false;
		}
		return getI18nService().hasMessage(code);
	}

	/**
	 * 检查消息是否存在，指定Locale
	 *
	 * @param code   消息代码
	 * @param locale 区域设置
	 * @return 如果消息存在返回true，否则返回false
	 */
	public static boolean exists(String code, Locale locale) {
		if (code == null || code.trim().isEmpty()) {
			return false;
		}
		return getI18nService().hasMessage(code, locale);
	}

	// ==================== 批量获取方法 ====================

	/**
	 * 批量获取消息
	 *
	 * @param codes 消息代码数组
	 * @return 消息代码与消息内容的映射
	 */
	public static Map<String, String> getBatch(String[] codes) {
		return getI18nService().getMessages(codes);
	}

	/**
	 * 批量获取消息，指定Locale
	 *
	 * @param codes  消息代码数组
	 * @param locale 区域设置
	 * @return 消息代码与消息内容的映射
	 */
	public static Map<String, String> getBatch(String[] codes, Locale locale) {
		return getI18nService().getMessages(locale, codes);
	}

	/**
	 * 批量获取消息（List形式）
	 *
	 * @param codes 消息代码列表
	 * @return 消息代码与消息内容的映射
	 */
	public static Map<String, String> getBatch(List<String> codes) {
		return getI18nService().getMessages(codes != null ? codes.toArray(new String[0]) : null);
	}

	/**
	 * 批量获取消息，指定Locale（List形式）
	 *
	 * @param codes  消息代码列表
	 * @param locale 区域设置
	 * @return 消息代码与消息内容的映射
	 */
	public static Map<String, String> getBatch(List<String> codes, Locale locale) {
		return getI18nService().getMessages(locale, codes != null ? codes.toArray(new String[0]) : null);
	}

	// ==================== 工具方法 ====================

	/**
	 * 获取当前Locale
	 *
	 * @return 当前区域设置
	 */
	public static Locale getCurrentLocale() {
		return getI18nService().getCurrentLocale();
	}

	// ==================== 简化方法 ====================

	/**
	 * 简化的消息获取方法
	 *
	 * @param code 消息代码
	 * @return 国际化消息
	 */
	public static String $(String code) {
		return get(code);
	}

	/**
	 * 简化的消息获取方法，带参数
	 *
	 * @param code 消息代码
	 * @param args 消息参数数组
	 * @return 国际化消息
	 */
	public static String $(String code, Object[] args) {
		return get(code, args);
	}

	/**
	 * 简化的消息获取方法，带参数（List形式）
	 *
	 * @param code 消息代码
	 * @param args 消息参数列表
	 * @return 国际化消息
	 */
	public static String $(String code, List<Object> args) {
		return get(code, args);
	}

	/**
	 * 简化的消息获取方法，指定Locale
	 *
	 * @param code   消息代码
	 * @param locale 区域设置
	 * @return 国际化消息
	 */
	public static String $(String code, Locale locale) {
		return get(code, locale);
	}

	/**
	 * 简化的消息获取方法，带参数和Locale
	 *
	 * @param code   消息代码
	 * @param args   消息参数数组
	 * @param locale 区域设置
	 * @return 国际化消息
	 */
	public static String $(String code, Object[] args, Locale locale) {
		return get(code, args, locale);
	}

	/**
	 * 简化的消息获取方法，带参数和Locale（List形式）
	 *
	 * @param code   消息代码
	 * @param args   消息参数列表
	 * @param locale 区域设置
	 * @return 国际化消息
	 */
	public static String $(String code, List<Object> args, Locale locale) {
		return get(code, args, locale);
	}
}
