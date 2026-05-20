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
package org.springblade.core.oauth2.utils;

import lombok.extern.slf4j.Slf4j;
import org.springblade.core.oauth2.exception.OAuth2Exception;
import org.springblade.core.oauth2.provider.OAuth2Request;

/**
 * OAuth2LogUtil
 *
 * @author Chill
 */
@Slf4j
public class OAuth2LogUtil {

	public static void logOAuth2Exception(OAuth2Exception ex, OAuth2Request request, boolean isProd) {
		if (!isProd) {
			log.warn("----------OAuth2认证异常，具体信息如下----------");
			log.warn("OAuth2Request: {}", request);
			log.warn("OAuth2Exception: ", ex);
			log.warn("--------------------------------------------");
		}
	}

}
