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
package org.springblade.core.secure.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.secure.nonce.NonceStore;
import org.springblade.core.secure.props.SignSecure;
import org.springblade.core.secure.provider.HttpMethod;
import org.springblade.core.secure.provider.ResponseProvider;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.DigestUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Date;
import java.util.List;

/**
 * 签名认证拦截器校验
 * <p>
 * 支持防重放攻击检测：
 * 1. 时间戳校验：验证请求时间是否在有效窗口内
 * 2. Nonce校验：通过存储nonce防止重复请求
 * 3. 签名校验：验证请求签名是否正确
 *
 * @author Chill
 */
@Slf4j
public class SignInterceptor implements HandlerInterceptor {

	/**
	 * 表达式匹配
	 */
	private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

	/**
	 * 授权集合
	 */
	private final List<SignSecure> signSecures;

	/**
	 * Nonce存储（用于防重放检测）
	 */
	private final NonceStore nonceStore;

	/**
	 * 请求时间
	 */
	private static final String TIMESTAMP = "timestamp";

	/**
	 * 随机数
	 */
	private static final String NONCE = "nonce";

	/**
	 * 时间随机数组合加密串
	 */
	private static final String SIGNATURE = "signature";

	/**
	 * sha1加密方式
	 */
	private static final String SHA1 = "sha1";

	/**
	 * md5加密方式
	 */
	private static final String MD5 = "md5";

	/**
	 * 时间戳有效窗口（秒）
	 */
	private static final int TIMESTAMP_WINDOW = 10;

	/**
	 * nonce过期时间（秒）
	 */
	private static final int NONCE_EXPIRE = 60;

	/**
	 * 构造函数
	 *
	 * @param signSecures 签名配置列表
	 * @param nonceStore  Nonce存储
	 */
	public SignInterceptor(List<SignSecure> signSecures, @Nullable NonceStore nonceStore) {
		this.signSecures = signSecures;
		this.nonceStore = nonceStore;
	}

	@Override
	public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
		boolean check = signSecures.stream().filter(signSecure -> checkAuth(request, signSecure)).findFirst().map(
			signSecure -> checkSign(signSecure.getCrypto())
		).orElse(Boolean.TRUE);
		if (!check) {
			log.warn("授权认证失败，请求接口：{}，请求IP：{}，请求参数：{}", request.getRequestURI(), WebUtil.getIP(request), JsonUtil.toJson(request.getParameterMap()));
			ResponseProvider.write(response);
			return false;
		}
		return true;
	}

	/**
	 * 检测授权
	 */
	private boolean checkAuth(HttpServletRequest request, SignSecure signSecure) {
		return checkMethod(request, signSecure.getMethod()) && checkPath(request, signSecure.getPattern());
	}

	/**
	 * 检测请求方法
	 */
	private boolean checkMethod(HttpServletRequest request, HttpMethod method) {
		return method == HttpMethod.ALL || (
			method != null && method == HttpMethod.of(request.getMethod())
		);
	}

	/**
	 * 检测路径匹配
	 */
	private boolean checkPath(HttpServletRequest request, String pattern) {
		String servletPath = request.getServletPath();
		String pathInfo = request.getPathInfo();
		if (pathInfo != null && !pathInfo.isEmpty()) {
			servletPath = servletPath + pathInfo;
		}
		return ANT_PATH_MATCHER.match(pattern, servletPath);
	}

	/**
	 * 检测签名（含防重放检测）
	 *
	 * @param crypto 加密方式
	 * @return 检测结果
	 */
	private boolean checkSign(String crypto) {
		try {
			HttpServletRequest request = WebUtil.getRequest();
			if (request == null) {
				return false;
			}
			// 获取头部动态签名信息
			String timestamp = request.getHeader(TIMESTAMP);
			String nonce = request.getHeader(NONCE);
			String signature = request.getHeader(SIGNATURE);

			// 1. 判断时间戳是否在合法时间段
			if (!checkTimestamp(timestamp)) {
				log.warn("授权认证失败，认证信息：{}", "请求时间戳非法");
				return false;
			}

			// 2. 防重放检测：验证nonce是否已使用
			if (!checkNonce(nonce)) {
				log.warn("授权认证失败，认证信息：{}", "请求nonce重复（疑似重放攻击）");
				return false;
			}

			// 3. 加密签名比对
			String sign;
			if (MD5.equals(crypto)) {
				sign = DigestUtil.md5Hex(timestamp + nonce);
			} else if (SHA1.equals(crypto)) {
				sign = DigestUtil.sha1Hex(timestamp + nonce);
			} else {
				sign = DigestUtil.sha1Hex(timestamp + nonce);
			}
			return sign.equalsIgnoreCase(signature);
		} catch (Exception e) {
			log.warn("授权认证失败，错误信息：{}", e.getMessage());
			return false;
		}
	}

	/**
	 * 检测时间戳是否在有效窗口内
	 *
	 * @param timestamp 时间戳
	 * @return 是否有效
	 */
	private boolean checkTimestamp(String timestamp) {
		if (Func.isBlank(timestamp)) {
			return false;
		}
		long seconds = Duration.between(new Date(Func.toLong(timestamp)).toInstant(), DateUtil.now().toInstant()).getSeconds();
		return seconds >= 0 && seconds <= TIMESTAMP_WINDOW;
	}

	/**
	 * 检测nonce是否已使用（防重放）
	 *
	 * @param nonce 随机数
	 * @return true-nonce有效（首次使用），false-nonce已使用（重放攻击）
	 */
	private boolean checkNonce(String nonce) {
		if (Func.isBlank(nonce)) {
			return false;
		}
		// 如果没有配置NonceStore，跳过防重放检测
		if (nonceStore == null) {
			return true;
		}
		// 尝试存储nonce，如果存储失败说明nonce已存在
		return nonceStore.tryStore(nonce, NONCE_EXPIRE);
	}

}
