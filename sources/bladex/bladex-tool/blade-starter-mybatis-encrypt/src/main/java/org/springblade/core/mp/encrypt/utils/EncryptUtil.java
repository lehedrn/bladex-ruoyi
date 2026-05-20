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
package org.springblade.core.mp.encrypt.utils;

import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.encrypt.algorithm.Algorithm;
import org.springblade.core.mp.encrypt.algorithm.EncryptAlgorithm;
import org.springblade.core.mp.encrypt.algorithm.EncryptAlgorithmFactory;
import org.springblade.core.mp.encrypt.exception.EncryptException;
import org.springblade.core.mp.encrypt.props.EncryptProperties;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringUtil;

/**
 * 加密工具类 - 提供统一的密钥管理和加密解密方法
 *
 * @author BladeX
 */
@Slf4j
public class EncryptUtil {

	private static volatile EncryptProperties encryptProperties;
	private static volatile EncryptAlgorithmFactory encryptAlgorithmFactory;
	private static final int DEFAULT_WINDOW_SIZE = 3;

	/**
	 * 延迟加载获取加密配置属性
	 *
	 * @return 加密配置属性
	 */
	private static EncryptProperties getEncryptProperties() {
		if (encryptProperties == null) {
			synchronized (EncryptUtil.class) {
				if (encryptProperties == null) {
					encryptProperties = SpringUtil.getBean(EncryptProperties.class);
				}
			}
		}
		return encryptProperties;
	}

	/**
	 * 延迟加载获取加密器工厂
	 *
	 * @return 加密器工厂
	 */
	private static EncryptAlgorithmFactory getEncryptFactory() {
		if (encryptAlgorithmFactory == null) {
			synchronized (EncryptUtil.class) {
				if (encryptAlgorithmFactory == null) {
					encryptAlgorithmFactory = SpringUtil.getBean(EncryptAlgorithmFactory.class);
				}
			}
		}
		return encryptAlgorithmFactory;
	}

	/**
	 * 获取加密算法
	 *
	 * @return 加密算法
	 */
	public static EncryptAlgorithm getAlgorithm() {
		EncryptProperties properties = getEncryptProperties();
		Algorithm algorithm = properties.getAlgorithm();
		return getEncryptFactory().create(algorithm.name());
	}

	/**
	 * 获取加密密钥
	 *
	 * @return 加密密钥
	 */
	public static String getSecretKey() {
		EncryptProperties properties = getEncryptProperties();
		String secretKey = properties.getSecretKey();
		if (StringUtil.isBlank(secretKey)) {
			throw new EncryptException(
				"Encryption key not configured. Please set 'blade.mybatis-plus.encrypt.secret-key' in configuration file");
		}
		if (secretKey.length() != 32) {
			throw new EncryptException(
				"Encryption key must be exactly 32 characters long, current length: " + secretKey.length());
		}
		return secretKey;
	}

	/**
	 * 获取滑动窗口大小
	 *
	 * @return 滑动窗口大小
	 */
	public static int getWindowSize() {
		EncryptProperties properties = getEncryptProperties();
		return Func.toInt(properties.getWindowSize(), DEFAULT_WINDOW_SIZE);
	}

	/**
	 * 加密字符串为Base64格式（使用配置的算法）
	 *
	 * @param plainText 明文
	 * @return 加密后的Base64字符串
	 */
	public static String encrypt(String plainText) {
		if (StringUtil.isBlank(plainText)) {
			return plainText;
		}
		EncryptAlgorithm algorithm = getAlgorithm();
		return algorithm.encrypt(plainText, getSecretKey());
	}

	/**
	 * 加密字符串为Base64格式（指定密钥）
	 *
	 * @param plainText 明文
	 * @param secretKey 密钥
	 * @return 加密后的Base64字符串
	 */
	public static String encrypt(String plainText, String secretKey) {
		if (StringUtil.isBlank(plainText)) {
			return plainText;
		}
		EncryptAlgorithm algorithm = getAlgorithm();
		return algorithm.encrypt(plainText, secretKey);
	}

	/**
	 * 解密Base64格式的密文（使用配置的算法）
	 *
	 * @param cipherText Base64格式的密文
	 * @return 解密后的明文
	 */
	public static String decrypt(String cipherText) {
		if (StringUtil.isBlank(cipherText)) {
			return cipherText;
		}
		EncryptAlgorithm algorithm = getAlgorithm();
		return algorithm.decrypt(cipherText, getSecretKey());
	}

	/**
	 * 解密Base64格式的密文（指定密钥）
	 *
	 * @param cipherText Base64格式的密文
	 * @param secretKey  密钥
	 * @return 解密后的明文
	 */
	public static String decrypt(String cipherText, String secretKey) {
		if (StringUtil.isBlank(cipherText)) {
			return cipherText;
		}
		EncryptAlgorithm algorithm = getAlgorithm();
		return algorithm.decrypt(cipherText, secretKey);
	}
}
