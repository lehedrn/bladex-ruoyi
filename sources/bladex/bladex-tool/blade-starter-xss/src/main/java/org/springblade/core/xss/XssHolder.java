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

package org.springblade.core.xss;

import lombok.experimental.UtilityClass;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

/**
 * 利用 ThreadLocal 缓存线程间的数据
 *
 * @author L.cm
 */
@UtilityClass
public class XssHolder {
	private static final ThreadLocal<XssIgnoreRules> TL = new ThreadLocal<>();

	/**
	 * 是否开启
	 *
	 * @return boolean
	 */
	public static boolean isEnabled() {
		return Objects.isNull(TL.get());
	}

	/**
	 * 判断是否被忽略
	 *
	 * @return XssCleanIgnore
	 */
	public static boolean isIgnore(String name) {
		XssIgnoreRules ignoreRules = TL.get();
		if (ignoreRules == null) {
			return false;
		}
		String[] ignoreNames = ignoreRules.getNames();
		// 1. 如果没有设置忽略的字段
		if (ignoreNames.length == 0) {
			return true;
		}
		// 2. 指定忽略的属性
		return ObjectUtils.containsElement(ignoreNames, name);
	}

	/**
	 * 标记为开启
	 */
	public static void setIgnore(XssIgnoreRules ignoreRules) {
		TL.set(ignoreRules);
	}

	/**
	 * 关闭 xss 清理
	 */
	public static void remove() {
		TL.remove();
	}

}
