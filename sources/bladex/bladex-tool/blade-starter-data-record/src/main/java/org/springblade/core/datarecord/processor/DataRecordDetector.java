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
package org.springblade.core.datarecord.processor;

import org.springblade.core.datarecord.annotation.DataRecord;
import org.springblade.core.datarecord.annotation.FieldRecord;

/**
 * 数据审计检测器接口
 * <p>
 * 用于检测表、实体类、字段是否需要进行数据审计
 *
 * @author BladeX
 */
public interface DataRecordDetector {

	/**
	 * 检测表是否需要数据审计
	 *
	 * @param tableName 表名
	 * @return DataRecord注解，如果不需要记录则返回null
	 */
	DataRecord detectDataRecord(String tableName);

	/**
	 * 检测字段是否需要数据审计
	 *
	 * @param tableName 表名
	 * @param fieldName 字段名
	 * @return FieldRecord注解，如果不需要记录则返回null
	 */
	FieldRecord detectFieldRecord(String tableName, String fieldName);

	/**
	 * 检测是否需要进行数据审计
	 *
	 * @param tableName 表名
	 * @return 是否需要记录
	 */
	boolean skipRecord(String tableName);

	/**
	 * 获取表的DataRecord配置
	 *
	 * @param tableName 表名
	 * @return DataRecord配置
	 */
	DataRecord getTableDataRecord(String tableName);

	/**
	 * 清除缓存
	 */
	void clearCache();

	/**
	 * 清除指定表的缓存
	 *
	 * @param tableName 表名
	 */
	void clearTableCache(String tableName);
}
