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
package org.springblade.core.tenant.dynamic;

import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.db.dynamic.creator.DynamicDataSourceProperty;
import org.springblade.core.db.dynamic.processor.DynamicDataSourceProcessor;
import org.springblade.core.tenant.utils.ShardingUtil;
import org.springblade.core.tool.utils.StringUtil;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.springblade.core.tenant.constant.TenantDynamicConstant.*;

/**
 * 租户动态数据源扩展
 * <p>
 * 实现数据源扩展来支持租户级别的动态数据源
 *
 * @author BladeX
 */
@Slf4j
public class TenantDataSourceProcessor implements DynamicDataSourceProcessor {

	@Override
	public Map<String, DataSourceProperty> loadDataSourceProperties(Statement statement, DynamicDataSourceProperties dynamicDataSourceProperties) throws SQLException {
		Map<String, DataSourceProperty> map = new HashMap<>(16);

		// 构建动态数据源
		ResultSet rs = statement.executeQuery(TENANT_DATASOURCE_GROUP_STATEMENT);
		while (rs.next()) {
			int category = rs.getInt("category");
			String tenantId = rs.getString("tenantId");
			String driver = rs.getString("driverClass");
			String url = rs.getString("url");
			String username = rs.getString("username");
			String password = rs.getString("password");
			String shardingConfig = rs.getString("shardingConfig");
			// JDBC直连配置
			if (category == JDBC_CATEGORY && StringUtil.isNoneBlank(tenantId, driver, url, username, password)) {
				DataSourceProperty jdbcProperty = new DataSourceProperty();
				jdbcProperty.setDriverClassName(driver);
				jdbcProperty.setUrl(url);
				jdbcProperty.setUsername(username);
				jdbcProperty.setPassword(password);
				jdbcProperty.setDruid(dynamicDataSourceProperties.getDruid());
				map.put(tenantId, jdbcProperty);
				log.info("Loaded JDBC datasource config for tenant {}", tenantId);
			}
			// Sharding分库分表配置
			else if (category == SHARDING_CATEGORY && StringUtil.isNotBlank(shardingConfig)) {
				DataSource dataSource = ShardingUtil.createDataSource(shardingConfig);
				DynamicDataSourceProperty shardingProperty = new DynamicDataSourceProperty();
				shardingProperty.setTenantId(tenantId);
				shardingProperty.setDataSource(dataSource);
				map.put(tenantId, shardingProperty);
				log.info("Loaded Sharding datasource config for tenant {}", tenantId);
			}
		}
		log.info("Tenant datasource processor loaded {} datasources", map.size());
		return map;
	}
}
