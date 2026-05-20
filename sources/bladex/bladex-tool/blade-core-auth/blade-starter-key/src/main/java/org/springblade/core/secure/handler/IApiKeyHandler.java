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
import org.springblade.core.launch.constant.TokenConstant;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.constant.ApiKeyConstant;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.core.tool.utils.WebUtil;

/**
 * API Key 处理器接口
 * 用于通过 API Key 获取用户信息
 *
 * @author Chill
 */
public interface IApiKeyHandler {

	/**
	 * 通过 API Key 获取用户信息
	 *
	 * @param apiKey API Key (ak-xxx格式)
	 * @return BladeUser 用户信息，若不存在或已过期则返回 null
	 */
	BladeUser getUser(String apiKey);

	/**
	 * 移除 API Key 相关缓存
	 *
	 * @param apiKey API Key
	 */
	void removeCache(String apiKey);

	/**
	 * 生成 API Key
	 * 格式: ak-[UUID无中划线]
	 *
	 * @return 生成的 API Key
	 */
	default String generateKey() {
		return ApiKeyConstant.API_KEY_PREFIX + StringUtil.randomUUID();
	}

	/**
	 * 判断给定的认证字符串是否为有效的 API Key
	 *
	 * @param auth 认证字符串
	 * @return true 如果是有效的 API Key，否则返回 false
	 */
	default boolean isApiKey(String auth) {
		return StringUtil.isNotBlank(auth) && auth.startsWith(ApiKeyConstant.API_KEY_PREFIX);
	}

	/**
	 * 判断当前请求是否包含有效的 API Key
	 *
	 * @return true 如果请求中包含有效的 API Key，否则返回 false
	 */
	default boolean isApiKeyRequest() {
		HttpServletRequest request = WebUtil.getRequest();
		if (request == null) {
			return false;
		}
		String auth = request.getHeader(TokenConstant.AUTH_HEADER);
		if (StringUtil.isBlank(auth)) {
			auth = request.getParameter(TokenConstant.AUTH_HEADER);
		}
		return isApiKey(auth);
	}

}

