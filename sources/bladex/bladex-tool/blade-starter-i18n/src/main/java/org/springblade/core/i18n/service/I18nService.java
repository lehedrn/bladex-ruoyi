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
package org.springblade.core.i18n.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.i18n.props.I18nProperties;
import org.springblade.core.i18n.utils.LocaleParseUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * I18n服务实现类
 *
 * @author BladeX
 */
@Slf4j
@RequiredArgsConstructor
public class I18nService {

	private final MessageSource messageSource;
	private final LocaleResolver localeResolver;
	private final I18nProperties properties;

	/**
	 * 获取消息 - 核心方法
	 *
	 * @param code 消息代码
	 * @return 国际化消息
	 */
	public String getMessage(String code) {
		return getMessage(code, null, null, getCurrentLocale());
	}

	/**
	 * 获取消息
	 *
	 * @param code 消息代码
	 * @param args 消息参数
	 * @return 国际化消息
	 */
	public String getMessage(String code, Object[] args) {
		return getMessage(code, args, null, getCurrentLocale());
	}

	/**
	 * 获取消息，带默认值
	 *
	 * @param code           消息代码
	 * @param args           消息参数
	 * @param defaultMessage 默认消息
	 * @return 国际化消息
	 */
	public String getMessage(String code, Object[] args, String defaultMessage) {
		return getMessage(code, args, defaultMessage, getCurrentLocale());
	}

	/**
	 * 获取消息 - 完整版本
	 *
	 * @param code           消息代码
	 * @param args           消息参数
	 * @param defaultMessage 默认消息
	 * @param locale         区域设置
	 * @return 国际化消息
	 */
	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		if (StringUtil.isBlank(code)) {
			return defaultMessage != null ? defaultMessage : StringPool.EMPTY;
		}

		final Locale finalLocale = Optional.ofNullable(locale).orElseGet(this::getCurrentLocale);

		try {
			return messageSource.getMessage(code, args, finalLocale);
		} catch (NoSuchMessageException e) {
			if (log.isDebugEnabled()) {
				log.debug("No message found for code '{}' with locale '{}'", code, finalLocale);
			}
			if (defaultMessage != null) {
				return defaultMessage;
			}
			return properties.getMessageSource().isUseCodeAsDefaultMessage() ? code : StringPool.EMPTY;
		} catch (Exception e) {
			log.error("Error retrieving message for code '{}': {}", code, e.getMessage());
			return defaultMessage != null ? defaultMessage : code;
		}
	}

	/**
	 * 使用MessageSourceResolvable获取消息
	 *
	 * @param resolvable MessageSourceResolvable对象
	 * @param locale     区域设置
	 * @return 国际化消息
	 */
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) {
		final Locale finalLocale = Optional.ofNullable(locale).orElseGet(this::getCurrentLocale);
		try {
			return messageSource.getMessage(resolvable, finalLocale);
		} catch (NoSuchMessageException e) {
			log.debug("No message found for resolvable with locale '{}'", finalLocale);
			return resolvable.getDefaultMessage() != null ? resolvable.getDefaultMessage() : "";
		}
	}

	/**
	 * 批量获取消息
	 *
	 * @param codes 消息代码数组
	 * @return 消息映射
	 */
	public Map<String, String> getMessages(String... codes) {
		return getMessages(getCurrentLocale(), codes);
	}

	/**
	 * 批量获取消息，指定Locale
	 *
	 * @param locale 区域设置
	 * @param codes  消息代码数组
	 * @return 消息映射
	 */
	public Map<String, String> getMessages(Locale locale, String... codes) {
		if (codes == null || codes.length == 0) {
			return Collections.emptyMap();
		}

		final Locale finalLocale = Optional.ofNullable(locale).orElseGet(this::getCurrentLocale);
		return Stream.of(codes)
			.distinct()
			.collect(Collectors.toMap(
				code -> code,
				code -> getMessage(code, null, null, finalLocale),
				(v1, v2) -> v1,
				LinkedHashMap::new
			));
	}

	/**
	 * 向后兼容的getBatch方法
	 */
	public Map<String, String> getBatch(String... codes) {
		return getMessages(codes);
	}

	/**
	 * 获取当前Locale
	 *
	 * @return 当前区域设置
	 */
	public Locale getCurrentLocale() {
		return LocaleContextHolder.getLocale();
	}

	/**
	 * 获取当前Locale，从HttpServletRequest解析
	 *
	 * @param request HTTP请求对象
	 * @return 当前区域设置
	 */
	public Locale getCurrentLocale(HttpServletRequest request) {
		if (request == null) {
			return getCurrentLocale();
		}
		return localeResolver.resolveLocale(request);
	}

	/**
	 * 检查消息是否存在
	 *
	 * @param code 消息代码
	 * @return 如果消息存在返回true，否则返回false
	 */
	public boolean hasMessage(String code) {
		return hasMessage(code, getCurrentLocale());
	}

	/**
	 * 检查消息是否存在，指定Locale
	 *
	 * @param code   消息代码
	 * @param locale 区域设置
	 * @return 如果消息存在返回true，否则返回false
	 */
	public boolean hasMessage(String code, Locale locale) {
		if (!StringUtils.hasText(code)) {
			return false;
		}

		final Locale finalLocale = Optional.ofNullable(locale).orElseGet(this::getCurrentLocale);

		try {
			messageSource.getMessage(code, null, finalLocale);
			return true;
		} catch (NoSuchMessageException e) {
			return false;
		}
	}

	/**
	 * 获取支持的Locales
	 *
	 * @return 支持的区域设置列表
	 */
	public List<Locale> getSupportLocales() {
		List<String> supportedLocaleStrings = properties.getSupportLocales();
		if (supportedLocaleStrings == null || supportedLocaleStrings.isEmpty()) {
			return Collections.singletonList(LocaleParseUtil.parseLocale(properties.getDefaultLocale()));
		}

		return supportedLocaleStrings.stream()
			.map(LocaleParseUtil::parseLocale)
			.filter(Objects::nonNull)
			.distinct()
			.collect(Collectors.toList());
	}

}
