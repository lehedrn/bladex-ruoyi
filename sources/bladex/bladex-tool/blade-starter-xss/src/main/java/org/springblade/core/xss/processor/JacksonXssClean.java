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

package org.springblade.core.xss.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.xss.BladeXssProperties;
import org.springblade.core.xss.XssHolder;
import org.springblade.core.xss.XssType;
import org.springblade.core.xss.XssUtil;

import java.io.IOException;

/**
 * jackson xss 处理
 *
 * @author L.cm
 */
@Slf4j
@RequiredArgsConstructor
public class JacksonXssClean extends XssDeserializerBase {
	private final BladeXssProperties properties;
	private final XssCleaner xssCleaner;

	@Override
	public String clean(String name, String text) throws IOException {
		if (text == null) {
			return null;
		}
		// 判断是否忽略
		if (XssHolder.isIgnore(name)) {
			return XssUtil.trim(text, properties.isTrimText());
		}
		String value = xssCleaner.clean(name, XssUtil.trim(text, properties.isTrimText()), XssType.JACKSON);
		log.debug("Json property name:{} value:{} cleaned up by blade-xss, current value is:{}.", name, text, value);
		return value;
	}

}
