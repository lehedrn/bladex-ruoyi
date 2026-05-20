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
 * 数据审计注解
 * <p>
 * 用于标记需要进行数据审计的实体类，提供全局级别的配置选项
 *
 * @author BladeX
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataRecord {

	/**
	 * 业务模块名称
	 */
	String module() default "";

	/**
	 * 是否记录详细变更数据
	 */
	boolean recordDetail() default true;

	/**
	 * 是否记录旧数据
	 */
	boolean recordOldData() default true;

	/**
	 * 是否记录新数据
	 */
	boolean recordNewData() default true;

	/**
	 * 忽略的字段列表（字段级别使用时无效）
	 */
	String[] ignoreFields() default {};

	/**
	 * 只记录指定字段列表（字段级别使用时无效）
	 */
	String[] includeFields() default {};

	/**
	 * 数据审计级别
	 */
	DataRecordLevel level() default DataRecordLevel.INFO;

	/**
	 * 是否异步处理
	 */
	boolean async() default false;

	/**
	 * 记录条件表达式
	 * <p>
	 * 支持的变量：
	 * - #oldData: 修改前的数据Map
	 * - #newData: 修改后的数据Map
	 * <p>
	 * 示例：
	 * - "#oldData.amount > 1000": 实体金额大于1000时才记录
	 * - "#oldData.status != #newData.status": 状态发生变化时才记录
	 */
	String condition() default "";

	/**
	 * 业务操作描述
	 * <p>
	 * 用于描述具体的业务操作类型，将与数据库操作类型组合显示
	 * 例如：如果设置为"用户信息变更"，最终记录的操作类型为"UPDATE - 用户信息变更"
	 * <p>
	 * 如果不设置，则只显示数据库操作类型（INSERT/UPDATE/DELETE）
	 */
	String operation() default "";
}
