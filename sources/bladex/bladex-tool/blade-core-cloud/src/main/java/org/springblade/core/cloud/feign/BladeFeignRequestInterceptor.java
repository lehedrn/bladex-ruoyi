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
package org.springblade.core.cloud.feign;

import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.ThreadLocalUtil;
import org.springframework.http.HttpHeaders;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * feign 传递Request header
 *
 * <p>
 *     https://blog.csdn.net/u014519194/article/details/77160958
 *     http://tietang.wang/2016/02/25/hystrix/Hystrix%E5%8F%82%E6%95%B0%E8%AF%A6%E8%A7%A3/
 *     https://github.com/Netflix/Hystrix/issues/92#issuecomment-260548068
 * </p>
 *
 * @author L.cm
 */
public class BladeFeignRequestInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate requestTemplate) {
		// 设置请求头
		HttpHeaders headers = ThreadLocalUtil.get(BladeConstant.CONTEXT_KEY);
		if (headers != null && !headers.isEmpty()) {
			headers.forEach((key, values) ->
				values.forEach(value -> requestTemplate.header(key, value))
			);
		}
		// 如果是 API Key 认证则跳过设置
		if (AuthUtil.isApiKeyRequest()) {
			return;
		}
		// 设置用户信息头
		BladeUser user = AuthUtil.getUser();
		if (Func.isNotEmpty(user)) {
			requestTemplate.header(BladeConstant.CONTEXT_ACCOUNT_ID, Func.toStr(user.getUserId()));
			requestTemplate.header(BladeConstant.CONTEXT_TENANT_ID, user.getTenantId());
		}
	}

}
