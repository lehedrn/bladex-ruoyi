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
 * Author: DreamLu (596392912@qq.com)
 */
package org.springblade.core.tenant;

import lombok.experimental.UtilityClass;
import org.springblade.core.secure.utils.AuthUtil;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;

import java.util.function.Supplier;

/**
 * Tenant 工具
 *
 * @author L.cm，BladeX
 */
@UtilityClass
public class TenantUtil {

	/**
	 * 租户ID线程
	 */
	private static final ThreadLocal<String> TENANT_ID_HOLDER = new NamedThreadLocal<>("blade-tenant-id") {
		@Override
		protected String initialValue() {
			return null;
		}
	};

	/**
	 * 租户状态线程
	 */
	private static final ThreadLocal<Boolean> TENANT_IGNORE_HOLDER = new NamedThreadLocal<>("blade-tenant-ignore") {
		@Override
		protected Boolean initialValue() {
			return Boolean.FALSE;
		}
	};

	/**
	 * 获取租户id
	 *
	 * @return 租户id
	 */
	public static String getTenantId() {
		String tenantId = TENANT_ID_HOLDER.get();
		if (tenantId != null) {
			return tenantId;
		}
		return AuthUtil.getTenantId();
	}

	/**
	 * 使用租户 id 执行函数
	 *
	 * @param tenantId tenantId
	 * @param supplier supplier
	 * @param <R>      泛型
	 * @return R 函数返回
	 */
	public static <R> R use(String tenantId, Supplier<R> supplier) {
		Assert.hasText(tenantId, "参数 tenantId 为空");
		try {
			TENANT_ID_HOLDER.set(tenantId);
			return supplier.get();
		} finally {
			TENANT_ID_HOLDER.remove();
		}
	}

	/**
	 * 使用租户 id 执行函数
	 *
	 * @param tenantId tenantId
	 * @param runnable Runnable
	 */
	public static void use(String tenantId, Runnable runnable) {
		Assert.hasText(tenantId, "参数 tenantId 为空");
		try {
			TENANT_ID_HOLDER.set(tenantId);
			runnable.run();
		} finally {
			TENANT_ID_HOLDER.remove();
		}
	}

	/**
	 * 是否忽略租户
	 */
	public static Boolean isIgnore() {
		return TENANT_IGNORE_HOLDER.get();
	}

	/**
	 * 忽略租户 执行函数
	 *
	 * @param supplier supplier
	 * @param <R>      泛型
	 * @return R 函数返回
	 */
	public static <R> R ignore(Supplier<R> supplier) {
		try {
			TENANT_IGNORE_HOLDER.set(Boolean.TRUE);
			return supplier.get();
		} finally {
			TENANT_IGNORE_HOLDER.remove();
		}
	}

	/**
	 * 忽略租户 执行函数
	 *
	 * @param runnable Runnable
	 */
	public static void ignore(Runnable runnable) {
		try {
			TENANT_IGNORE_HOLDER.set(Boolean.TRUE);
			runnable.run();
		} finally {
			TENANT_IGNORE_HOLDER.remove();
		}
	}

}
