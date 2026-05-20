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

package org.springblade.core.redis.debounce;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 防抖异常处理器
 *
 * @author BladeX
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@RestControllerAdvice
public class DebounceExceptionTranslator {

	/**
	 * 处理防抖异常
	 */
	@ExceptionHandler(DebounceException.class)
	@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
	public R<String> handleError(DebounceException e) {
		DebounceLogBuilder logBuilder = DebounceLogBuilder.create()
			.debounceKey(e.getDebounceKey())
			.message(e.getMessage());

		if (hasRemainingTimeInfo(e)) {
			logBuilder.withRemainingTime(e.getRemainingTime(), e.getTimeUnit())
				.remainingSeconds(e.getRemainingSeconds())
				.remainingMillis(e.getRemainingMillis());
		}

		logBuilder.withRequestInfo().build().log();
		return R.fail(HttpStatus.TOO_MANY_REQUESTS.value(), e.getMessage());
	}

	/**
	 * 判断异常是否包含剩余时间信息
	 */
	private boolean hasRemainingTimeInfo(DebounceException e) {
		return e.getRemainingTime() > 0 && e.getTimeUnit() != null;
	}

	/**
	 * 防抖日志构建器
	 */
	private static class DebounceLogBuilder {
		private final StringBuilder template = new StringBuilder("防抖异常 - 操作过于频繁:");
		private final List<Object> parameters = new ArrayList<>();

		private DebounceLogBuilder() {
		}

		public static DebounceLogBuilder create() {
			return new DebounceLogBuilder();
		}

		public DebounceLogBuilder debounceKey(String key) {
			template.append("\n  防抖键值: {}");
			parameters.add(key);
			return this;
		}

		public DebounceLogBuilder withRemainingTime(long remainingTime, TimeUnit timeUnit) {
			template.append("\n  剩余时间: {} {}");
			parameters.add(remainingTime);
			parameters.add(timeUnit.name().toLowerCase());
			return this;
		}

		public DebounceLogBuilder remainingSeconds(long seconds) {
			template.append("\n  剩余秒数: {} 秒");
			parameters.add(seconds);
			return this;
		}

		public void remainingMillis(long millis) {
			template.append("\n  剩余毫秒: {} 毫秒");
			parameters.add(millis);
		}

		public DebounceLogBuilder message(String message) {
			template.append("\n  异常消息: {}");
			parameters.add(message);
			return this;
		}

		public DebounceLogBuilder withRequestInfo() {
			HttpServletRequest request = WebUtil.getRequest();
			if (request != null) {
				appendRequestUrl(request);
				appendRequestMethod(request);
				appendRequestParameter(request);
				appendClientIp(request);
				appendRequestUser(request);
				appendRequestUserId(request);
			}
			return this;
		}

		private void appendRequestUrl(HttpServletRequest request) {
			template.append("\n  请求URL: {}");
			String requestUrl = request.getRequestURL().toString();
			String queryString = request.getQueryString();
			String fullUrl = queryString != null && !queryString.isEmpty() ?
				requestUrl + "?" + queryString : requestUrl;
			parameters.add(fullUrl);
		}

		private void appendRequestMethod(HttpServletRequest request) {
			template.append("\n  请求方法: {}");
			parameters.add(request.getMethod());
		}

		private void appendRequestParameter(HttpServletRequest request) {
			template.append("\n  请求参数: {}");
			parameters.add(WebUtil.getRequestContent(request));
		}

		private void appendClientIp(HttpServletRequest request) {
			template.append("\n  请求地址: {}");
			parameters.add(WebUtil.getIP(request));
		}

		private void appendRequestUser(HttpServletRequest request) {
			template.append("\n  请求用户: {}");
			parameters.add(AuthUtil.getUserAccount(request));
		}

		private void appendRequestUserId(HttpServletRequest request) {
			template.append("\n  用户主键: {}");
			parameters.add(AuthUtil.getUserId(request));
		}

		public LogEntry build() {
			return new LogEntry(template.toString(), parameters.toArray());
		}
	}

	/**
	 * 日志条目
	 */
	private record LogEntry(String template, Object[] parameters) {

		public void log() {
			log.error(template, parameters);
		}
	}


}
