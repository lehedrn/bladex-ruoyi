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
package org.springblade.core.tenant.aspect;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springblade.core.context.BladeContext;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tenant.TenantUtil;
import org.springblade.core.tenant.annotation.TenantAsync;
import org.springblade.core.tenant.exception.TenantException;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 租户异步切面
 *
 * @author Chill
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class BladeTenantAsyncAspect {

	private final BladeContext bladeContext;

	@Pointcut("@within(org.springblade.core.tenant.annotation.TenantAsync) || " +
		"@annotation(org.springblade.core.tenant.annotation.TenantAsync)")
	public void tenantAsyncPointcut() {
	}

	@Around("tenantAsyncPointcut()")
	public Object handleTenantAsync(ProceedingJoinPoint joinPoint) throws Throwable {
		// 获取注解和租户信息
		TenantAsync tenantAsync = resolveTenantAsync(joinPoint);

		// 优先从认证信息获取租户ID，其次从上下文获取
		String tenantId = StringUtil.isNotBlank(AuthUtil.getTenantId())
			? AuthUtil.getTenantId()
			: bladeContext.getTenantId();

		// 无需处理租户切换的情况
		if (tenantAsync == null || Func.isBlank(tenantId)) {
			return joinPoint.proceed();
		}

		// 根据配置执行租户操作
		return executeTenantOperation(joinPoint, tenantId, tenantAsync.datasource());
	}

	/**
	 * 解析TenantAsync注解
	 */
	private TenantAsync resolveTenantAsync(ProceedingJoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();

		// 优先从方法获取注解
		TenantAsync tenantAsync = AnnotationUtils.findAnnotation(method, TenantAsync.class);
		if (tenantAsync == null) {
			// 从类级别获取注解
			tenantAsync = AnnotationUtils.findAnnotation(joinPoint.getTarget().getClass(), TenantAsync.class);
		}
		return tenantAsync;
	}

	/**
	 * 执行租户操作
	 */
	private Object executeTenantOperation(ProceedingJoinPoint joinPoint, String tenantId, boolean useDatasource) {
		if (!useDatasource) {
			// 仅切换租户上下文
			return executeWithTenantContext(joinPoint, tenantId);
		}

		// 需要切换数据源的情况
		try {
			DynamicDataSourceContextHolder.push(tenantId);
			return executeWithTenantContext(joinPoint, tenantId);
		} finally {
			DynamicDataSourceContextHolder.poll();
		}
	}

	/**
	 * 在租户上下文中执行业务逻辑
	 */
	private Object executeWithTenantContext(ProceedingJoinPoint joinPoint, String tenantId) {
		return TenantUtil.use(tenantId, () -> {
			try {
				return joinPoint.proceed();
			} catch (Throwable e) {
				throw new TenantException(e);
			}
		});
	}
}
