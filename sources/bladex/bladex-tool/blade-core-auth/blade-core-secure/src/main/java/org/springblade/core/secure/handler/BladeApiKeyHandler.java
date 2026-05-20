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
package org.springblade.core.secure.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.KeyCrypto;
import org.springblade.core.secure.props.KeyProperties;
import org.springblade.core.secure.provider.ApiKeyInfo;
import org.springblade.core.tool.jackson.JsonUtil;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Date;
import java.util.List;

import static org.springblade.core.secure.constant.ApiKeyConstant.*;

/**
 * API Key 处理器默认实现
 *
 * @author Chill
 */
@AllArgsConstructor
public class BladeApiKeyHandler implements IApiKeyHandler {

	/**
	 * 路径匹配器
	 */
	private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

	private final JdbcTemplate jdbcTemplate;
	private final KeyProperties keyProperties;

	@Override
	public BladeUser getUser(String apiKey) {
		// 检查功能是否启用
		if (!keyProperties.getEnabled()) {
			return null;
		}
		// 检查令牌格式是否合法
		String parseKey = KeyCrypto.parseKey(apiKey, keyProperties.getCryptoKey());
		if (Func.isBlank(parseKey)) {
			return null;
		}
		// 加载用户信息
		BladeUser bladeUser = loadUser(apiKey);
		if (bladeUser == null) {
			return null;
		}
		// 验证访问路径权限
		HttpServletRequest request = WebUtil.getRequest();
		if (request != null) {
			String requestPath = request.getRequestURI();
			if (!validateApiPath(apiKey, requestPath)) {
				return null;
			}
		}
		return bladeUser;
	}

	@Override
	public void removeCache(String apiKey) {
		CacheUtil.evict(API_KEY_CACHE, CACHE_USER_PREFIX, apiKey, Boolean.FALSE);
		CacheUtil.evict(API_KEY_CACHE, CACHE_PATH_PREFIX, apiKey, Boolean.FALSE);
	}

	@Override
	public String generateKey() {
		return KeyCrypto.generateKey(keyProperties.getCryptoKey());
	}

	/**
	 * 查询 API Key 信息
	 *
	 * @param apiKey API Key
	 * @return ApiKeyInfo
	 */
	private ApiKeyInfo queryApiKeyInfo(String apiKey) {
		List<ApiKeyInfo> results = jdbcTemplate.query(API_KEY_SELECT_STATEMENT, new BeanPropertyRowMapper<>(ApiKeyInfo.class), apiKey);
		if (results.isEmpty()) {
			return null;
		}
		return results.get(0);
	}

	/**
	 * 加载用户信息（优先从缓存获取）
	 *
	 * @param apiKey API Key
	 * @return BladeUser
	 */
	private BladeUser loadUser(String apiKey) {
		// 从缓存获取用户信息
		BladeUser bladeUser = CacheUtil.get(API_KEY_CACHE, CACHE_USER_PREFIX, apiKey, BladeUser.class, Boolean.FALSE);
		if (bladeUser != null) {
			// 若用户ID为空，说明是缓存的空对象标记，返回null防止缓存穿透
			return bladeUser.getUserId() != null ? bladeUser : null;
		}
		// 从数据库查询
		ApiKeyInfo apiKeyInfo = queryApiKeyInfo(apiKey);
		if (apiKeyInfo == null) {
			// 若数据库查询依旧为空，则默认空对象以防止缓存穿透
			CacheUtil.put(API_KEY_CACHE, CACHE_USER_PREFIX, apiKey, new BladeUser(), Boolean.FALSE);
			return null;
		}
		// 检查状态
		if (apiKeyInfo.getStatus() == null || apiKeyInfo.getStatus() != STATUS_ACTIVE) {
			return null;
		}
		// 检查过期时间
		Date expireTime = apiKeyInfo.getExpireTime();
		if (expireTime != null && expireTime.before(new Date())) {
			return null;
		}
		// 检查用户是否存在
		if (apiKeyInfo.getUserId() == null || Func.isBlank(apiKeyInfo.getAccount())) {
			return null;
		}
		// 构建 BladeUser
		bladeUser = new BladeUser();
		bladeUser.setUserId(apiKeyInfo.getUserId());
		bladeUser.setTenantId(apiKeyInfo.getTenantId());
		bladeUser.setAccount(apiKeyInfo.getAccount());
		bladeUser.setUserName(apiKeyInfo.getName());
		bladeUser.setNickName(apiKeyInfo.getRealName());
		bladeUser.setClientId(apiKey);
		bladeUser.setDeptId(Func.toStrWithEmpty(apiKeyInfo.getDeptId(), StringPool.MINUS_ONE));
		bladeUser.setPostId(Func.toStrWithEmpty(apiKeyInfo.getPostId(), StringPool.MINUS_ONE));
		bladeUser.setRoleId(Func.toStrWithEmpty(apiKeyInfo.getRoleId(), StringPool.MINUS_ONE));
		// 为了系统安全性，默认不返回角色名称，保持低权限角色调用系统给第三方的接口
		// 若需要调用高权限接口，请自行扩展设置角色名称，但请一定要做好安全风险管理
		bladeUser.setRoleName(StringPool.EMPTY);
		// 解析扩展参数
		String extParams = apiKeyInfo.getExtParams();
		if (Func.isNotBlank(extParams)) {
			Kv detail = JsonUtil.parse(extParams, Kv.class);
			bladeUser.setDetail(detail);
		}
		// 写入缓存
		CacheUtil.put(API_KEY_CACHE, CACHE_USER_PREFIX, apiKey, bladeUser, Boolean.FALSE);
		return bladeUser;
	}

	/**
	 * 验证请求路径是否有访问权限
	 *
	 * @param apiKey      API Key
	 * @param requestPath 请求路径
	 * @return true 有权限，false 无权限
	 */
	private boolean validateApiPath(String apiKey, String requestPath) {
		// 从缓存获取访问权限路径，未命中则查询数据库
		String apiPath = CacheUtil.get(API_KEY_CACHE, CACHE_PATH_PREFIX, apiKey, String.class, Boolean.FALSE);
		if (apiPath == null) {
			ApiKeyInfo apiKeyInfo = queryApiKeyInfo(apiKey);
			apiPath = (apiKeyInfo != null) ? apiKeyInfo.getApiPath() : null;
			// 若数据库查询依旧为空，则默认全路径权限以防止缓存穿透
			CacheUtil.put(API_KEY_CACHE, CACHE_PATH_PREFIX, apiKey, Func.toStrWithEmpty(apiPath, FULL_PATH), Boolean.FALSE);
		}
		// 如果未配置访问权限，默认允许所有访问
		if (Func.isBlank(apiPath) || FULL_PATH.equals(apiPath)) {
			return true;
		}
		// 解析逗号分隔的路径并匹配
		String[] paths = apiPath.split(StringPool.COMMA);
		for (String path : paths) {
			String trimmedPath = path.trim();
			if (Func.isNotBlank(trimmedPath) && PATH_MATCHER.match(trimmedPath, requestPath)) {
				return true;
			}
		}
		return false;
	}

}
