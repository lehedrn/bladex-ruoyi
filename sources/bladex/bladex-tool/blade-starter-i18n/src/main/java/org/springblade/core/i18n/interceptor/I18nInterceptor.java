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
package org.springblade.core.i18n.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.i18n.props.I18nProperties;
import org.springblade.core.i18n.utils.LocaleParseUtil;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * I18n拦截器
 *
 * @author BladeX
 */
@Slf4j
@RequiredArgsConstructor
public class I18nInterceptor implements HandlerInterceptor {

	private static final String LOCALE_ATTRIBUTE = "locale";
	private static final String LANG_ATTRIBUTE = "lang";
	private static final String CONTENT_LANGUAGE_HEADER = "Content-Language";

	private final I18nProperties properties;
	private final Locale defaultLocale;
	private final Set<String> supportedLocales;

	public I18nInterceptor(I18nProperties properties) {
		this.properties = properties;
		this.defaultLocale = LocaleParseUtil.parseLocale(properties.getDefaultLocale());
		this.supportedLocales = properties.getSupportLocales().stream()
			.map(String::toLowerCase)
			.collect(Collectors.toSet());
	}

	/**
	 * 请求处理前设置Locale
	 *
	 * @param request  请求
	 * @param response 响应
	 * @param handler  处理器
	 * @return 是否继续处理
	 */
	@Override
	public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
		try {
			// 解析Locale
			Locale locale = LocaleParseUtil.resolveFromRequest(
				request,
				properties.getHeaderName(),
				properties.getParamName(),
				supportedLocales,
				defaultLocale
			);
			// 设置到LocaleContextHolder
			LocaleContextHolder.setLocale(locale);
			// 设置到请求属性
			request.setAttribute(LOCALE_ATTRIBUTE, locale);
			request.setAttribute(LANG_ATTRIBUTE, locale.toString());
			// 添加响应头，告知客户端当前使用的语言
			response.setHeader(CONTENT_LANGUAGE_HEADER, locale.toLanguageTag());
		} catch (Exception e) {
			// 设置默认Locale
			LocaleContextHolder.setLocale(this.defaultLocale);
		}
		return true;
	}

	/**
	 * 请求完成后清理Locale上下文
	 *
	 * @param request  请求
	 * @param response 响应
	 * @param handler  处理器
	 * @param ex       异常
	 */
	@Override
	public void afterCompletion(@NonNull HttpServletRequest request,
								@NonNull HttpServletResponse response,
								@NonNull Object handler,
								Exception ex) {
		// 清理LocaleContextHolder
		LocaleContextHolder.resetLocaleContext();
		// 清理请求属性
		request.removeAttribute(LOCALE_ATTRIBUTE);
		request.removeAttribute(LANG_ATTRIBUTE);
	}

}
