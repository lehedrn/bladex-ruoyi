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
package org.springblade.core.i18n.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * I18n配置属性
 *
 * @author BladeX
 */
@Data
@ConfigurationProperties(prefix = I18nProperties.PREFIX)
public class I18nProperties {
	public static final String PREFIX = "blade.i18n";

	/**
	 * 是否启用i18n
	 */
	private boolean enabled = true;

	/**
	 * 默认locale
	 */
	private String defaultLocale = "zh_CN";

	/**
	 * 支持的locales
	 */
	private List<String> supportLocales = new ArrayList<>();

	/**
	 * HTTP头名称
	 */
	private String headerName = "Accept-Language";

	/**
	 * 请求参数名称
	 */
	private String paramName = "lang";

	/**
	 * 消息源配置
	 */
	private MessageSource messageSource = new MessageSource();

	/**
	 * 消息源配置
	 */
	@Data
	public static class MessageSource {
		/**
		 * 国际化基础名列表
		 * 示例：
		 * - i18n/errors
		 * - i18n/messages
		 */
		private List<String> baseNames = List.of("i18n/errors", "i18n/messages");

		/**
		 * 编码
		 */
		private String encoding = "UTF-8";

		/**
		 * 缓存过期时间
		 */
		private Duration cacheDuration = Duration.ofMinutes(30);

		/**
		 * 是否使用代码作为默认消息
		 */
		private boolean useCodeAsDefaultMessage = true;
	}
}
