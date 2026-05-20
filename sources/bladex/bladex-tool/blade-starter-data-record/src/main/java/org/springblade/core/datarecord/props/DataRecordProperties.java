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
package org.springblade.core.datarecord.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据审计配置属性
 *
 * @author BladeX
 */
@Data
@ConfigurationProperties(prefix = "blade.data-record")
public class DataRecordProperties {

	/**
	 * 是否启用数据审计，默认为true
	 */
	private Boolean enabled = true;

	/**
	 * 全局忽略的表名
	 */
	private List<String> ignoreTables = new ArrayList<>();

	/**
	 * 全局忽略的字段名
	 */
	private List<String> ignoreFields = new ArrayList<>();

	/**
	 * 是否记录详细的变更数据，默认为true
	 */
	private Boolean recordDetailedChanges = true;

	/**
	 * 是否记录原始数据，默认为true
	 */
	private Boolean recordRawData = true;

	/**
	 * 获取全局忽略的表名集合（转换为大写以便比较）
	 */
	public List<String> getIgnoreTablesUpperCase() {
		List<String> upperCaseTables = new ArrayList<>();
		for (String table : ignoreTables) {
			if (table != null) {
				upperCaseTables.add(table.toUpperCase());
			}
		}
		return upperCaseTables;
	}

	/**
	 * 获取全局忽略的字段名集合（转换为大写以便比较）
	 */
	public List<String> getIgnoreFieldsUpperCase() {
		List<String> upperCaseFields = new ArrayList<>();
		for (String field : ignoreFields) {
			if (field != null) {
				upperCaseFields.add(field.toUpperCase());
			}
		}
		return upperCaseFields;
	}

	/**
	 * 检查表是否被全局忽略
	 */
	public boolean isTableIgnored(String tableName) {
		if (tableName == null || ignoreTables.isEmpty()) {
			return false;
		}
		String upperTableName = tableName.toUpperCase();
		return getIgnoreTablesUpperCase().contains(upperTableName);
	}

	/**
	 * 检查字段是否被全局忽略
	 */
	public boolean isFieldIgnored(String fieldName) {
		if (fieldName == null || ignoreFields.isEmpty()) {
			return false;
		}
		String upperFieldName = fieldName.toUpperCase();
		return getIgnoreFieldsUpperCase().contains(upperFieldName);
	}
}
