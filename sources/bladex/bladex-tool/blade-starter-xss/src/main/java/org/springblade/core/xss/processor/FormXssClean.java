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
import org.springblade.core.auto.annotation.AutoIgnore;
import org.springblade.core.xss.BladeXssProperties;
import org.springblade.core.xss.XssHolder;
import org.springblade.core.xss.XssType;
import org.springblade.core.xss.XssUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;

/**
 * 表单 xss 处理
 *
 * @author L.cm
 */
@AutoIgnore
@ControllerAdvice
@ConditionalOnProperty(
	prefix = BladeXssProperties.PREFIX,
	name = "enabled",
	havingValue = "true",
	matchIfMissing = true
)
@RequiredArgsConstructor
public class FormXssClean {
	private final BladeXssProperties properties;
	private final XssCleaner xssCleaner;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// 处理前端传来的表单字符串
		binder.registerCustomEditor(String.class, new StringPropertiesEditor(xssCleaner, properties));
	}

	@Slf4j
	@RequiredArgsConstructor
	public static class StringPropertiesEditor extends PropertyEditorSupport {
		private final XssCleaner xssCleaner;
		private final BladeXssProperties properties;

		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			if (text == null) {
				setValue(null);
			} else if (XssHolder.isEnabled()) {
				String value = xssCleaner.clean(XssUtil.trim(text, properties.isTrimText()), XssType.FORM);
				setValue(value);
				log.debug("Request parameter value:{} cleaned up by blade-xss, current value is:{}.", text, value);
			} else {
				setValue(XssUtil.trim(text, properties.isTrimText()));
			}
		}
	}

}
