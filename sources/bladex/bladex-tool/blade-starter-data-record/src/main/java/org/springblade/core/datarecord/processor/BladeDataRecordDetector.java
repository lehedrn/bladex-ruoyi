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

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.datarecord.annotation.DataRecord;
import org.springblade.core.datarecord.annotation.DataRecordLevel;
import org.springblade.core.datarecord.annotation.FieldRecord;
import org.springblade.core.datarecord.props.DataRecordProperties;
import org.springblade.core.datarecord.expression.DataRecordEvaluator;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认数据审计检测器实现
 * <p>
 * 用于检测表、实体类、字段是否需要进行数据审计
 *
 * @author BladeX
 */
@Slf4j
@RequiredArgsConstructor
public class BladeDataRecordDetector implements DataRecordDetector {

	private final DataRecordProperties dataRecordProperties;

	/**
	 * 表名与DataRecord注解的缓存
	 */
	private final ConcurrentHashMap<String, DataRecord> tableRecordCache = new ConcurrentHashMap<>();

	/**
	 * 表名与实体类的缓存
	 */
	private final ConcurrentHashMap<String, Class<?>> tableEntityCache = new ConcurrentHashMap<>();

	@Override
	public DataRecord detectDataRecord(String tableName) {
		if (tableName == null || tableName.trim().isEmpty()) {
			return null;
		}

		// 先从缓存中获取
		DataRecord cachedRecord = tableRecordCache.get(tableName);
		if (cachedRecord != null) {
			// 如果是空标记，返回null
			if (isEmptyDataRecord(cachedRecord)) {
				return null;
			}
			return cachedRecord;
		}

		try {
			// 通过MyBatis-Plus获取表信息
			TableInfo tableInfo = getTableInfoByTableName(tableName);
			if (tableInfo != null) {
				Class<?> entityClass = tableInfo.getEntityType();
				DataRecord dataRecord = entityClass.getAnnotation(DataRecord.class);

				if (dataRecord != null) {
					// 验证实体级别的条件表达式
					validateConditionExpression(dataRecord.condition(), "实体 " + entityClass.getSimpleName());

					// 缓存结果
					tableRecordCache.put(tableName, dataRecord);
					tableEntityCache.put(tableName, entityClass);
					return dataRecord;
				}
			}

			// 如果没有找到注解，缓存一个空标记以避免重复检查
			tableRecordCache.put(tableName, createEmptyDataRecord());
			return null;

		} catch (Exception e) {
			log.debug("检测表 {} 的数据审计注解时发生异常: {}", tableName, e.getMessage());
			return null;
		}
	}

	@Override
	public FieldRecord detectFieldRecord(String tableName, String fieldName) {
		if (tableName == null || fieldName == null) {
			return null;
		}

		try {
			Class<?> entityClass = tableEntityCache.get(tableName);
			if (entityClass == null) {
				// 先检测表级别的注解以填充缓存
				detectDataRecord(tableName);
				entityClass = tableEntityCache.get(tableName);
			}

			if (entityClass != null) {
				// 查找字段上的 @FieldRecord 注解
				Field field = findField(entityClass, fieldName);
				if (field != null) {
					FieldRecord fieldRecord = field.getAnnotation(FieldRecord.class);
					if (fieldRecord != null) {
						// 验证字段级别的条件表达式
						validateConditionExpression(fieldRecord.condition(), "字段 " + entityClass.getSimpleName() + "." + fieldName);
						return fieldRecord;
					}
				}
			}

			return null;
		} catch (Exception e) {
			log.debug("检测字段 {}.{} 的 @FieldRecord 注解时发生异常: {}", tableName, fieldName, e.getMessage());
			return null;
		}
	}

	@Override
	public boolean skipRecord(String tableName) {
		// 首先检查全局配置是否启用
		if (dataRecordProperties != null && !dataRecordProperties.getEnabled()) {
			return true;
		}

		// 检查是否在全局忽略表列表中
		if (dataRecordProperties != null && dataRecordProperties.isTableIgnored(tableName)) {
			return true;
		}

		DataRecord dataRecord = detectDataRecord(tableName);
		return dataRecord == null || isEmptyDataRecord(dataRecord);
	}

	@Override
	public DataRecord getTableDataRecord(String tableName) {
		DataRecord dataRecord = detectDataRecord(tableName);
		return isEmptyDataRecord(dataRecord) ? null : dataRecord;
	}

	@Override
	public void clearCache() {
		tableRecordCache.clear();
		tableEntityCache.clear();
	}

	@Override
	public void clearTableCache(String tableName) {
		tableRecordCache.remove(tableName);
		tableEntityCache.remove(tableName);
	}

	/**
	 * 通过表名获取TableInfo
	 *
	 * @param tableName 表名
	 * @return TableInfo
	 */
	private TableInfo getTableInfoByTableName(String tableName) {
		return TableInfoHelper.getTableInfos().stream()
			.filter(tableInfo -> tableName.equalsIgnoreCase(tableInfo.getTableName()))
			.findFirst()
			.orElse(null);
	}

	/**
	 * 查找字段（支持驼峰命名转换）
	 *
	 * @param entityClass 实体类
	 * @param fieldName   字段名
	 * @return 字段
	 */
	private Field findField(Class<?> entityClass, String fieldName) {
		try {
			// 直接查找
			return entityClass.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			// 尝试驼峰命名转换
			String camelFieldName = toCamelCase(fieldName);
			try {
				return entityClass.getDeclaredField(camelFieldName);
			} catch (NoSuchFieldException ex) {
				// 遍历所有字段查找
				for (Field field : entityClass.getDeclaredFields()) {
					if (field.getName().equalsIgnoreCase(fieldName) ||
						field.getName().equalsIgnoreCase(camelFieldName)) {
						return field;
					}
				}
				return null;
			}
		}
	}

	/**
	 * 下划线转驼峰
	 *
	 * @param fieldName 字段名
	 * @return 驼峰命名
	 */
	private String toCamelCase(String fieldName) {
		if (fieldName == null || fieldName.isEmpty()) {
			return fieldName;
		}

		StringBuilder result = new StringBuilder();
		boolean nextUpperCase = false;

		for (int i = 0; i < fieldName.length(); i++) {
			char c = fieldName.charAt(i);
			if (c == '_') {
				nextUpperCase = true;
			} else {
				if (nextUpperCase) {
					result.append(Character.toUpperCase(c));
					nextUpperCase = false;
				} else {
					result.append(Character.toLowerCase(c));
				}
			}
		}

		return result.toString();
	}

	/**
	 * 创建空的DataRecord注解（用于缓存标记）
	 */
	private DataRecord createEmptyDataRecord() {
		return new DataRecord() {
			@Override
			public String module() {
				return "";
			}

			@Override
			public String operation() {
				return "";
			}

			@Override
			public boolean recordDetail() {
				return false;
			}

			@Override
			public boolean recordOldData() {
				return false;
			}

			@Override
			public boolean recordNewData() {
				return false;
			}

			@Override
			public String[] ignoreFields() {
				return new String[0];
			}

			@Override
			public String[] includeFields() {
				return new String[0];
			}

			@Override
			public DataRecordLevel level() {
				return DataRecordLevel.INFO;
			}

			@Override
			public boolean async() {
				return false;
			}

			@Override
			public String condition() {
				return "";
			}

			@Override
			public Class<? extends java.lang.annotation.Annotation> annotationType() {
				return DataRecord.class;
			}
		};
	}

	/**
	 * 判断是否为空的DataRecord
	 */
	private boolean isEmptyDataRecord(DataRecord dataRecord) {
		return dataRecord != null && !dataRecord.recordDetail() &&
			!dataRecord.recordOldData() && !dataRecord.recordNewData();
	}

	/**
	 * 验证条件表达式
	 *
	 * @param condition 条件表达式
	 * @param context   上下文描述（用于日志）
	 */
	private void validateConditionExpression(String condition, String context) {
		if (condition == null || condition.trim().isEmpty()) {
			return;
		}

		DataRecordEvaluator.ExpressionValidationResult result = DataRecordEvaluator.validateExpression(condition);
		if (!result.isValid()) {
			log.warn("数据审计条件表达式验证失败 [{}]: 表达式='{}', 错误={}",
				context, condition, result.getErrorMessage());
		} else {
			log.debug("数据审计条件表达式验证通过 [{}]: '{}'", context, condition);
		}
	}
}
