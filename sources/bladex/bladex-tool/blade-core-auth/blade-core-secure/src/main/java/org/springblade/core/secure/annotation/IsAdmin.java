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

import org.springblade.core.tool.constant.RoleConstant;

import java.lang.annotation.*;

/**
 * Admin角色权限注解，默认校验是否具有 Admin 角色
 * <p>
 * 使用示例：
 * <pre>{@code
 *   // 仅管理员可访问
 *   @IsAdmin
 *   public R<Boolean> adminOperation() {
 *       return R.data(true);
 *   }
 * }</pre>
 *
 * @author BladeX
 * @see PreAuth
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
public @interface IsAdmin {
}
