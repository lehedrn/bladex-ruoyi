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
package org.springblade.core.mp.encrypt.algorithm.provider;

import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.encrypt.algorithm.Algorithm;
import org.springblade.core.mp.encrypt.algorithm.EncryptAlgorithm;
import org.springblade.core.tool.utils.DesUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.stereotype.Component;

/**
 * DES加密器
 *
 * @author BladeX
 */
@Slf4j
@Component
public class DesAlgorithm implements EncryptAlgorithm {

	@Override
	public String type() {
		return Algorithm.DES.name();
	}

	@Override
	public String encrypt(String plainText, String secretKey) {
		if (StringUtil.isBlank(plainText)) {
			return plainText;
		}
		return DesUtil.encryptToHex(plainText, secretKey);
	}

	@Override
	public String decrypt(String cipherText, String secretKey) {
		if (StringUtil.isBlank(cipherText)) {
			return cipherText;
		}
		try {
			return DesUtil.decryptFormHex(cipherText, secretKey);
		} catch (Exception e) {
			log.error("DES decryption failed, returning original value: {}", e.getMessage());
			return cipherText;
		}
	}
}
