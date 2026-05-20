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

import org.springblade.core.xss.BladeXssProperties;
import org.springblade.core.xss.BladeXssProperties.Mode;
import org.springblade.core.xss.XssType;
import org.springblade.core.xss.XssUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;

/**
 * 默认的 xss 清理器
 *
 * @author L.cm，BladeX
 */
public class DefaultXssCleaner implements XssCleaner {
	private final BladeXssProperties properties;

	public DefaultXssCleaner(BladeXssProperties properties) {
		this.properties = properties;
	}

	private static Document.OutputSettings getOutputSettings(BladeXssProperties properties) {
		return new Document.OutputSettings()
			// 转义
			.escapeMode(Entities.EscapeMode.xhtml)
			// 保留换行
			.prettyPrint(properties.isPrettyPrint());
	}

	@Override
	public String clean(String name, String bodyHtml, XssType type) {
		// 为空直接返回
		if (!StringUtils.hasText(bodyHtml)) {
			return bodyHtml;
		}
		Mode mode = properties.getMode();
		if (Mode.ESCAPE == mode) {
			// html 转义
			return HtmlUtils.htmlEscape(bodyHtml, StandardCharsets.UTF_8.name());
		} else if (Mode.VALIDATE == mode) {
			// 校验
			if (Jsoup.isValid(bodyHtml, XssUtil.SAFE_LIST)) {
				return bodyHtml;
			}
			throw type.getXssException(name, bodyHtml, "Xss validate fail, input value:" + bodyHtml);
		} else {
			if (properties.isEnableEscape()) {
				// 转义
				bodyHtml = Entities.escape(bodyHtml);
			} else {
				// 反转义
				bodyHtml = Entities.unescape(bodyHtml);
			}
			return XssUtil.clean(bodyHtml, getOutputSettings(properties));
		}
	}

}
