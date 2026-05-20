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

package org.springblade.core.report.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.WebUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * UReport 鉴权过滤器
 *
 * <p>处理 /ureport/* 路径的访问鉴权，放行前端资源文件</p>
 *
 * @author BladeX
 */
@Slf4j
public class UReportAuthFilter implements Filter {

	/**
	 * 前端资源文件扩展名
	 */
	private static final List<String> STATIC_RESOURCE_EXTENSIONS = Arrays.asList(
		".js", ".css", ".png", ".jpg", ".jpeg", ".gif", ".ico", ".svg",
		".woff", ".woff2", ".ttf", ".eot", ".map", ".html", ".htm"
	);

	/**
	 * 资源文件路径前缀
	 */
	private static final String RESOURCE_PATH_PREFIX = "/ureport/res/ureport-asserts";

	/**
	 * Session 中存储认证状态的键名
	 */
	private static final String SESSION_AUTH_KEY = "UREPORT_AUTHENTICATED";

	/**
	 * Session 中存储用户信息的键名
	 */
	private static final String SESSION_USER_KEY = "UREPORT_USER_INFO";

	/**
	 * Session 中存储用户主键的键名
	 */
	private static final String SESSION_USER_ID = "UREPORT_USER_ID";

	/**
	 * Session 超时时间，单位为秒
	 */
	private static final Integer INTERVAL_TIME = 120 * 60;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String requestURI = WebUtil.getRequestURI(httpRequest);
		String requestIP = WebUtil.getIP(httpRequest);

		// 检查是否为静态资源文件，如果是则直接放行
		if (isStaticResource(requestURI)) {
			chain.doFilter(request, response);
			return;
		}

		// 验证用户是否已认证（优先检查 Session，再检查 Token）
		if (!isAuthenticatedWithSession(httpRequest)) {
			log.warn("未授权访问 UReport API: {}, IP: {}", requestURI, requestIP);
			handleUnauthorized(httpResponse);
			return;
		}

		// 验证通过放行请求
		chain.doFilter(request, response);
	}

	/**
	 * 判断是否为静态资源文件
	 */
	private boolean isStaticResource(String requestURI) {
		// 检查是否为资源文件路径
		if (requestURI.startsWith(RESOURCE_PATH_PREFIX)) {
			return true;
		}

		// 检查文件扩展名
		String lowerCaseURI = requestURI.toLowerCase();
		return STATIC_RESOURCE_EXTENSIONS.stream()
			.anyMatch(lowerCaseURI::endsWith);
	}

	/**
	 * 验证用户是否已认证（Session + Token 双重验证）
	 */
	private boolean isAuthenticatedWithSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		// 1. 优先检查 Session 认证状态
		if (session != null && Boolean.TRUE.equals(session.getAttribute(SESSION_AUTH_KEY))) {
			log.debug("Session 认证有效，用户: {}", session.getAttribute(SESSION_USER_KEY));
			return true;
		}

		// 2. Session 无效，尝试 Token 认证
		if (isTokenAuthenticated(request)) {
			// Token 认证成功，建立 Session
			establishSession(request);
			log.info("Token 认证成功，已建立 Session 会话");
			return true;
		}

		return false;
	}

	/**
	 * Token 认证验证
	 */
	private boolean isTokenAuthenticated(HttpServletRequest request) {
		try {
			Long userId = AuthUtil.getUserId(request);
			return userId != null && userId > 0;
		} catch (Exception e) {
			log.debug("Token 认证失败: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * 建立认证会话
	 */
	private void establishSession(HttpServletRequest request) {
		HttpSession session = request.getSession(true);

		try {
			String userAccount = AuthUtil.getUserAccount(request);
			Long userId = AuthUtil.getUserId(request);

			// 设置认证标记
			session.setAttribute(SESSION_AUTH_KEY, true);
			session.setAttribute(SESSION_USER_KEY, userAccount);
			session.setAttribute(SESSION_USER_ID, userId);

			// 设置 Session 超时时间（60分钟）
			session.setMaxInactiveInterval(INTERVAL_TIME);

			log.debug("建立 UReport 认证会话: 用户 {}, SessionID: {}", userAccount, session.getId());
		} catch (Exception e) {
			log.warn("建立认证会话失败: {}", e.getMessage());
		}
	}

	/**
	 * 从 Session 获取用户账号
	 */
	private String getUserFromSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object userAccount = session.getAttribute(SESSION_USER_KEY);
			return userAccount != null ? userAccount.toString() : "未知用户";
		}
		return "未知用户";
	}

	/**
	 * 处理未授权访问
	 */
	private void handleUnauthorized(HttpServletResponse response) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		try (PrintWriter writer = response.getWriter()) {
			writer.write("访问 UReport 需要 BladeX 的 Token 认证");
			writer.flush();
		}
	}

}
