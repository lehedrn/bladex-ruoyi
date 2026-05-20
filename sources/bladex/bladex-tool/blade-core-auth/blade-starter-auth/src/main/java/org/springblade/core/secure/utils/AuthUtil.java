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
package org.springblade.core.secure.utils;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springblade.core.jwt.JwtCrypto;
import org.springblade.core.jwt.JwtUtil;
import org.springblade.core.launch.constant.TokenConstant;
import org.springblade.core.launch.props.BladeProperties;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.handler.IApiKeyHandler;
import org.springblade.core.tool.constant.RoleConstant;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static org.springblade.core.jwt.JwtCrypto.BLADE_TOKEN_CRYPTO_KEY;

/**
 * Auth工具类
 *
 * @author Chill
 */
public class AuthUtil {
	private static final String BLADE_USER_REQUEST_ATTR = "_BLADE_USER_REQUEST_ATTR_";
	private final static String BLADE_SECURE_HEADER = TokenConstant.SECURE_HEADER;
	private final static String BLADE_SECURE_HEADER_VALUE = TokenConstant.SECURE_HEADER_VALUE;
	private final static String HEADER = TokenConstant.AUTH_HEADER;
	private final static String ACCOUNT = TokenConstant.ACCOUNT;
	private final static String USER_NAME = TokenConstant.USER_NAME;
	private final static String NICK_NAME = TokenConstant.NICK_NAME;
	private final static String USER_ID = TokenConstant.USER_ID;
	private final static String DEPT_ID = TokenConstant.DEPT_ID;
	private final static String POST_ID = TokenConstant.POST_ID;
	private final static String ROLE_ID = TokenConstant.ROLE_ID;
	private final static String ROLE_NAME = TokenConstant.ROLE_NAME;
	private final static String TENANT_ID = TokenConstant.TENANT_ID;
	private final static String OAUTH_ID = TokenConstant.OAUTH_ID;
	private final static String CLIENT_ID = TokenConstant.CLIENT_ID;
	private final static String DETAIL = TokenConstant.DETAIL;

	private static BladeProperties bladeProperties;

	/**
	 * ApiKey 处理器
	 */
	private static IApiKeyHandler apiKeyHandler;

	/**
	 * 获取配置类
	 *
	 * @return jwtProperties
	 */
	private static BladeProperties getBladeProperties() {
		if (bladeProperties == null) {
			bladeProperties = SpringUtil.getBean(BladeProperties.class);
		}
		return bladeProperties;
	}

	/**
	 * 获取 ApiKey 处理器
	 */
	private static IApiKeyHandler getApiKeyHandler() {
		if (apiKeyHandler == null) {
			apiKeyHandler = SpringUtil.getBean(IApiKeyHandler.class);
		}
		return apiKeyHandler;
	}

	/**
	 * 判断给定的认证字符串是否为 API Key
	 *
	 * @param auth 认证字符串
	 * @return true 如果是 API Key
	 */
	public static boolean isApiKey(String auth) {
		return getApiKeyHandler().isApiKey(auth);
	}

	/**
	 * 判断当前请求是否为 API Key 认证
	 *
	 * @return true 如果当前请求使用 API Key 认证
	 */
	public static boolean isApiKeyRequest() {
		return getApiKeyHandler().isApiKeyRequest();
	}

	/**
	 * 通过 API Key 获取用户信息
	 *
	 * @param apiKey API Key
	 * @return BladeUser
	 */
	public static BladeUser getUserByApiKey(String apiKey) {
		IApiKeyHandler handler = getApiKeyHandler();
		if (handler != null && StringUtil.isNotBlank(apiKey)) {
			return handler.getUser(apiKey);
		}
		return null;
	}

	/**
	 * 获取用户信息
	 *
	 * @return BladeUser
	 */
	public static BladeUser getUser() {
		HttpServletRequest request = WebUtil.getRequest();
		if (request == null) {
			return null;
		}
		// 优先从 request 中获取
		Object bladeUser = request.getAttribute(BLADE_USER_REQUEST_ATTR);
		if (bladeUser == null) {
			bladeUser = getUser(request);
			if (bladeUser != null) {
				// 设置到 request 中
				request.setAttribute(BLADE_USER_REQUEST_ATTR, bladeUser);
			}
		}
		return (BladeUser) bladeUser;
	}

	/**
	 * 获取用户信息
	 *
	 * @param auth auth
	 * @return BladeUser
	 */
	public static BladeUser getUser(String auth) {
		return getUser(getClaims(auth));
	}

	/**
	 * 获取用户信息
	 *
	 * @param request request
	 * @return BladeUser
	 */
	public static BladeUser getUser(HttpServletRequest request) {
		// 获取 Token 参数
		String auth = request.getHeader(AuthUtil.HEADER);
		if (StringUtil.isBlank(auth)) {
			auth = request.getParameter(AuthUtil.HEADER);
		}
		// 判断是否为 API Key
		if (getApiKeyHandler().isApiKey(auth)) {
			return getUserByApiKey(auth);
		}
		return getUser(getClaims(request));
	}

	/**
	 * 获取用户信息
	 *
	 * @param claims claims
	 * @return BladeUser
	 */
	@SuppressWarnings("unchecked")
	public static BladeUser getUser(Claims claims) {
		if (claims == null) {
			return null;
		}
		String clientId = Func.toStr(claims.get(AuthUtil.CLIENT_ID));
		Long userId = Func.toLong(claims.get(AuthUtil.USER_ID));
		String tenantId = Func.toStr(claims.get(AuthUtil.TENANT_ID));
		String oauthId = Func.toStr(claims.get(AuthUtil.OAUTH_ID));
		String deptId = Func.toStrWithEmpty(claims.get(AuthUtil.DEPT_ID), StringPool.MINUS_ONE);
		String postId = Func.toStrWithEmpty(claims.get(AuthUtil.POST_ID), StringPool.MINUS_ONE);
		String roleId = Func.toStrWithEmpty(claims.get(AuthUtil.ROLE_ID), StringPool.MINUS_ONE);
		String account = Func.toStr(claims.get(AuthUtil.ACCOUNT));
		String roleName = Func.toStr(claims.get(AuthUtil.ROLE_NAME));
		String userName = Func.toStr(claims.get(AuthUtil.USER_NAME));
		String nickName = Func.toStr(claims.get(AuthUtil.NICK_NAME));
		Kv detail = Kv.create().setAll((Map<? extends String, ?>) claims.get(AuthUtil.DETAIL));
		BladeUser bladeUser = new BladeUser();
		bladeUser.setClientId(clientId);
		bladeUser.setUserId(userId);
		bladeUser.setTenantId(tenantId);
		bladeUser.setOauthId(oauthId);
		bladeUser.setAccount(account);
		bladeUser.setDeptId(deptId);
		bladeUser.setPostId(postId);
		bladeUser.setRoleId(roleId);
		bladeUser.setRoleName(roleName);
		bladeUser.setUserName(userName);
		bladeUser.setNickName(nickName);
		bladeUser.setDetail(detail);
		return bladeUser;
	}

	/**
	 * 是否为超管
	 *
	 * @return boolean
	 */
	public static boolean isAdministrator() {
		return StringUtil.containsAny(getUserRole(), RoleConstant.ADMINISTRATOR);
	}

	/**
	 * 是否为管理员
	 *
	 * @return boolean
	 */
	public static boolean isAdmin() {
		return StringUtil.containsAny(getUserRole(), RoleConstant.ADMIN);
	}

	/**
	 * 是否通过鉴权
	 *
	 * @return boolean
	 */
	public static boolean hasAuth() {
		return null != getUser();
	}

	/**
	 * 获取用户id
	 *
	 * @return userId
	 */
	public static Long getUserId() {
		BladeUser user = getUser();
		return (null == user) ? -1 : user.getUserId();
	}

	/**
	 * 获取用户id
	 *
	 * @param request request
	 * @return userId
	 */
	public static Long getUserId(HttpServletRequest request) {
		BladeUser user = getUser(request);
		return (null == user) ? -1 : user.getUserId();
	}

	/**
	 * 获取用户账号
	 *
	 * @return userAccount
	 */
	public static String getUserAccount() {
		BladeUser user = getUser();
		return (null == user) ? StringPool.EMPTY : user.getAccount();
	}

	/**
	 * 获取用户账号
	 *
	 * @param request request
	 * @return userAccount
	 */
	public static String getUserAccount(HttpServletRequest request) {
		BladeUser user = getUser(request);
		return (null == user) ? StringPool.EMPTY : user.getAccount();
	}

	/**
	 * 获取用户名
	 *
	 * @return userName
	 */
	public static String getUserName() {
		BladeUser user = getUser();
		return (null == user) ? StringPool.EMPTY : user.getUserName();
	}

	/**
	 * 获取用户名
	 *
	 * @param request request
	 * @return userName
	 */
	public static String getUserName(HttpServletRequest request) {
		BladeUser user = getUser(request);
		return (null == user) ? StringPool.EMPTY : user.getUserName();
	}

	/**
	 * 获取昵称
	 *
	 * @return userName
	 */
	public static String getNickName() {
		BladeUser user = getUser();
		return (null == user) ? StringPool.EMPTY : user.getNickName();
	}

	/**
	 * 获取昵称
	 *
	 * @param request request
	 * @return userName
	 */
	public static String getNickName(HttpServletRequest request) {
		BladeUser user = getUser(request);
		return (null == user) ? StringPool.EMPTY : user.getNickName();
	}

	/**
	 * 获取用户部门
	 *
	 * @return userName
	 */
	public static String getDeptId() {
		BladeUser user = getUser();
		return (null == user) ? StringPool.EMPTY : user.getDeptId();
	}

	/**
	 * 获取用户部门
	 *
	 * @param request request
	 * @return userName
	 */
	public static String getDeptId(HttpServletRequest request) {
		BladeUser user = getUser(request);
		return (null == user) ? StringPool.EMPTY : user.getDeptId();
	}

	/**
	 * 获取用户岗位
	 *
	 * @return userName
	 */
	public static String getPostId() {
		BladeUser user = getUser();
		return (null == user) ? StringPool.EMPTY : user.getPostId();
	}

	/**
	 * 获取用户岗位
	 *
	 * @param request request
	 * @return userName
	 */
	public static String getPostId(HttpServletRequest request) {
		BladeUser user = getUser(request);
		return (null == user) ? StringPool.EMPTY : user.getPostId();
	}

	/**
	 * 获取用户角色
	 *
	 * @return userName
	 */
	public static String getUserRole() {
		BladeUser user = getUser();
		return (null == user) ? StringPool.EMPTY : user.getRoleName();
	}

	/**
	 * 获取用角色
	 *
	 * @param request request
	 * @return userName
	 */
	public static String getUserRole(HttpServletRequest request) {
		BladeUser user = getUser(request);
		return (null == user) ? StringPool.EMPTY : user.getRoleName();
	}

	/**
	 * 获取租户ID
	 *
	 * @return tenantId
	 */
	public static String getTenantId() {
		BladeUser user = getUser();
		return (null == user) ? StringPool.EMPTY : user.getTenantId();
	}

	/**
	 * 获取租户ID
	 *
	 * @param request request
	 * @return tenantId
	 */
	public static String getTenantId(HttpServletRequest request) {
		BladeUser user = getUser(request);
		return (null == user) ? StringPool.EMPTY : user.getTenantId();
	}

	/**
	 * 获取第三方认证ID
	 *
	 * @return tenantId
	 */
	public static String getOauthId() {
		BladeUser user = getUser();
		return (null == user) ? StringPool.EMPTY : user.getOauthId();
	}

	/**
	 * 获取第三方认证ID
	 *
	 * @param request request
	 * @return tenantId
	 */
	public static String getOauthId(HttpServletRequest request) {
		BladeUser user = getUser(request);
		return (null == user) ? StringPool.EMPTY : user.getOauthId();
	}

	/**
	 * 获取客户端id
	 *
	 * @return clientId
	 */
	public static String getClientId() {
		BladeUser user = getUser();
		return (null == user) ? StringPool.EMPTY : user.getClientId();
	}

	/**
	 * 获取客户端id
	 *
	 * @param request request
	 * @return clientId
	 */
	public static String getClientId(HttpServletRequest request) {
		BladeUser user = getUser(request);
		return (null == user) ? StringPool.EMPTY : user.getClientId();
	}

	/**
	 * 获取用户详情
	 *
	 * @return clientId
	 */
	public static Kv getDetail() {
		BladeUser user = getUser();
		return (null == user) ? Kv.create() : user.getDetail();
	}

	/**
	 * 获取用户详情
	 *
	 * @param request request
	 * @return clientId
	 */
	public static Kv getDetail(HttpServletRequest request) {
		BladeUser user = getUser(request);
		return (null == user) ? Kv.create() : user.getDetail();
	}

	/**
	 * 用户信息是否缺失
	 *
	 * @return boolean
	 */
	public static boolean userIncomplete() {
		return userIncomplete(Objects.requireNonNull(getUser()));
	}

	/**
	 * 用户信息是否缺失
	 *
	 * @param user user
	 * @return boolean
	 */
	public static boolean userIncomplete(BladeUser user) {
		if (Func.isEmpty(user)) {
			return true;
		}
		if (Func.hasEmpty(user.getUserId(),
			user.getAccount(),
			user.getTenantId(),
			user.getClientId(),
			user.getDeptId(),
			user.getRoleId())) {
			return true;
		}
		return isInvalidIds(String.valueOf(user.getUserId()))
			|| isInvalidIds(user.getDeptId())
			|| isInvalidIds(user.getRoleId());
	}

	/**
	 * 判断复数id是否均为空
	 *
	 * @param ids 复数id
	 * @return boolean
	 */
	private static boolean isInvalidIds(String ids) {
		return Arrays.stream(ids.split(StringPool.COMMA))
			.map(String::trim)
			.mapToLong(id -> NumberUtil.toLong(id, -1L))
			.anyMatch(id -> id < 0);
	}


	/**
	 * 不包含安全请求头
	 *
	 * @return boolean
	 */
	public static boolean secureHeaderIncomplete() {
		HttpServletRequest request = WebUtil.getRequest();
		if (request == null) {
			return false;
		}
		String header = request.getHeader(BLADE_SECURE_HEADER);
		String parameter = request.getParameter(BLADE_SECURE_HEADER);
		return !(StringUtil.equals(BLADE_SECURE_HEADER_VALUE, header) || StringUtil.equals(BLADE_SECURE_HEADER_VALUE, parameter));
	}

	/**
	 * 获取Claims
	 *
	 * @param request request
	 * @return Claims
	 */
	public static Claims getClaims(HttpServletRequest request) {
		// 获取 Token 参数
		String auth = request.getHeader(AuthUtil.HEADER);
		String token = getToken(
			StringUtil.isNotBlank(auth) ? auth : request.getParameter(AuthUtil.HEADER)
		);
		// 获取 Token 值
		Claims claims = null;
		if (StringUtil.isNotBlank(token)) {
			claims = AuthUtil.parseJWT(token);
		}
		// 判断 Token 状态
		if (ObjectUtil.isNotEmpty(claims) && JwtUtil.getJwtProperties().getState()) {
			String tenantId = Func.toStr(claims.get(AuthUtil.TENANT_ID));
			String clientId = Func.toStr(claims.get(AuthUtil.CLIENT_ID));
			String userId = Func.toStr(claims.get(AuthUtil.USER_ID));
			String accessToken = JwtUtil.getAccessToken(tenantId, clientId, userId, token);
			if (!token.equalsIgnoreCase(accessToken)) {
				return null;
			}
		}
		return claims;
	}

	/**
	 * 获取Claims
	 *
	 * @param auth auth
	 * @return Claims
	 */
	public static Claims getClaims(String auth) {
		return AuthUtil.parseJWT(getToken(auth));
	}

	/**
	 * 获取请求头
	 *
	 * @return header
	 */
	public static String getHeader() {
		return getHeader(Objects.requireNonNull(WebUtil.getRequest()));
	}

	/**
	 * 获取请求头
	 *
	 * @param request request
	 * @return header
	 */
	public static String getHeader(HttpServletRequest request) {
		return request.getHeader(HEADER);
	}

	/**
	 * 解析jsonWebToken
	 *
	 * @param jsonWebToken jsonWebToken
	 * @return Claims
	 */
	public static Claims parseJWT(String jsonWebToken) {
		return JwtUtil.parseJWT(jsonWebToken);
	}

	/**
	 * 获取Token
	 *
	 * @param auth 认证串
	 * @return string
	 */
	public static String getToken(String auth) {
		String token = JwtUtil.getToken(auth);
		if (JwtUtil.isCrypto(auth)) {
			token = JwtCrypto.decryptToString(token, getBladeProperties().getEnvironment().getProperty(BLADE_TOKEN_CRYPTO_KEY));
		}
		return token;
	}

}
