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
package org.springblade.core.db.dynamic.provider;

import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.creator.druid.DruidConfig;
import com.baomidou.dynamic.datasource.provider.AbstractJdbcDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.toolkit.DsStrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.db.dynamic.creator.DynamicDataSourceProperty;
import org.springblade.core.db.dynamic.processor.DynamicDataSourceProcessor;
import org.springblade.core.db.dynamic.utils.DataSourceUtil;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.StringUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 动态数据源初始加载
 *
 * @author BladeX
 */
@Slf4j
public class DynamicDataSourceAutoProvider extends AbstractJdbcDataSourceProvider {

	private final String driverClassName;
	private final String url;
	private final String username;
	private final String password;
	private final DefaultDataSourceCreator dataSourceCreator;
	private final DynamicDataSourceProperties dynamicDataSourceProperties;
	private final List<DynamicDataSourceProcessor> processors;

	public DynamicDataSourceAutoProvider(DefaultDataSourceCreator dataSourceCreator, DynamicDataSourceProperties dynamicDataSourceProperties, String driverClassName, String url, String username, String password) {
		this(dataSourceCreator, dynamicDataSourceProperties, driverClassName, url, username, password, Collections.emptyList());
	}

	public DynamicDataSourceAutoProvider(DefaultDataSourceCreator dataSourceCreator, DynamicDataSourceProperties dynamicDataSourceProperties, String driverClassName, String url, String username, String password, List<DynamicDataSourceProcessor> processors) {
		super(dataSourceCreator, driverClassName, url, username, password);
		this.dataSourceCreator = dataSourceCreator;
		this.dynamicDataSourceProperties = dynamicDataSourceProperties;
		this.driverClassName = driverClassName;
		this.url = url;
		this.username = username;
		this.password = password;
		this.processors = Optional.ofNullable(processors)
			.map(ext -> ext.stream()
				.filter(DynamicDataSourceProcessor::isEnabled)
				.sorted(Comparator.comparingInt(DynamicDataSourceProcessor::getOrder))
				.collect(Collectors.toList()))
			.orElse(Collections.emptyList());
	}

	@Override
	protected Map<String, DataSourceProperty> executeStmt(Statement statement) {
		// 构建数据源集合
		Map<String, DataSourceProperty> map = new HashMap<>(16);
		// 构建主数据源
		DruidConfig druid = dynamicDataSourceProperties.getDruid();
		druid.setProxyFilters("sqlLogInterceptor");
		DataSourceProperty masterProperty = new DataSourceProperty();
		masterProperty.setDriverClassName(driverClassName);
		masterProperty.setUrl(url);
		masterProperty.setUsername(username);
		masterProperty.setPassword(password);
		masterProperty.setDruid(druid);
		map.put(dynamicDataSourceProperties.getPrimary(), masterProperty);
		// 构建yml数据源
		Map<String, DataSourceProperty> datasource = dynamicDataSourceProperties.getDatasource();
		if (!datasource.isEmpty()) {
			map.putAll(datasource);
		}
		// 执行数据源扩展逻辑
		executeProcessors(statement, map);
		return map;
	}

	/**
	 * 执行所有数据源扩展
	 *
	 * @param statement     数据库连接语句对象
	 * @param dataSourceMap 数据源映射集合
	 */
	private void executeProcessors(Statement statement, Map<String, DataSourceProperty> dataSourceMap) {
		// 如果没有配置任何数据源扩展，则直接返回
		if (processors.isEmpty()) {
			log.warn("No datasource processors found");
			return;
		}
		// 遍历所有数据源扩展并执行
		processors.forEach(extension -> {
			try {
				Map<String, DataSourceProperty> customDataSources = extension.loadDataSourceProperties(statement, dynamicDataSourceProperties);
				if (customDataSources != null && !customDataSources.isEmpty()) {
					dataSourceMap.putAll(customDataSources);
				}
			} catch (Exception e) {
				log.error("Exception occurred while executing extension", e);
			}
		});
	}

	@Override
	public Map<String, DataSource> loadDataSources() {
		Connection conn = null;
		Statement stmt = null;
		try {
			// 由于 SPI 的支持，现在已无需显示加载驱动了
			// 但在用户显示配置的情况下，进行主动加载
			if (!DsStrUtils.isEmpty(driverClassName)) {
				Class.forName(driverClassName);
				log.info("Database driver loaded successfully");
			}
			conn = DriverManager.getConnection(url, username, password);
			log.info("Database connection established");
			stmt = conn.createStatement();
			Map<String, DataSourceProperty> dataSourcePropertiesMap = executeStmt(stmt);
			return createDataSourceMap(dataSourcePropertiesMap);
		} catch (Exception e) {
			log.error("Failed to establish database connection", e);
		} finally {
			closeResource(conn);
			closeResource(stmt);
		}
		return null;
	}

	/**
	 * 关闭资源
	 *
	 * @param con 资源
	 */
	private static void closeResource(AutoCloseable con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ex) {
				log.error("Failed to close connection", ex);
			} catch (Throwable ex) {
				log.error("Exception occurred while closing connection", ex);
			}
		}
	}

	@Override
	protected Map<String, DataSource> createDataSourceMap(Map<String, DataSourceProperty> dataSourcePropertiesMap) {
		Map<String, DataSource> dataSourceMap = new HashMap<>(dataSourcePropertiesMap.size() * 2);
		for (Map.Entry<String, DataSourceProperty> item : dataSourcePropertiesMap.entrySet()) {
			String dsName = item.getKey();
			DataSourceProperty dataSourceProperty = item.getValue();
			String poolName = dataSourceProperty.getPoolName();
			if (StringUtil.isBlank(poolName)) {
				poolName = dsName;
			}
			DynamicDataSourceProperty dynamicDataSourceProperty = BeanUtil.copyProperties(dataSourceProperty, DynamicDataSourceProperty.class);
			DataSource dataSource = Objects.requireNonNull(dynamicDataSourceProperty).getDataSource();
			if (dataSource == null) {
				dataSourceProperty.setPoolName(poolName);
				// 数据库连接测试 - 获取连接参数并验证
				String driverClassName = dataSourceProperty.getDriverClassName();
				String url = dataSourceProperty.getUrl();
				String username = dataSourceProperty.getUsername();
				String password = dataSourceProperty.getPassword();
				// 进行连接测试，如果失败则跳过此数据源的创建
				if (!DataSourceUtil.dbTest(driverClassName, url, username, password)) {
					log.warn("Connection link failed for datasource [{}], skipping creation", dsName);
					continue;
				}
				log.info("Connection link passed for datasource [{}], creating datasource", dsName);
				dataSourceMap.put(dsName, dataSourceCreator.createDataSource(dataSourceProperty));
			} else {
				log.info("Custom datasource [{}] detected, creating datasource", dsName);
				dataSourceMap.put(dsName, dataSource);
			}
		}
		return dataSourceMap;
	}

}
