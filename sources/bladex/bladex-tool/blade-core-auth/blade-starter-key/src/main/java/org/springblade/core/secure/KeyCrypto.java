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
package org.springblade.core.secure;

import org.springblade.core.secure.constant.ApiKeyConstant;
import org.springblade.core.tool.utils.Charsets;
import org.springblade.core.tool.utils.Exceptions;
import org.springblade.core.tool.utils.HexUtil;
import org.springblade.core.tool.utils.Pkcs7Encoder;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

/**
 * Key加解密工具类
 *
 * @author BladeX
 */
public class KeyCrypto {
	public static final Charset DEFAULT_CHARSET = Charsets.UTF_8;

	public static String genAesKey() {
		return StringUtil.random(32);
	}

	/**
	 * 生成 API Key
	 *
	 * @param cryptoKey 加密密钥
	 * @return 带前缀的加密 Key
	 */
	public static String generateKey(String cryptoKey) {
		return ApiKeyConstant.API_KEY_PREFIX + encryptToHex(StringUtil.randomUUID(), cryptoKey);
	}

	/**
	 * 解析 API Key
	 *
	 * @param key       带前缀的加密 Key
	 * @param cryptoKey 加密密钥
	 * @return 解密后的原始值
	 */
	@Nullable
	public static String parseKey(@Nullable String key, String cryptoKey) {
		if (StringUtil.isBlank(key) || !key.startsWith(ApiKeyConstant.API_KEY_PREFIX)) {
			return null;
		}
		try {
			String encryptedPart = key.substring(ApiKeyConstant.API_KEY_PREFIX.length());
			return decryptFormHexToString(encryptedPart, cryptoKey);
		} catch (Exception e) {
			return null;
		}
	}

	public static String encryptToHex(String content, String aesTextKey) {
		return HexUtil.encodeToString(encrypt(content, aesTextKey));
	}

	public static String encryptToHex(byte[] content, String aesTextKey) {
		return HexUtil.encodeToString(encrypt(content, aesTextKey));
	}

	@Nullable
	public static String decryptFormHexToString(@Nullable String content, String aesTextKey) {
		byte[] hexBytes = decryptFormHex(content, aesTextKey);
		if (hexBytes == null) {
			return null;
		}
		return new String(hexBytes, DEFAULT_CHARSET);
	}

	@Nullable
	public static byte[] decryptFormHex(@Nullable String content, String aesTextKey) {
		if (StringUtil.isBlank(content)) {
			return null;
		}
		return decryptFormHex(content.getBytes(DEFAULT_CHARSET), aesTextKey);
	}

	public static byte[] decryptFormHex(byte[] content, String aesTextKey) {
		return decrypt(HexUtil.decode(content), aesTextKey);
	}

	public static byte[] encrypt(String content, String aesTextKey) {
		return encrypt(content.getBytes(DEFAULT_CHARSET), aesTextKey);
	}

	public static byte[] encrypt(String content, Charset charset, String aesTextKey) {
		return encrypt(content.getBytes(charset), aesTextKey);
	}

	public static byte[] encrypt(byte[] content, String aesTextKey) {
		return encrypt(content, Objects.requireNonNull(aesTextKey).getBytes(DEFAULT_CHARSET));
	}

	public static byte[] encrypt(byte[] content, byte[] aesKey) {
		return aes(Pkcs7Encoder.encode(content), aesKey, Cipher.ENCRYPT_MODE);
	}

	public static String decryptToString(byte[] content, String aesTextKey) {
		return new String(decrypt(content, aesTextKey), DEFAULT_CHARSET);
	}

	public static byte[] decrypt(byte[] content, String aesTextKey) {
		return decrypt(content, Objects.requireNonNull(aesTextKey).getBytes(DEFAULT_CHARSET));
	}

	public static byte[] decrypt(byte[] encrypted, byte[] aesKey) {
		return Pkcs7Encoder.decode(aes(encrypted, aesKey, Cipher.DECRYPT_MODE));
	}

	private static byte[] aes(byte[] encrypted, byte[] aesKey, int mode) {
		Assert.isTrue(aesKey.length == 32, "IllegalAesKey, aesKey's length must be 32");
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
			IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
			cipher.init(mode, keySpec, iv);
			return cipher.doFinal(encrypted);
		} catch (Exception e) {
			throw Exceptions.unchecked(e);
		}
	}
}
