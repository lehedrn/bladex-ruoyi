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
package org.springblade.core.sharding.processor;

import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.db.dynamic.creator.DynamicDataSourceProperty;
import org.springblade.core.db.dynamic.processor.DynamicDataSourceProcessor;
import org.springblade.core.sharding.constant.ShardingConstant;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Sharding动态数据源扩展
 *
 * @author BladeX
 */
@Slf4j
@RequiredArgsConstructor
public class ShardingDataSourceProcessor implements DynamicDataSourceProcessor {

	private final DataSource shardingSphereDataSource;

	@Override
	public Map<String, DataSourceProperty> loadDataSourceProperties(Statement statement, DynamicDataSourceProperties dynamicDataSourceProperties) throws SQLException {
		Map<String, DataSourceProperty> map = new HashMap<>(16);

		// 创建 Sharding数据源 对象
		DynamicDataSourceProperty shardingProperty = new DynamicDataSourceProperty();
		shardingProperty.setDataSource(shardingSphereDataSource);
		map.put(ShardingConstant.SHARDING_DATASOURCE_KEY, shardingProperty);

		log.info("Sharding datasource processor loaded {} datasources", map.size());
		return map;
	}
}
