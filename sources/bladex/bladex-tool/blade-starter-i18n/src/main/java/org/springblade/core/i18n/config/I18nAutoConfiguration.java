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
package org.springblade.core.i18n.config;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.i18n.props.I18nProperties;
import org.springblade.core.i18n.resolver.I18nLocaleResolver;
import org.springblade.core.i18n.service.I18nService;
import org.springblade.core.i18n.interceptor.I18nInterceptor;
import org.springblade.core.tool.utils.Func;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * I18n自动配置类
 *
 * @author BladeX
 */
@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = I18nProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class I18nAutoConfiguration {

	/**
	 * 类路径前缀常量
	 */
	private static final String CLASSPATH_PREFIX = "classpath:";

	/**
	 * 国际化配置类
	 */
	private final I18nProperties properties;

	/**
	 * 覆盖Spring配置的MessageSource
	 */
	@Bean
	@Primary
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		I18nProperties.MessageSource config = properties.getMessageSource();

		// 设置基础名列表
		String[] baseNames = config.getBaseNames().stream()
			.map(name -> name.startsWith(CLASSPATH_PREFIX) ? name : CLASSPATH_PREFIX + name)
			.toArray(String[]::new);
		messageSource.setBasenames(baseNames);
		// 设置编码
		messageSource.setDefaultEncoding(config.getEncoding());
		// 设置缓存时间
		if (config.getCacheDuration() != null) {
			messageSource.setCacheSeconds(Func.toInt(config.getCacheDuration().getSeconds()));
		}
		// 设置是否使用代码作为默认消息
		messageSource.setUseCodeAsDefaultMessage(config.isUseCodeAsDefaultMessage());
		return messageSource;
	}

	/**
	 * 覆盖Spring配置的Locale解析器
	 */
	@Bean
	@Primary
	public LocaleResolver localeResolver() {
		return new I18nLocaleResolver(properties);
	}

	/**
	 * 配置I18n服务
	 */
	@Bean
	@ConditionalOnMissingBean
	public I18nService i18nService(MessageSource messageSource, LocaleResolver localeResolver) {
		return new I18nService(messageSource, localeResolver, properties);
	}

	/**
	 * 配置I18n拦截器
	 */
	@Bean
	@ConditionalOnMissingBean
	public I18nInterceptor i18nInterceptor() {
		return new I18nInterceptor(properties);
	}

	/**
	 * 注册I18n拦截器
	 */
	@Bean
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
	public WebMvcConfigurer i18nWebMvcConfigurer(I18nInterceptor i18nInterceptor) {
		return new WebMvcConfigurer() {
			@Override
			public void addInterceptors(@Nonnull InterceptorRegistry registry) {
				registry.addInterceptor(i18nInterceptor)
					.addPathPatterns("/**")
					.order(0);
			}
		};
	}

}
