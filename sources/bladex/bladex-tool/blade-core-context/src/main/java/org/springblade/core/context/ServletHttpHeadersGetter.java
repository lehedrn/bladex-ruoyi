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
package org.springblade.core.context;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springblade.core.context.props.BladeContextProperties;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;

import java.util.Enumeration;
import java.util.List;

/**
 * HttpHeaders 获取器
 *
 * @author L.cm
 */
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletHttpHeadersGetter implements BladeHttpHeadersGetter {
	private final BladeContextProperties properties;

	@Nullable
	@Override
	public HttpHeaders get() {
		HttpServletRequest request = WebUtil.getRequest();
		if (request == null) {
			return null;
		}
		return get(request);
	}

	@Nullable
	@Override
	public HttpHeaders get(HttpServletRequest request) {
		// 传递请求头
		HttpHeaders headers = new HttpHeaders();
		Enumeration<String> headerNames = request.getHeaderNames();
		if (headerNames != null) {
			List<String> crossHeaders = properties.getCrossHeaders();
			List<String> lowerCrossHeaders = crossHeaders.stream().map(String::toLowerCase).toList();
			List<String> allowed = properties.getHeaders().getAllowed();
			List<String> lowerAllowed = allowed.stream().map(String::toLowerCase).toList();
			while (headerNames.hasMoreElements()) {
				String key = headerNames.nextElement();
				String lowerKey = key.toLowerCase();
				// 只支持配置的 header
				if (lowerCrossHeaders.contains(lowerKey) ||
					lowerAllowed.contains(lowerKey) ||
					crossHeaders.contains(key) ||
					allowed.contains(key)) {
					String values = request.getHeader(key);
					// header value 不为空的 传递
					if (StringUtil.isNotBlank(values)) {
						headers.add(key, values);
					}
				}
			}
		}
		return headers;
	}

}
