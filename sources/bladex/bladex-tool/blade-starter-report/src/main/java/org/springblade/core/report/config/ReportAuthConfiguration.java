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
package org.springblade.core.report.config;

import jakarta.servlet.Filter;
import org.springblade.core.report.filter.UReportAuthFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

/**
 * UReport认证配置类
 *
 * @author BladeX
 */
@Order
@AutoConfiguration
@ConditionalOnProperty(value = "report.auth", havingValue = "true", matchIfMissing = true)
public class ReportAuthConfiguration {

	@Bean
	public FilterRegistrationBean<Filter> uReportAuthFilter() {
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new UReportAuthFilter());
		registration.addUrlPatterns("/ureport/*");
		registration.setName("uReportAuthFilter");
		registration.setOrder(1);
		return registration;
	}

}
