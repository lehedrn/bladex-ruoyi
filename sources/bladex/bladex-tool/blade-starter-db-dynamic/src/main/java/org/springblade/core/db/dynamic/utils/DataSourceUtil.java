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
package org.springblade.core.db.dynamic.utils;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.db.dynamic.exception.DynamicDataSourceException;
import org.springblade.core.db.dynamic.holder.DynamicDataSourceHolder;
import org.springblade.core.tool.utils.StringUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.function.Supplier;

/**
 * 数据源工具类
 *
 * @author BladeX
 */
@Slf4j
public class DataSourceUtil {

	/**
	 * 动态切换数据源并处理
	 *
	 * @param dbName   数据源名称
	 * @param supplier supplier
	 * @param <R>      泛型
	 * @return R 函数返回
	 */
	public static <R> R use(String dbName, Supplier<R> supplier) {
		return use(dbName, null, supplier);
	}

	/**
	 * 动态切换数据源并处理
	 *
	 * @param dbName   数据源名称
	 * @param holder   数据源持有者
	 * @param supplier supplier
	 * @param <R>      泛型
	 * @return R 函数返回
	 */
	public static <R> R use(String dbName, DynamicDataSourceHolder holder, Supplier<R> supplier) {
		try {
			if (StringUtil.isNotBlank(dbName)) {
				if (holder != null) {
					holder.handleDataSource(dbName);
				}
				DynamicDataSourceContextHolder.push(dbName);
			}
			return supplier.get();
		} catch (Exception exception) {
			throw new DynamicDataSourceException(exception.getMessage());
		} finally {
			if (StringUtil.isNotBlank(dbName)) {
				DynamicDataSourceContextHolder.poll();
			}
		}
	}


	/**
	 * 测试数据库连接
	 *
	 * @param driverClass 数据库驱动类
	 * @param url         数据库连接地址
	 * @param username    用户名
	 * @param password    密码
	 * @return Boolean 连接测试结果
	 */
	public static Boolean dbTest(String driverClass, String url, String username, String password) {
		Connection conn = null;
		try {
			//测试驱动类
			Class.forName(driverClass);
			//创建连接
			conn = DriverManager.getConnection(url, username, password);
			conn.setAutoCommit(Boolean.FALSE);
			return true;
		} catch (Exception ex) {
			log.warn("Database connection test failed: {}", ex.getMessage());
			return false;
		} finally {
			//关闭连接
			dbClose(conn);
		}
	}

	/**
	 * 关闭数据库链接
	 *
	 * @param conn 数据库连接
	 */
	private static void dbClose(Connection conn) {
		try {
			//关闭数据源连接
			if (conn != null) {
				conn.close();
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}

}
