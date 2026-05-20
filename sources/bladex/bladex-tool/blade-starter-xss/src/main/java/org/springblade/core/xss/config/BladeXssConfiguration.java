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

package org.springblade.core.xss.config;

import lombok.RequiredArgsConstructor;
import org.springblade.core.xss.BladeXssProperties;
import org.springblade.core.xss.XssInterceptor;
import org.springblade.core.xss.processor.DefaultXssCleaner;
import org.springblade.core.xss.processor.FormXssClean;
import org.springblade.core.xss.processor.JacksonXssClean;
import org.springblade.core.xss.processor.XssCleaner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Nonnull;

/**
 * jackson xss 配置
 *
 * @author L.cm，BladeX
 */
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(BladeXssProperties.class)
@ConditionalOnProperty(
	prefix = BladeXssProperties.PREFIX,
	name = "enabled",
	havingValue = "true",
	matchIfMissing = true
)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class BladeXssConfiguration implements WebMvcConfigurer {
	private final BladeXssProperties xssProperties;

	@Bean
	@ConditionalOnMissingBean
	public XssCleaner xssCleaner(BladeXssProperties properties) {
		return new DefaultXssCleaner(properties);
	}

	@Bean
	public FormXssClean formXssClean(BladeXssProperties properties,
									 XssCleaner xssCleaner) {
		return new FormXssClean(properties, xssCleaner);
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer xssJacksonCustomizer(BladeXssProperties properties,
																	  XssCleaner xssCleaner) {
		return builder -> builder.deserializerByType(String.class, new JacksonXssClean(properties, xssCleaner));
	}

	@Override
	public void addInterceptors(@Nonnull InterceptorRegistry registry) {
		XssInterceptor interceptor = new XssInterceptor(xssProperties);
		registry.addInterceptor(interceptor)
			.addPathPatterns(xssProperties.getBlockUrl())
			.addPathPatterns(xssProperties.getSkipUrl())
			.order(Ordered.LOWEST_PRECEDENCE);
	}

}
