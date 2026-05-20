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
package org.springblade.core.i18n.resolver;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.i18n.props.I18nProperties;
import org.springblade.core.i18n.utils.LocaleParseUtil;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * I18nLocale解析器
 *
 * @author BladeX
 */
@Slf4j
@RequiredArgsConstructor
public class I18nLocaleResolver implements LocaleResolver {

	private final I18nProperties properties;
	private final Locale defaultLocale;
	private final Set<String> supportedLocales;

	public I18nLocaleResolver(I18nProperties properties) {
		this.properties = properties;
		this.defaultLocale = LocaleParseUtil.parseLocale(properties.getDefaultLocale());
		this.supportedLocales = properties.getSupportLocales().stream()
			.map(String::toLowerCase)
			.collect(Collectors.toSet());
	}

	@Nonnull
	@Override
	public Locale resolveLocale(@Nonnull HttpServletRequest request) {
		return LocaleParseUtil.resolveFromRequest(
			request,
			properties.getHeaderName(),
			properties.getParamName(),
			supportedLocales,
			defaultLocale
		);
	}

	@Override
	public void setLocale(@Nonnull HttpServletRequest request, HttpServletResponse response, Locale locale) {
		if (response != null && locale != null) {
			// 检查是否支持该locale
			boolean isSupported = supportedLocales.isEmpty() ||
				supportedLocales.contains(locale.toString().toLowerCase()) ||
				supportedLocales.contains(locale.getLanguage().toLowerCase());

			if (isSupported) {
				// 可选：设置响应头提示客户端当前的 Locale
				response.setHeader(properties.getHeaderName(), locale.toLanguageTag());
			}
		}
	}
}
