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

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Locale解析工具类
 *
 * @author BladeX
 */
@Slf4j
@UtilityClass
public class LocaleParseUtil {

	/**
	 * Accept-Language header pattern
	 * 用于匹配和验证locale格式（如：zh-CN, en-US）
	 */
	private static final Pattern LOCALE_PATTERN = Pattern.compile("^[a-z]{2}(-[A-Z]{2})?$");

	/**
	 * 权重分隔符
	 */
	private static final String QUALITY_VALUE_SEPARATOR = ";";

	/**
	 * Locale分隔符
	 */
	private static final String LOCALE_SEPARATOR = ",";

	/**
	 * 下划线分隔符
	 */
	private static final String UNDERSCORE = "_";

	/**
	 * 中划线分隔符
	 */
	private static final String DASH = "-";

	/**
	 * 解析Locale字符串
	 * 支持多种格式：
	 * 1. 简单格式：zh_CN, zh-CN, en, en-US
	 * 2. Accept-Language格式：zh-CN,zh;q=0.9,en;q=0.8
	 * 3. 多个locale逗号分隔：zh-CN,en-US,ja-JP
	 *
	 * @param localeStr locale字符串
	 * @return 解析后的Locale对象，如果无法解析则返回默认Locale
	 */
	public static Locale parseLocale(String localeStr) {
		if (!StringUtils.hasText(localeStr)) {
			return Locale.getDefault();
		}

		try {
			// 提取第一个有效的locale
			String firstLocale = extractFirstLocale(localeStr);

			// 标准化locale格式（将下划线替换为中划线）
			String normalizedLocale = normalizeLocaleString(firstLocale);

			// 验证locale格式
			if (isValidLocaleFormat(normalizedLocale)) {
				return Locale.forLanguageTag(normalizedLocale);
			}

			// 如果格式不完全匹配，尝试直接解析
			return Locale.forLanguageTag(normalizedLocale);

		} catch (Exception e) {
			log.debug("Failed to parse locale string '{}', using default locale. Error: {}",
				localeStr, e.getMessage());
			return Locale.getDefault();
		}
	}

	/**
	 * 从Accept-Language或类似格式中提取第一个locale
	 * 处理格式如：zh-CN,zh;q=0.9,en;q=0.8
	 *
	 * @param localeStr 原始locale字符串
	 * @return 第一个locale字符串
	 */
	private static String extractFirstLocale(String localeStr) {
		// 去除首尾空格
		String trimmed = localeStr.trim();

		// 如果包含逗号，说明可能是多个locale
		if (trimmed.contains(LOCALE_SEPARATOR)) {
			// 获取第一个逗号前的内容
			trimmed = trimmed.substring(0, trimmed.indexOf(LOCALE_SEPARATOR)).trim();
		}

		// 如果包含分号（权重值），去除权重部分
		if (trimmed.contains(QUALITY_VALUE_SEPARATOR)) {
			trimmed = trimmed.substring(0, trimmed.indexOf(QUALITY_VALUE_SEPARATOR)).trim();
		}

		return trimmed;
	}

	/**
	 * 标准化locale字符串格式
	 * 将下划线替换为中划线，符合BCP 47标准
	 *
	 * @param localeStr locale字符串
	 * @return 标准化后的locale字符串
	 */
	private static String normalizeLocaleString(String localeStr) {
		if (!StringUtils.hasText(localeStr)) {
			return localeStr;
		}

		// 将下划线替换为中划线，统一格式
		return localeStr.replace(UNDERSCORE, DASH).trim();
	}

	/**
	 * 验证locale格式是否有效
	 * 支持格式：en, zh-CN, en-US等
	 *
	 * @param localeStr 标准化后的locale字符串
	 * @return 是否为有效格式
	 */
	private static boolean isValidLocaleFormat(String localeStr) {
		if (!StringUtils.hasText(localeStr)) {
			return false;
		}

		// 对于简单的语言代码（如：zh, en），直接认为有效
		if (localeStr.length() == 2 && localeStr.matches("^[a-z]{2}$")) {
			return true;
		}

		// 对于完整格式（如：zh-CN），进行格式验证
		// 转换为小写-大写格式进行验证
		String[] parts = localeStr.split(DASH);
		if (parts.length == 2) {
			String normalized = parts[0].toLowerCase() + DASH + parts[1].toUpperCase();
			return LOCALE_PATTERN.matcher(normalized).matches();
		}

		return false;
	}

	/**
	 * 安全地解析Locale，永不返回null
	 *
	 * @param localeStr     locale字符串
	 * @param defaultLocale 默认Locale
	 * @return 解析后的Locale对象，解析失败则返回defaultLocale
	 */
	public static Locale parseLocaleSafely(String localeStr, Locale defaultLocale) {
		if (defaultLocale == null) {
			defaultLocale = Locale.getDefault();
		}

		if (!StringUtils.hasText(localeStr)) {
			return defaultLocale;
		}

		try {
			Locale parsed = parseLocale(localeStr);
			return parsed != null ? parsed : defaultLocale;
		} catch (Exception e) {
			log.debug("Safe parse failed for locale '{}', returning default", localeStr);
			return defaultLocale;
		}
	}

	/**
	 * 从HttpServletRequest中解析Locale
	 * 优先级：Header > Parameter > Default
	 *
	 * @param request          HTTP请求对象
	 * @param headerName       Header参数名
	 * @param paramName        Query参数名
	 * @param supportedLocales 支持的Locale列表
	 * @param defaultLocale    默认Locale
	 * @return 解析后的Locale对象
	 */
	public static Locale resolveFromRequest(HttpServletRequest request,
											 String headerName,
											 String paramName,
											 Set<String> supportedLocales,
											 Locale defaultLocale) {
		if (request == null) {
			return defaultLocale != null ? defaultLocale : Locale.getDefault();
		}

		// 优先从Header中解析
		Locale locale = resolveFromHeader(request, headerName);

		// 如果Header中没有，从Parameter中解析
		if (locale == null) {
			locale = resolveFromParameter(request, paramName);
		}

		// 检查是否在支持列表中
		if (isSupported(locale, supportedLocales)) {
			return locale;
		}

		return defaultLocale != null ? defaultLocale : Locale.getDefault();
	}

	/**
	 * 从HTTP Header中解析Locale
	 *
	 * @param request    HTTP请求对象
	 * @param headerName Header参数名
	 * @return 解析后的Locale对象，如果无法解析则返回null
	 */
	public static Locale resolveFromHeader(HttpServletRequest request, String headerName) {
		if (request == null || !StringUtils.hasText(headerName)) {
			return null;
		}

		return Optional.ofNullable(request.getHeader(headerName))
			.filter(StringUtils::hasText)
			.map(LocaleParseUtil::parseLocale)
			.orElse(null);
	}

	/**
	 * 从请求参数中解析Locale
	 *
	 * @param request   HTTP请求对象
	 * @param paramName 参数名
	 * @return 解析后的Locale对象，如果无法解析则返回null
	 */
	public static Locale resolveFromParameter(HttpServletRequest request, String paramName) {
		if (request == null || !StringUtils.hasText(paramName)) {
			return null;
		}

		return Optional.ofNullable(request.getParameter(paramName))
			.filter(StringUtils::hasText)
			.map(LocaleParseUtil::parseLocale)
			.orElse(null);
	}

	/**
	 * 检查Locale是否在支持列表中
	 *
	 * @param locale           待检查的Locale
	 * @param supportedLocales 支持的Locale列表（小写）
	 * @return 是否支持
	 */
	public static boolean isSupported(Locale locale, Set<String> supportedLocales) {
		if (locale == null) {
			return false;
		}

		// 如果支持列表为空，表示支持所有locale
		if (supportedLocales == null || supportedLocales.isEmpty()) {
			return true;
		}

		String localeStr = locale.toString().toLowerCase();
		String language = locale.getLanguage().toLowerCase();

		// 检查完整locale或仅语言代码是否在支持列表中
		return supportedLocales.contains(localeStr) || supportedLocales.contains(language);
	}
}
