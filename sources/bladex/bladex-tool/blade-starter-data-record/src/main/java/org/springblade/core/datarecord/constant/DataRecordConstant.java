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
package org.springblade.core.datarecord.constant;

/**
 * 数据审计常量
 * <p>
 * 定义数据审计模块中使用的各种常量
 *
 * @author BladeX
 */
public class DataRecordConstant {

	// ==================== 操作符常量 ====================
	/**
	 * 不等于操作符
	 */
	public static final String NOT_EQUALS = " != ";

	/**
	 * 等于操作符
	 */
	public static final String EQUALS = " == ";

	/**
	 * 大于等于操作符
	 */
	public static final String GREATER_THAN_OR_EQUAL = " >= ";

	/**
	 * 小于等于操作符
	 */
	public static final String LESS_THAN_OR_EQUAL = " <= ";

	/**
	 * 大于操作符
	 */
	public static final String GREATER_THAN = " > ";

	/**
	 * 小于操作符
	 */
	public static final String LESS_THAN = " < ";

	// ==================== 数据库操作类型常量 ====================
	/**
	 * 插入操作
	 */
	public static final String OPERATION_INSERT = "INSERT";

	/**
	 * 更新操作
	 */
	public static final String OPERATION_UPDATE = "UPDATE";

	/**
	 * 删除操作
	 */
	public static final String OPERATION_DELETE = "DELETE";

	// ==================== SQL关键字常量 ====================
	/**
	 * SQL INTO 关键字
	 */
	public static final String SQL_INTO = "INTO";

	/**
	 * SQL FROM 关键字
	 */
	public static final String SQL_FROM = "FROM";

	// ==================== 主键字段相关常量 ====================
	/**
	 * 通用主键字段名
	 */
	public static final String PRIMARY_KEY_ID = "ID";

	/**
	 * 主键字段后缀
	 */
	public static final String PRIMARY_KEY_SUFFIX = "_ID";

	/**
	 * 主键字段名 PK
	 */
	public static final String PRIMARY_KEY_PK = "PK";

	// ==================== 特殊值常量 ====================

	/**
	 * 旧数据字段名
	 */
	public static final String OLD_DATA = "oldData";

	/**
	 * 新数据字段名
	 */
	public static final String NEW_DATA = "newData";

	/**
	 * 旧字段值
	 */
	public static final String OLD_VALUE = "oldValue";

	/**
	 * 新字段值
	 */
	public static final String NEW_VALUE = "newValue";

	/**
	 * 空值字符串
	 */
	public static final String NULL_VALUE = "null";

	/**
	 * 匿名用户ID
	 */
	public static final String ANONYMOUS_USER_ID = "anonymous";

	/**
	 * 匿名用户名
	 */
	public static final String ANONYMOUS_USER_NAME = "Anonymous User";

	// ==================== 表达式相关常量 ====================
	/**
	 * 变量占位符前缀
	 */
	public static final String VARIABLE_PLACEHOLDER_PREFIX = "__VAR_";

	/**
	 * 变量占位符后缀
	 */
	public static final String VARIABLE_PLACEHOLDER_SUFFIX = "__";

	/**
	 * 引号字符
	 */
	public static final String QUOTE = "\"";

	/**
	 * 单引号字符
	 */
	public static final String SINGLE_QUOTE = "'";

	/**
	 * 点号字符（属性访问）
	 */
	public static final String DOT = ".";

	// ==================== 正则表达式模式常量 ====================
	/**
	 * 变量模式 - 匹配 #variable 或 #object.property 格式
	 */
	public static final String VARIABLE_PATTERN_REGEX = "#(\\w+(?:\\.\\w+)*)";

	/**
	 * 数值模式 - 匹配整数和小数
	 */
	public static final String NUMBER_PATTERN_REGEX = "^\\d+(?:\\.\\d+)?$";

	/**
	 * 字符串模式 - 匹配被引号包围的字符串
	 */
	public static final String STRING_PATTERN_REGEX = "^[\"'](.+?)[\"']$";

	/**
	 * 变更模式 - 匹配 oldValue->newValue 格式
	 */
	public static final String CHANGE_PATTERN_REGEX = "^(.+)->(.+)$";

	// ==================== SQL相关常量 ====================
	/**
	 * SQL字符清理模式 - 清理引号和括号
	 */
	public static final String SQL_CHAR_CLEANUP_REGEX = "[`\"\\[\\]]";

	/**
	 * SQL入库模式 - 用于插入数据记录的SQL语句模板
	 */
	public final static String RECORD_INSERT_SQL = """
		INSERT INTO blade_record_data (
		    id, service_id, server_host, server_ip, env, record_level, method,
		    request_uri, user_agent, remote_ip, operation, table_name,
		    old_data, new_data, record_message, record_result, record_cost,
		    record_time, record_user, status, is_deleted
		) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
		""";

	/**
	 * 私有构造函数，防止实例化
	 */
	private DataRecordConstant() {
		throw new UnsupportedOperationException("Utility class");
	}
}
