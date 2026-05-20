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
package org.springblade.core.db.dynamic.processor;

import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * 动态数据源扩展接口
 * <p>
 * 其他模块可以实现此接口来提供自定义的数据源启动创建逻辑
 *
 * @author BladeX
 */
public interface DynamicDataSourceProcessor {

	/**
	 * 是否启用该扩展
	 * <p>
	 * 可以通过配置文件或条件判断来控制是否启用
	 *
	 * @return true表示启用，false表示禁用
	 */
	default boolean isEnabled() {
		return true;
	}

	/**
	 * 获取优先级
	 * <p>
	 * 数值越小优先级越高，默认值为 100
	 *
	 * @return 优先级数值
	 */
	default int getOrder() {
		return 100;
	}

	/**
	 * 执行自定义数据源创建逻辑
	 * <p>
	 * 通过执行SQL语句或其他方式获取数据源配置信息，
	 * 并构建对应的数据源属性映射
	 *
	 * @param statement                   数据库连接语句对象
	 * @param dynamicDataSourceProperties 动态数据源配置属性
	 * @return 数据源配置映射，key为数据源名称，value为数据源属性
	 * @throws SQLException 数据库操作异常
	 */
	Map<String, DataSourceProperty> loadDataSourceProperties(Statement statement, DynamicDataSourceProperties dynamicDataSourceProperties) throws SQLException;
}
