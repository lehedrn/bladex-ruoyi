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
package org.springblade.core.secure.annotation;

import org.springblade.core.tool.utils.StringPool;

import java.lang.annotation.*;

/**
 * 权限注解，用于检查权限，规定访问权限
 * 支持以下几种使用方式：
 * <p>
 * 1. 单个属性模式：
 * <pre>{@code
 *   @PreAuth(permission = "user:add")
 *   @PreAuth(role = "admin")
 *   @PreAuth(menu = "user")
 * }</pre>
 *
 * 2. 组合属性模式：
 * <pre>{@code
 *   @PreAuth(role = "admin", permission = "user:add")
 *   @PreAuth(menu = "user", permission = "user:list")
 * }</pre>
 *
 * 3. SpEL表达式模式：
 * <pre>{@code
 *   @PreAuth("#userVO.id<10")
 *   @PreAuth("hasMenu('user')")
 *   @PreAuth("hasRole('admin')")
 *   @PreAuth("hasPermission('user:add')")
 *   @PreAuth("hasPermission(#test) and hasRole('admin')")
 * }</pre>
 *
 * @author BladeX
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PreAuth {

	/**
	 * Spring el表达式
	 */
	String value() default StringPool.EMPTY;

	/**
	 * 接口权限
	 */
	String permission() default StringPool.EMPTY;

	/**
	 * 角色权限
	 */
	String role() default StringPool.EMPTY;

	/**
	 * 菜单权限
	 */
	String menu() default StringPool.EMPTY;

}

