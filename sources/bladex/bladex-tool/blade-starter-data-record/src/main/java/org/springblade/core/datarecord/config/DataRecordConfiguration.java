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
package org.springblade.core.datarecord.config;

import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import org.springblade.core.datarecord.processor.BladeDataRecordHandler;
import org.springblade.core.datarecord.processor.DataRecordHandler;
import org.springblade.core.datarecord.interceptor.BladeRecorderInterceptor;
import org.springblade.core.datarecord.processor.DataRecordParser;
import org.springblade.core.datarecord.processor.BladeDataRecordParser;
import org.springblade.core.datarecord.props.DataRecordProperties;
import org.springblade.core.datarecord.processor.DataRecordDetector;
import org.springblade.core.datarecord.processor.BladeDataRecordDetector;
import org.springblade.core.launch.props.BladeProperties;
import org.springblade.core.launch.server.ServerInfo;
import org.springblade.core.mp.config.MybatisPlusConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据审计配置类
 *
 * @author Chill
 */
@Configuration
@ConditionalOnProperty(value = "blade.data-record.enabled", matchIfMissing = true)
@AutoConfigureBefore(MybatisPlusConfiguration.class)
@EnableConfigurationProperties(DataRecordProperties.class)
public class DataRecordConfiguration {

	@Bean
	@ConditionalOnMissingBean(DataChangeRecorderInnerInterceptor.class)
	public DataChangeRecorderInnerInterceptor dataChangeRecorderInnerInterceptor(DataRecordDetector dataRecordDetector,
																				 DataRecordParser dataRecordParser,
																				 DataRecordHandler dataRecordHandler) {
		return new BladeRecorderInterceptor(dataRecordDetector, dataRecordParser, dataRecordHandler);
	}

	@Bean
	@ConditionalOnMissingBean(DataRecordHandler.class)
	public DataRecordHandler dataRecordHandler() {
		return new BladeDataRecordHandler();
	}

	@Bean
	@ConditionalOnMissingBean(DataRecordDetector.class)
	public DataRecordDetector dataRecordDetector(DataRecordProperties dataRecordProperties) {
		return new BladeDataRecordDetector(dataRecordProperties);
	}

	@Bean
	@ConditionalOnMissingBean(DataRecordParser.class)
	public DataRecordParser dataRecordParser(DataRecordDetector dataRecordDetector,
											 DataRecordProperties dataRecordProperties,
											 BladeProperties bladeProperties,
											 ServerInfo serverInfo) {
		return new BladeDataRecordParser(dataRecordDetector, dataRecordProperties, bladeProperties, serverInfo);
	}
}
