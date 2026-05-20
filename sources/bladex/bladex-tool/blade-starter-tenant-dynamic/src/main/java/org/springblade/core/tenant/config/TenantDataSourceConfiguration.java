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
package org.springblade.core.tenant.config;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.dynamic.datasource.aop.DynamicDataSourceAnnotationAdvisor;
import com.baomidou.dynamic.datasource.creator.druid.DruidDataSourceCreator;
import com.baomidou.dynamic.datasource.processor.DsJakartaHeaderProcessor;
import com.baomidou.dynamic.datasource.processor.DsJakartaSessionProcessor;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.processor.DsSpelExpressionProcessor;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceCreatorAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springblade.core.db.dynamic.config.DynamicDataSourceConfiguration;
import org.springblade.core.db.dynamic.processor.DynamicDataSourceProcessor;
import org.springblade.core.tenant.annotation.TenantDS;
import org.springblade.core.tenant.dynamic.*;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.springblade.core.tenant.constant.TenantDynamicConstant.TENANT_DYNAMIC_DATASOURCE_PROP;
import static org.springblade.core.tenant.constant.TenantDynamicConstant.TENANT_DYNAMIC_GLOBAL_PROP;

/**
 * 多租户数据源配置类
 *
 * @author Chill
 */
@RequiredArgsConstructor
@EnableConfigurationProperties({DataSourceProperties.class, DynamicDataSourceProperties.class})
@AutoConfiguration(before = {DruidDataSourceAutoConfigure.class, DynamicDataSourceAutoConfiguration.class, DynamicDataSourceConfiguration.class})
@Import(value = {DynamicDataSourceCreatorAutoConfiguration.class})
@ConditionalOnProperty(value = TENANT_DYNAMIC_DATASOURCE_PROP, havingValue = "true")
public class TenantDataSourceConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public DsProcessor dsProcessor() {
		DsProcessor headerProcessor = new DsJakartaHeaderProcessor();
		DsProcessor sessionProcessor = new DsJakartaSessionProcessor();
		DsTenantIdProcessor tenantIdProcessor = new DsTenantIdProcessor();
		DsSpelExpressionProcessor spelExpressionProcessor = new DsSpelExpressionProcessor();
		headerProcessor.setNextProcessor(sessionProcessor);
		sessionProcessor.setNextProcessor(tenantIdProcessor);
		tenantIdProcessor.setNextProcessor(spelExpressionProcessor);
		return headerProcessor;
	}

	@Bean
	@ConditionalOnMissingBean
	public DynamicDataSourceProcessor dynamicTenantDataSourceProcessor() {
		return new TenantDataSourceProcessor();
	}

	@Bean
	@ConditionalOnMissingBean
	public TenantDataSourceAnnotationInterceptor tenantDataSourceAnnotationInterceptor() {
		return new TenantDataSourceAnnotationInterceptor();
	}

	@Bean
	@ConditionalOnMissingBean
	@Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
	public DynamicDataSourceAnnotationAdvisor dynamicTenantDatasourceAnnotationAdvisor(TenantDataSourceAnnotationInterceptor tenantDataSourceAnnotationInterceptor, DynamicDataSourceProperties dynamicDataSourceProperties) {
		DynamicDataSourceAnnotationAdvisor advisor = new DynamicDataSourceAnnotationAdvisor(tenantDataSourceAnnotationInterceptor, TenantDS.class);
		advisor.setOrder(dynamicDataSourceProperties.getAop().getOrder());
		return advisor;
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(value = TENANT_DYNAMIC_GLOBAL_PROP, havingValue = "true")
	public TenantDataSourceGlobalInterceptor tenantDataSourceGlobalInterceptor() {
		return new TenantDataSourceGlobalInterceptor();
	}

	@Bean
	@ConditionalOnMissingBean
	@Role(value = BeanDefinition.ROLE_INFRASTRUCTURE)
	@ConditionalOnProperty(value = TENANT_DYNAMIC_GLOBAL_PROP, havingValue = "true")
	public TenantDataSourceGlobalAdvisor tenantDataSourceGlobalAdvisor(TenantDataSourceGlobalInterceptor tenantDataSourceGlobalInterceptor, DynamicDataSourceProperties dynamicDataSourceProperties) {
		TenantDataSourceGlobalAdvisor advisor = new TenantDataSourceGlobalAdvisor(tenantDataSourceGlobalInterceptor);
		advisor.setOrder(dynamicDataSourceProperties.getAop().getOrder() + 1);
		return advisor;
	}

	@Order
	@AutoConfiguration
	@AllArgsConstructor
	@ConditionalOnProperty(value = TENANT_DYNAMIC_DATASOURCE_PROP, havingValue = "true")
	public static class TenantDataSourceAnnotationConfiguration implements SmartInitializingSingleton {

		private final TenantDataSourceAnnotationInterceptor tenantDataSourceAnnotationInterceptor;

		private final DataSource dataSource;
		private final DruidDataSourceCreator dataSourceCreator;
		private final JdbcTemplate jdbcTemplate;

		@Override
		public void afterSingletonsInstantiated() {
			TenantDataSourceHolder tenantDataSourceHolder = new TenantDataSourceHolder(dataSource, dataSourceCreator, jdbcTemplate);
			tenantDataSourceAnnotationInterceptor.setHolder(tenantDataSourceHolder);
		}
	}

	@Order
	@AutoConfiguration
	@AllArgsConstructor
	@ConditionalOnProperty(value = TENANT_DYNAMIC_GLOBAL_PROP, havingValue = "true")
	public static class TenantDataSourceGlobalConfiguration implements SmartInitializingSingleton {

		private final TenantDataSourceGlobalInterceptor tenantDataSourceGlobalInterceptor;

		private final DataSource dataSource;
		private final DruidDataSourceCreator dataSourceCreator;
		private final JdbcTemplate jdbcTemplate;

		@Override
		public void afterSingletonsInstantiated() {
			TenantDataSourceHolder tenantDataSourceHolder = new TenantDataSourceHolder(dataSource, dataSourceCreator, jdbcTemplate);
			tenantDataSourceGlobalInterceptor.setHolder(tenantDataSourceHolder);
		}
	}

}
