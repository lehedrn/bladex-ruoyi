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

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.datarecord.annotation.DataRecord;
import org.springblade.core.datarecord.annotation.FieldRecord;
import org.springblade.core.datarecord.constant.DataRecordConstant;
import org.springblade.core.datarecord.expression.DataRecordEvaluator;
import org.springblade.core.datarecord.model.DataRecordInfo;
import org.springblade.core.datarecord.props.DataRecordProperties;
import org.springblade.core.launch.props.BladeProperties;
import org.springblade.core.launch.server.ServerInfo;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.constant.BladeConstant;
import org.springblade.core.tool.utils.*;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 默认数据审计解析器实现
 * <p>
 * 用于解析数据库操作结果为数据审计记录
 *
 * @author BladeX
 */
@Slf4j
@RequiredArgsConstructor
public class BladeDataRecordParser implements DataRecordParser {

	private final DataRecordDetector dataRecordDetector;
	private final DataRecordProperties dataRecordProperties;
	private final BladeProperties bladeProperties;
	private final ServerInfo serverInfo;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private static final Pattern CHANGE_PATTERN = Pattern.compile(DataRecordConstant.CHANGE_PATTERN_REGEX);

	@Override
	public DataRecordInfo parseOperationResult(DataChangeRecorderInnerInterceptor.OperationResult operationResult, DataRecord dataRecord) {
		if (operationResult == null) {
			return null;
		}

		try {
			DataRecordInfo recordInfo = new DataRecordInfo();
			BladeUser user = AuthUtil.getUser();
			if (user != null) {
				recordInfo.setUserId(String.valueOf(user.getUserId()));
				recordInfo.setUserName(user.getUserName());
			} else {
				recordInfo.setUserId(DataRecordConstant.ANONYMOUS_USER_ID);
				recordInfo.setUserName(DataRecordConstant.ANONYMOUS_USER_NAME);
			}
			recordInfo.setRecordId(IdWorker.getId());
			recordInfo.setModule(dataRecord != null ? dataRecord.module() : "");

			// 处理 operation 属性
			String operation = operationResult.getOperation(); // 数据库操作类型：INSERT/UPDATE/DELETE
			if (dataRecord != null && StringUtil.isNotBlank(dataRecord.operation())) {
				// 如果注解中定义了业务操作描述，则组合使用
				operation = operationResult.getOperation() + "[" + dataRecord.operation() + "]";
			}
			recordInfo.setOperation(operation);

			recordInfo.setTableName(operationResult.getTableName());
			recordInfo.setRecordStatus(operationResult.isRecordStatus());
			recordInfo.setCost(operationResult.getCost());
			recordInfo.setRecordTime(LocalDateTime.now());

			// 根据配置决定是否记录原始数据
			if (dataRecordProperties == null || dataRecordProperties.getRecordRawData()) {
				recordInfo.setRecordResult(operationResult.toString());
			}

			// 填充扩展字段
			fillExtendedFields(recordInfo);

			// 解析变更数据
			parseChangedData(recordInfo, operationResult.getChangedData(), dataRecord);

			return recordInfo;
		} catch (Exception e) {
			log.error("解析数据审计失败", e);
			return null;
		}
	}

	/**
	 * 解析变更数据
	 *
	 * @param recordInfo  记录信息
	 * @param changedData 变更数据JSON字符串
	 * @param dataRecord  数据审计注解
	 */
	private void parseChangedData(DataRecordInfo recordInfo, String changedData, DataRecord dataRecord) {
		if (StringUtil.isBlank(changedData) || "[]".equals(changedData)) {
			return;
		}

		try {
			// 解析JSON数组
			List<Map<String, Object>> dataList = objectMapper.readValue(changedData, new TypeReference<>() {
			});

			if (dataList.isEmpty()) {
				return;
			}

			// 处理第一条记录（通常只有一条）
			Map<String, Object> dataMap = dataList.get(0);

			Map<String, Object> oldData = new HashMap<>();
			Map<String, Object> newData = new HashMap<>();
			Map<String, DataRecordInfo.FieldChangeInfo> changeData = new HashMap<>();
			List<String> changedFields = new ArrayList<>();

			// 解析每个字段的变更
			for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
				String fieldName = entry.getKey();
				Object value = entry.getValue();

				if (value == null) {
					continue;
				}

				String valueStr = value.toString();

				// 检查是否为主键
				if (isPrimaryKeyField(fieldName)) {
					recordInfo.setPrimaryKey(fieldName);
					recordInfo.setPrimaryKeyValue(valueStr);
				}

				// 解析字段变更（格式：oldValue->newValue）
				Matcher matcher = CHANGE_PATTERN.matcher(valueStr);
				if (matcher.matches()) {
					String oldValue = matcher.group(1);
					String newValue = matcher.group(2);

					// 应用字段过滤规则
					if (shouldRecordField(fieldName, dataRecord, recordInfo.getTableName(), parseValue(oldValue), parseValue(newValue))) {
						oldData.put(fieldName, parseValue(oldValue));
						newData.put(fieldName, parseValue(newValue));

						DataRecordInfo.FieldChangeInfo fieldChangeInfo = new DataRecordInfo.FieldChangeInfo();
						fieldChangeInfo.setFieldName(fieldName);
						fieldChangeInfo.setOldValue(parseValue(oldValue));
						fieldChangeInfo.setNewValue(parseValue(newValue));
						fieldChangeInfo.setIsPrimaryKey(isPrimaryKeyField(fieldName));

						// 设置字段标签：优先使用 @FieldRecord.description，否则使用字段名
						String fieldLabel = getFieldLabel(fieldName, recordInfo.getTableName());
						fieldChangeInfo.setFieldLabel(fieldLabel);

						changeData.put(fieldName, fieldChangeInfo);
						changedFields.add(fieldName);
					}
				} else {
									// 没有变更标识，可能是新增或删除操作
				if (shouldRecordField(fieldName, dataRecord, recordInfo.getTableName(), null, parseValue(valueStr))) {
					if (DataRecordConstant.OPERATION_INSERT.equalsIgnoreCase(recordInfo.getOperation())) {
						newData.put(fieldName, parseValue(valueStr));
					} else if (DataRecordConstant.OPERATION_DELETE.equalsIgnoreCase(recordInfo.getOperation())) {
						oldData.put(fieldName, parseValue(valueStr));
					}
				}
				}
			}

			// 根据配置决定是否记录详细变更数据
			if (dataRecordProperties == null || dataRecordProperties.getRecordDetailedChanges()) {
				recordInfo.setOldData(oldData);
				recordInfo.setNewData(newData);
				recordInfo.setChangeData(changeData);
			}
			recordInfo.setChangedFields(changedFields);

		} catch (Exception e) {
			log.error("解析变更数据失败: {}", changedData, e);
		}
	}

	/**
	 * 判断是否应该记录该字段
	 *
	 * @param fieldName  字段名
	 * @param dataRecord 数据审计注解
	 * @param tableName  表名
	 * @param oldValue   旧值
	 * @param newValue   新值
	 * @return 是否记录
	 */
	private boolean shouldRecordField(String fieldName, DataRecord dataRecord, String tableName, Object oldValue, Object newValue) {
		// 首先检查全局配置的忽略字段
		if (dataRecordProperties != null && dataRecordProperties.isFieldIgnored(fieldName)) {
			return false;
		}

		// 检查 @FieldRecord 注解
		if (dataRecordDetector != null) {
			FieldRecord fieldRecord = dataRecordDetector.detectFieldRecord(tableName, fieldName);
			if (fieldRecord != null) {
				// 如果字段标记为不记录
				if (!fieldRecord.value()) {
					return false;
				}

				// 评估字段级别的条件表达式
				return DataRecordEvaluator.evaluateFieldCondition(fieldRecord.condition(), oldValue, newValue);
			}
		}

		if (dataRecord == null) {
			return true;
		}

		// 检查包含字段列表
		String[] includeFields = dataRecord.includeFields();
		if (includeFields.length > 0) {
			return Arrays.stream(includeFields)
				.anyMatch(field -> field.equalsIgnoreCase(fieldName));
		}

		// 检查忽略字段列表
		String[] ignoreFields = dataRecord.ignoreFields();
		if (ignoreFields.length > 0) {
			return Arrays.stream(ignoreFields)
				.noneMatch(field -> field.equalsIgnoreCase(fieldName));
		}

		return true;
	}

	/**
	 * 判断是否为主键字段
	 *
	 * @param fieldName 字段名
	 * @return 是否为主键
	 */
	private boolean isPrimaryKeyField(String fieldName) {
		return DataRecordConstant.PRIMARY_KEY_ID.equalsIgnoreCase(fieldName) ||
			fieldName.toUpperCase().endsWith(DataRecordConstant.PRIMARY_KEY_SUFFIX) ||
			DataRecordConstant.PRIMARY_KEY_PK.equalsIgnoreCase(fieldName);
	}

	/**
	 * 解析值，尝试转换为合适的类型
	 *
	 * @param valueStr 值字符串
	 * @return 解析后的值
	 */
	private Object parseValue(String valueStr) {
		if (valueStr == null || DataRecordConstant.NULL_VALUE.equals(valueStr)) {
			return null;
		}

		// 尝试解析为数字
		try {
			if (valueStr.contains(".")) {
				return Double.parseDouble(valueStr);
			} else {
				return Long.parseLong(valueStr);
			}
		} catch (NumberFormatException e) {
			// 不是数字，返回字符串
			return valueStr;
		}
	}

	/**
	 * 获取字段标签
	 *
	 * @param fieldName 字段名
	 * @param tableName 表名
	 * @return 字段标签
	 */
	private String getFieldLabel(String fieldName, String tableName) {
		if (dataRecordDetector != null) {
			FieldRecord fieldRecord = dataRecordDetector.detectFieldRecord(tableName, fieldName);
			if (fieldRecord != null) {
				String description = fieldRecord.description();
				// 如果配置了 description 且不为空，则使用 description，否则使用字段名
				return (description != null && !description.trim().isEmpty()) ? description : fieldName;
			}
		}
		return fieldName;
	}

	/**
	 * 填充扩展字段
	 * 参考 LogAbstractUtil 的实现方式
	 *
	 * @param recordInfo 记录信息
	 */
	private void fillExtendedFields(DataRecordInfo recordInfo) {
		// 填充租户ID
		recordInfo.setTenantId(Func.toStrWithEmpty(AuthUtil.getTenantId(), BladeConstant.ADMIN_TENANT_ID));

		// 填充服务信息
		if (bladeProperties != null) {
			recordInfo.setServiceId(bladeProperties.getName());
			recordInfo.setEnv(bladeProperties.getEnv());
		}

		// 填充服务器信息
		if (serverInfo != null) {
			recordInfo.setServerHost(serverInfo.getHostName());
			recordInfo.setServerIp(serverInfo.getIpWithPort());
		}

		// 尝试获取当前HTTP请求信息
		HttpServletRequest request = getCurrentRequest();
		if (request != null) {
			recordInfo.setRemoteIp(WebUtil.getIP(request));
			recordInfo.setUserAgent(request.getHeader(WebUtil.USER_AGENT_HEADER));
			recordInfo.setRequestUri(UrlUtil.getPath(request.getRequestURI()));
			recordInfo.setMethod(request.getMethod());
		}
	}

	/**
	 * 获取当前HTTP请求
	 * 使用Spring的RequestContextHolder安全获取
	 *
	 * @return HttpServletRequest 或 null
	 */
	private HttpServletRequest getCurrentRequest() {
		try {
			return WebUtil.getRequest();
		} catch (Exception e) {
			// 非Web环境或没有请求上下文时返回null
			return null;
		}
	}
}
