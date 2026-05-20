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
package org.springblade.core.secure.props;

import lombok.Data;
import org.springblade.core.secure.exception.KeyException;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 超级密钥配置
 *
 * @author Chill
 */
@Data
@ConfigurationProperties("blade.key")
public class KeyProperties {

	/**
	 * 超级密钥是否开启
	 */
	private Boolean enabled = Boolean.FALSE;

	/**
	 * 超级密钥
	 */
	private String cryptoKey = StringPool.EMPTY;

	/**
	 * 获取超级密钥
	 */
	public String getCryptoKey() {
		if (StringUtil.isBlank(cryptoKey)) {
			throw new KeyException("请配置 blade.key.crypto-key 的值");
		}
		return this.cryptoKey;
	}

}
