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
package org.springblade.core.mp.encrypt.props;

import lombok.Data;
import org.springblade.core.mp.encrypt.algorithm.Algorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 加密配置属性
 *
 * @author BladeX
 */
@Data
@ConfigurationProperties(prefix = EncryptProperties.PREFIX)
public class EncryptProperties {
	public static final String PREFIX = "blade.mybatis-plus.encrypt";

	/**
	 * 是否启用字段加密功能
	 */
	private boolean enabled = true;

	/**
	 * 滑动窗口大小,用于字段加密的模糊查询
	 */
	private int windowSize = 3;

	/**
	 * 加密算法类型
	 */
	private Algorithm algorithm = Algorithm.AES;

	/**
	 * 加密算法密钥
	 */
	private String secretKey;

}
