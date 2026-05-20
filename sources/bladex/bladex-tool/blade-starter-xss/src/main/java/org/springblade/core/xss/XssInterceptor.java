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
 * Author: DreamLu (596392912@qq.com)
 */

package org.springblade.core.xss;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springblade.core.tool.utils.ClassUtil;
import org.springblade.core.xss.annotation.XssIgnore;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import java.util.List;

/**
 * xss 处理拦截器
 *
 * @author L.cm
 */
@RequiredArgsConstructor
public class XssInterceptor implements AsyncHandlerInterceptor {
	private final PathMatcher matcher = new AntPathMatcher();
	private final BladeXssProperties xssProperties;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 1. 非控制器请求直接跳出
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}
		// 2. 没有开启
		if (!xssProperties.isEnabled()) {
			return true;
		}
		// 判断是否需要跳过
		List<String> skipUrl = xssProperties.getSkipUrl();
		String requestURL = request.getRequestURI();
		boolean needExclude = skipUrl.stream()
			.anyMatch(pattern -> matcher.match(pattern, requestURL));
		if (needExclude) {
			XssHolder.setIgnore(new XssIgnoreRules());
			return true;
		}
		// 3. 处理 XssIgnore 注解
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		XssIgnore xssIgnore = ClassUtil.getAnnotation(handlerMethod, XssIgnore.class);
		if (xssIgnore != null) {
			XssHolder.setIgnore(new XssIgnoreRules(xssIgnore.value()));
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		XssHolder.remove();
	}

	@Override
	public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		XssHolder.remove();
	}
}
