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
package org.springblade.core.boot.request;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;
import java.util.List;

/**
 * Request全局过滤
 *
 * @author Chill
 */
@AllArgsConstructor
public class BladeRequestFilter implements Filter {

	/**
	 * 请求配置
	 */
	private final RequestProperties requestProperties;
	/**
	 * 路径匹配
	 */
	private final AntPathMatcher antPathMatcher = new AntPathMatcher();

	/**
	 * 默认拦截路径
	 */
	private final List<String> defaultBlockUrl = List.of("/**/actuator/**", "/health/**");
	/**
	 * 默认白名单
	 */
	private final List<String> defaultWhiteList = List.of("127.0.0.1", "172.30.*.*", "192.168.*.*", "10.*.*.*", "0:0:0:0:0:0:0:1");
	/**
	 * 默认提示信息
	 */
	private static final String DEFAULT_MESSAGE = "当前请求被拒绝，请联系管理员！";
	/**
	 * 方法不允许提示信息
	 */
	private static final String METHOD_NOT_ALLOWED_MESSAGE = "当前请求方法不被允许，请联系管理员！";

	@Override
	public void init(FilterConfig config) {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// 获取请求
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		// 判断 拦截请求 与 白名单
		if (requestProperties.getEnabled()) {
			// 获取请求路径
			String path = httpRequest.getServletPath();
			// 获取请求IP
			String ip = WebUtil.getIP(httpRequest);
			// 获取请求方法
			String method = httpRequest.getMethod();
			// 判断是否拦截请求
			if (isRequestBlock(path, ip)) {
				throw new ServletException(DEFAULT_MESSAGE);
			}
			// 判断请求方法是否允许
			if (!isMethodAllowed(path, method)) {
				throw new ServletException(METHOD_NOT_ALLOWED_MESSAGE);
			}
			// 判断是否跳过Request包装
			if (isRequestSkip(path)) {
				chain.doFilter(request, response);
			}
			// 采用Request包装
			else {
				BladeHttpServletRequestWrapper bladeRequest = new BladeHttpServletRequestWrapper(httpRequest);
				chain.doFilter(bladeRequest, response);
			}
		}
		// 采用原版Request请求
		else {
			chain.doFilter(request, response);
		}
	}

	/**
	 * 是否白名单
	 *
	 * @param ip ip地址
	 * @return boolean
	 */
	private boolean isWhiteList(String ip) {
		List<String> whiteList = requestProperties.getWhiteList();
		String[] defaultWhiteIps = defaultWhiteList.toArray(new String[0]);
		String[] whiteIps = whiteList.toArray(new String[0]);
		return PatternMatchUtils.simpleMatch(defaultWhiteIps, ip) || PatternMatchUtils.simpleMatch(whiteIps, ip);
	}

	/**
	 * 是否黑名单
	 *
	 * @param ip ip地址
	 * @return boolean
	 */
	private boolean isBlackList(String ip) {
		List<String> blackList = requestProperties.getBlackList();
		String[] blackIps = blackList.toArray(new String[0]);
		return PatternMatchUtils.simpleMatch(blackIps, ip);
	}

	/**
	 * 是否禁用请求访问
	 *
	 * @param path 请求路径
	 * @return boolean
	 */
	private boolean isRequestBlock(String path) {
		List<String> blockUrl = requestProperties.getBlockUrl();
		return defaultBlockUrl.stream().anyMatch(pattern -> antPathMatcher.match(pattern, path)) ||
			blockUrl.stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
	}

	/**
	 * 是否拦截请求
	 *
	 * @param path 请求路径
	 * @param ip   ip地址
	 * @return boolean
	 */
	private boolean isRequestBlock(String path, String ip) {
		return (isRequestBlock(path) && !isWhiteList(ip)) || isBlackList(ip);
	}

	/**
	 * 是否跳过请求包装
	 *
	 * @param path 请求路径
	 * @return boolean
	 */
	private boolean isRequestSkip(String path) {
		return requestProperties.getSkipUrl().stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
	}

	/**
	 * 判断请求方法是否允许
	 *
	 * @param path   请求路径
	 * @param method 请求方法
	 * @return boolean
	 */
	private boolean isMethodAllowed(String path, String method) {
		// 优先匹配路径级别规则
		List<HttpMethod> allowMethods = findAllowMethodsByPath(path);
		// 如果没有匹配的路径规则，使用全局配置
		if (allowMethods == null) {
			allowMethods = requestProperties.getAllowMethods();
		}
		// 如果配置为空，允许所有方法
		if (allowMethods == null || allowMethods.isEmpty()) {
			return true;
		}
		// 检查请求方法是否在允许列表中
		return allowMethods.stream().anyMatch(httpMethod -> httpMethod.matches(method));
	}

	/**
	 * 根据路径查找匹配的方法规则
	 *
	 * @param path 请求路径
	 * @return 允许的方法列表，未匹配返回null
	 */
	private List<HttpMethod> findAllowMethodsByPath(String path) {
		List<MethodRule> methodRules = requestProperties.getMethodRules();
		if (methodRules == null || methodRules.isEmpty()) {
			return null;
		}
		return methodRules.stream()
			.filter(rule -> rule.getPattern() != null && antPathMatcher.match(rule.getPattern(), path))
			.findFirst()
			.map(MethodRule::getAllowMethods)
			.orElse(null);
	}

	@Override
	public void destroy() {

	}

}
