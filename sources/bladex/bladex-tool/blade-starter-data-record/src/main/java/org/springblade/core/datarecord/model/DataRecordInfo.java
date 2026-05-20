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
package org.springblade.core.datarecord.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 数据审计信息
 *
 * @author BladeX
 */
@Data
@Accessors(chain = true)
public class DataRecordInfo {

	/**
	 * 记录ID
	 */
	private Long recordId;

	/**
	 * 操作用户ID
	 */
	private String userId;

	/**
	 * 操作用户名
	 */
	private String userName;

	/**
	 * 租户ID
	 */
	private String tenantId;

	/**
	 * 服务ID
	 */
	protected String serviceId;

	/**
	 * 服务器 ip
	 */
	protected String serverIp;

	/**
	 * 服务器名
	 */
	protected String serverHost;

	/**
	 * 环境
	 */
	protected String env;

	/**
	 * 操作IP地址
	 */
	protected String remoteIp;

	/**
	 * 用户代理
	 */
	protected String userAgent;

	/**
	 * 请求URI
	 */
	protected String requestUri;

	/**
	 * 操作方式
	 */
	protected String method;

	/**
	 * 业务模块
	 */
	private String module;

	/**
	 * 操作类型（INSERT、UPDATE、DELETE）
	 */
	private String operation;

	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * 主键字段名
	 */
	private String primaryKey;

	/**
	 * 主键值
	 */
	private Object primaryKeyValue;

	/**
	 * 记录状态
	 */
	private Boolean recordStatus;

	/**
	 * 执行耗时（毫秒）
	 */
	private Long cost;

	/**
	 * 记录时间
	 */
	private LocalDateTime recordTime;

	/**
	 * 修改前的完整数据
	 */
	private Map<String, Object> oldData;

	/**
	 * 修改后的完整数据
	 */
	private Map<String, Object> newData;

	/**
	 * 变更的字段数据（只包含发生变化的字段）
	 */
	private Map<String, FieldChangeInfo> changeData;

	/**
	 * 原始记录数据（OperationResult的原始数据）
	 */
	private String recordResult;

	/**
	 * 变更字段列表
	 */
	private List<String> changedFields;

	/**
	 * 字段变更信息
	 */
	@Data
	@Accessors(chain = true)
	public static class FieldChangeInfo {
		/**
		 * 字段名
		 */
		private String fieldName;

		/**
		 * 字段中文名（如果有）
		 */
		private String fieldLabel;

		/**
		 * 旧值
		 */
		private Object oldValue;

		/**
		 * 新值
		 */
		private Object newValue;

		/**
		 * 字段类型
		 */
		private String fieldType;

		/**
		 * 是否为主键
		 */
		private Boolean isPrimaryKey;

		/**
		 * 变更描述
		 */
		private String changeDescription;

		/**
		 * 获取变更描述
		 */
		public String getChangeDescription() {
			if (changeDescription != null) {
				return changeDescription;
			}

			// 构建显示名称：如果有自定义 description，则显示为 "fieldName[description]"，否则只显示 "fieldName"
			String displayName;
			if (fieldLabel != null && !fieldLabel.isEmpty() && !fieldLabel.equals(fieldName)) {
				// 有自定义字段标签，格式：fieldName[description]
				displayName = String.format("%s[%s]", fieldName, fieldLabel);
			} else {
				// 没有自定义字段标签，只显示字段名
				displayName = fieldName;
			}

			return String.format("%s: %s->%s", displayName, oldValue, newValue);
		}
	}
}
