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
package org.springblade.core.xss;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;

/**
 * XSS工具类
 *
 * @author BladeX
 */
public class XssUtil {

	/**
	 * 定义安全的HTML白名单
	 */
	public static final Safelist SAFE_LIST = Safelist.basicWithImages(); // 允许基本的HTML标签和图片

	// 初始化白名单，可以根据需求添加或删除标签和属性
	static {
		// 添加额外的标签和属性
		SAFE_LIST.addTags("span", "div"); // 示例：添加额外的标签
		SAFE_LIST.addAttributes("div", "class"); // 示例：为特定标签添加安全属性
	}

	/**
	 * trim 字符串
	 *
	 * @param text text
	 * @return 清理后的 text
	 */
	public static String trim(String text, boolean trim) {
		return trim ? text.trim() : text;
	}

	/**
	 * 清洁输入字符串，去除所有潜在的XSS攻击
	 *
	 * @param value 输入字符串
	 * @return 清理后的字符串
	 */
	public static String clean(String value) {
		if (StringUtil.isNotBlank(value)) {
			return Jsoup.clean(value, SAFE_LIST);
		}
		return StringPool.EMPTY;
	}

	/**
	 * 清洁输入字符串，去除所有潜在的XSS攻击
	 *
	 * @param value    输入字符串
	 * @param settings 输入设置
	 * @return 清理后的字符串
	 */
	public static String clean(String value, Document.OutputSettings settings) {
		if (StringUtil.isNotBlank(value)) {
			return Jsoup.clean(value, StringPool.EMPTY, SAFE_LIST, settings);
		}
		return StringPool.EMPTY;
	}

	/**
	 * 检查输入字符串是否通过XSS检测
	 *
	 * @param value 输入字符串
	 * @return 是否通过XSS检测
	 */
	public static boolean isPass(String value) {
		if (StringUtil.isBlank(value)) {
			return false;
		}
		// 清洁输入后得到的结果与输入字符串对比
		String cleanedString = Jsoup.clean(value, SAFE_LIST);
		return cleanedString.equals(value);
	}
}
