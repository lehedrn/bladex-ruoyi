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
package org.springblade.core.tool.utils;

import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

/**
 * SM4对称加密、解密工具类。
 *
 * @author BladeX
 */
public class SM4Util {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * SM4 密钥长度，必须为 16 字节（128 位）。
	 */
	private static final int KEY_LENGTH = 16;

	/**
	 * 生成一个随机的 SM4 密钥。
	 *
	 * @return 16进制编码的密钥字符串
	 */
	public static String generateKey() {
		byte[] key = new byte[KEY_LENGTH];
		new SecureRandom().nextBytes(key);
		return Hex.toHexString(key);
	}

	/**
	 * 使用SM4算法加密文本。
	 *
	 * @param input 待加密的文本
	 * @param key   16进制编码的密钥
	 * @return 16进制编码的加密后字符串
	 */
	public static String encrypt(String input, String key) {
		if (StringUtil.isAnyBlank(input, key)) {
			return null;
		}
		byte[] keyBytes = Hex.decode(key);
		byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
		byte[] encryptedBytes = encrypt(inputBytes, keyBytes);
		return (encryptedBytes != null) ? Hex.toHexString(encryptedBytes) : null;
	}

	/**
	 * 使用SM4算法加密数据。
	 *
	 * @param input 待加密的字节数组
	 * @param key   密钥字节数组
	 * @return 加密后的字节数组
	 */
	public static byte[] encrypt(byte[] input, byte[] key) {
		if (input == null || key == null) {
			return null;
		}
		// 校验密钥长度
		if (key.length != KEY_LENGTH) {
			throw new IllegalArgumentException("SM4 key must be 16 bytes long.");
		}
		try {
			// 创建 SM4 引擎，并使用 PKCS7 填充模式
			PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new SM4Engine());
			KeyParameter keyParameter = new KeyParameter(key);
			// 初始化为加密模式
			cipher.init(true, keyParameter);
			// 计算输出缓冲区大小
			int outputSize = cipher.getOutputSize(input.length);
			byte[] output = new byte[outputSize];
			// 处理加密
			int processedLength = cipher.processBytes(input, 0, input.length, output, 0);
			// 处理最后一块
			int finalLength = cipher.doFinal(output, processedLength);
			// 返回实际加密后的数据
			return Arrays.copyOf(output, processedLength + finalLength);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 使用SM4算法解密文本。
	 *
	 * @param encrypted 16进制编码的加密字符串
	 * @param key       16进制编码的密钥
	 * @return 解密后的原文
	 */
	public static String decrypt(String encrypted, String key) {
		if (StringUtil.isAnyBlank(encrypted, key)) {
			return null;
		}
		byte[] keyBytes = Hex.decode(key);
		byte[] encryptedBytes = Hex.decode(encrypted);
		byte[] decryptedBytes = decrypt(encryptedBytes, keyBytes);
		return (decryptedBytes != null) ? new String(decryptedBytes, StandardCharsets.UTF_8) : null;
	}

	/**
	 * 使用SM4算法解密数据。
	 *
	 * @param encrypted 待解密的字节数组
	 * @param key       密钥字节数组
	 * @return 解密后的字节数组
	 */
	public static byte[] decrypt(byte[] encrypted, byte[] key) {
		if (encrypted == null || key == null) {
			return null;
		}
		// 校验密钥长度
		if (key.length != KEY_LENGTH) {
			throw new IllegalArgumentException("SM4 key must be 16 bytes long.");
		}
		try {
			// 创建 SM4 引擎，并使用 PKCS7 填充模式
			PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new SM4Engine());
			KeyParameter keyParameter = new KeyParameter(key);
			// 初始化为解密模式
			cipher.init(false, keyParameter);
			// 计算输出缓冲区大小
			int outputSize = cipher.getOutputSize(encrypted.length);
			byte[] output = new byte[outputSize];
			// 处理解密
			int processedLength = cipher.processBytes(encrypted, 0, encrypted.length, output, 0);
			// 处理最后一块
			int finalLength = cipher.doFinal(output, processedLength);
			// 返回实际解密后的数据
			return Arrays.copyOf(output, processedLength + finalLength);
		} catch (Exception e) {
			return null;
		}
	}

}
