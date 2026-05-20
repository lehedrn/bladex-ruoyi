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
package org.springblade.core.mp.encrypt.annotation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.lang.annotation.*;

/**
 * Mybatis字段加密模糊查询注解
 *
 * @author BladeX
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TableField(fill = FieldFill.INSERT_UPDATE)
public @interface SearchableFieldEncrypt {

	/**
	 * 是否启用模糊查询支持
	 * 当设置为true时，字段值会使用滑动窗口加密存储，支持like查询
	 *
	 * @return true-支持模糊查询，false-不支持模糊查询
	 */
	boolean enabled() default true;

	/**
	 * 模糊查询时，Entity的加密字段名称后缀
	 *
	 * @return 加密字段名称后缀，默认"Enc"
	 */
	String encName() default "Enc";

}
