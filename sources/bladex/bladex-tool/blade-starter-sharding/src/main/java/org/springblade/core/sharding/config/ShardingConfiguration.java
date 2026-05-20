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
package org.springblade.core.sharding.config;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceCreatorAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import jakarta.annotation.Resource;
import org.springblade.core.db.dynamic.config.DynamicDataSourceConfiguration;
import org.springblade.core.db.dynamic.processor.DynamicDataSourceProcessor;
import org.springblade.core.sharding.processor.ShardingDataSourceProcessor;
import org.springblade.core.sharding.props.ShardingProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;

/**
 * ShardingSphere与DynamicDatasource配置类
 * 此配置类用于未开启租户模块数据库隔离功能的场景
 *
 * @author Chill
 */
@Configuration
@EnableConfigurationProperties({DynamicDataSourceProperties.class, ShardingProperties.class})
@AutoConfiguration(before = {DruidDataSourceAutoConfigure.class, DynamicDataSourceAutoConfiguration.class, DynamicDataSourceConfiguration.class})
@Import(value = {DynamicDataSourceCreatorAutoConfiguration.class})
@ConditionalOnProperty(value = ShardingProperties.PREFIX + ".enabled", havingValue = "true")
public class ShardingConfiguration {

	@Lazy
	@Resource(name = "shardingSphereDataSource")
	private DataSource shardingSphereDataSource;

	/**
	 * 自定义分库分表动态数据源加载逻辑
	 */
	@Bean
	@ConditionalOnMissingBean
	public DynamicDataSourceProcessor dynamicShardingDataSourceProcessor() {
		return new ShardingDataSourceProcessor(shardingSphereDataSource);
	}


}
