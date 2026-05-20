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

import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 滑动窗口加密工具类
 * 用于支持加密字段的模糊查询
 *
 * @author BladeX
 */
public class SlidingUtil {

	/**
	 * 使用默认窗口大小进行滑动窗口加密
	 *
	 * @param text      原始文本
	 * @param secretKey 加密密钥
	 * @return 加密后的拼接字符串
	 */
	public static String segment(String text, String secretKey) {
		return segment(text, EncryptUtil.getWindowSize(), secretKey);
	}

	/**
	 * 使用默认窗口大小和默认密钥进行滑动窗口加密
	 *
	 * @param text 原始文本
	 * @return 加密后的拼接字符串
	 */
	public static String segment(String text) {
		return segment(text, EncryptUtil.getWindowSize(), EncryptUtil.getSecretKey());
	}

	/**
	 * 使用指定窗口大小和默认密钥进行滑动窗口加密
	 *
	 * @param text       原始文本
	 * @param windowSize 窗口大小
	 * @return 加密后的拼接字符串
	 */
	public static String segment(String text, int windowSize) {
		return segment(text, windowSize, EncryptUtil.getSecretKey());
	}

	/**
	 * 按滑动窗口方式将字符串分割为固定长度的子串并加密
	 *
	 * @param text       原始文本
	 * @param windowSize 窗口大小
	 * @param secretKey  加密密钥
	 * @return 加密后的拼接字符串
	 */
	public static String segment(String text, int windowSize, String secretKey) {
		if (StringUtil.isBlank(text) || StringUtil.isBlank(secretKey)) {
			return null;
		}

		// 如果文本长度小于窗口大小，直接加密整个文本
		if (text.length() < windowSize || windowSize <= 0) {
			return EncryptUtil.encrypt(text, secretKey);
		}

		int maxStartIndex = text.length() - windowSize;

		// 使用 Stream 进行滑动窗口处理
		return IntStream.rangeClosed(0, maxStartIndex)
			.mapToObj(i -> text.substring(i, i + windowSize))
			.map(subStr -> EncryptUtil.encrypt(subStr, secretKey))
			.collect(Collectors.joining(StringPool.COMMA));
	}
}
