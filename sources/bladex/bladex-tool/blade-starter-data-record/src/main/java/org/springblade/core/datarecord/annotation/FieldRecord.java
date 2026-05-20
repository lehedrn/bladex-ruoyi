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
package org.springblade.core.datarecord.annotation;

import java.lang.annotation.*;

/**
 * 字段记录注解
 * <p>
 * 用于标记需要进行数据审计的实体字段，提供字段级别的精细化控制
 *
 * @author BladeX
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldRecord {

	/**
	 * 是否记录该字段的变更
	 */
	boolean value() default true;

	/**
	 * 数据审计级别
	 */
	DataRecordLevel level() default DataRecordLevel.INFO;

	/**
	 * 字段描述
	 * <p>
	 * 用于在日志输出中提供更友好的字段名称显示
	 * <p>
	 * 示例：
	 * - 设置 description = "用户名"，日志显示：{用户名: oldValue->newValue}
	 * - 不设置 description，日志显示：{username: oldValue->newValue}
	 */
	String description() default "";

	/**
	 * 记录条件表达式
	 * <p>
	 * 支持的变量：
	 * - #oldValue: 字段的旧值
	 * - #newValue: 字段的新值
	 * <p>
	 * 示例：
	 * - "#oldValue != #newValue": 只有值变更时才记录
	 * - "#newValue > 1000": 新值大于1000时才记录
	 */
	String condition() default "";
}
