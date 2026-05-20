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
package org.springblade.core.secure.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springblade.core.secure.annotation.PreAuth;
import org.springblade.core.secure.auth.AuthFun;
import org.springblade.core.secure.exception.SecureException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.ClassUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.MethodParameter;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.springblade.core.launch.constant.TokenConstant.AUTH_HEADER;

/**
 * AOP 鉴权
 *
 * @author Chill
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class AuthAspect implements ApplicationContextAware {

	/**
	 * 权限处理函数
	 */
	private final AuthFun authFun;

	/**
	 * 表达式处理
	 */
	private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

	/**
	 * 切 方法 和 类上的 @PreAuth 注解
	 *
	 * @param point 切点
	 * @return Object
	 * @throws Throwable 没有权限的异常
	 */
	@Around(
		"@annotation(org.springblade.core.secure.annotation.PreAuth) || " +
			"@within(org.springblade.core.secure.annotation.PreAuth)"
	)
	public Object preAuth(ProceedingJoinPoint point) throws Throwable {
		if (handleAuth(point)) {
			return point.proceed();
		}
		this.handleLogger(point);
		throw new SecureException(ResultCode.UN_AUTHORIZED);
	}

	/**
	 * 处理权限
	 *
	 * @param point 切点
	 */
	private boolean handleAuth(ProceedingJoinPoint point) {
		// 读取权限注解，优先方法上，没有则读取类
		MethodSignature ms = (MethodSignature) point.getSignature();
		Method method = ms.getMethod();
		PreAuth preAuth = ClassUtil.getAnnotation(method, PreAuth.class);
		// 处理接口权限属性验证
		if (StringUtil.isNotBlank(preAuth.permission()) && !authFun.hasPermission(preAuth.permission())) {
			return false;
		}
		// 处理角色权限属性验证
		if (StringUtil.isNotBlank(preAuth.role()) && !authFun.hasRole(preAuth.role())) {
			return false;
		}
		// 处理菜单权限属性验证
		if (StringUtil.isNotBlank(preAuth.menu()) && !authFun.hasMenu(preAuth.menu())) {
			return false;
		}
		// 判断表达式
		String condition = preAuth.value();
		if (StringUtil.isNotBlank(condition)) {
			Expression expression = EXPRESSION_PARSER.parseExpression(condition);
			// 方法参数值
			Object[] args = point.getArgs();
			StandardEvaluationContext context = getEvaluationContext(method, args);
			return Boolean.TRUE.equals(expression.getValue(context, Boolean.class));
		} else {
			// 判断用户是否通过鉴权
			return AuthUtil.hasAuth();
		}
	}

	/**
	 * 获取方法上的参数
	 *
	 * @param method 方法
	 * @param args   变量
	 * @return {SimpleEvaluationContext}
	 */
	private StandardEvaluationContext getEvaluationContext(Method method, Object[] args) {
		// 初始化Sp el表达式上下文，并设置 AuthFun
		StandardEvaluationContext context = new StandardEvaluationContext(authFun);
		// 设置表达式支持spring bean
		context.setBeanResolver(new BeanFactoryResolver(applicationContext));
		for (int i = 0; i < args.length; i++) {
			// 读取方法参数
			MethodParameter methodParam = ClassUtil.getMethodParameter(method, i);
			// 设置方法 参数名和值 为sp el变量
			context.setVariable(methodParam.getParameterName(), args[i]);
		}
		return context;
	}

	/**
	 * 记录日志
	 */
	private void handleLogger(ProceedingJoinPoint point) {
		HttpServletRequest request = WebUtil.getRequest();
		if (request != null && AuthUtil.hasAuth()) {
			// 读取权限注解，优先方法上，没有则读取类
			MethodSignature ms = (MethodSignature) point.getSignature();
			Method method = ms.getMethod();
			PreAuth preAuth = ClassUtil.getAnnotation(method, PreAuth.class);
			String menu = preAuth.menu();
			String role = preAuth.role();
			String permission = preAuth.permission();
			String value = preAuth.value();
			// 获取preAuth注解有值的定义
			StringBuilder authMessage = new StringBuilder();
			if (StringUtil.isNotBlank(menu)) {
				authMessage.append("menu[").append(menu).append("] ");
			}
			if (StringUtil.isNotBlank(role)) {
				authMessage.append("role[").append(role).append("] ");
			}
			if (StringUtil.isNotBlank(permission)) {
				authMessage.append("permission[").append(permission).append("] ");
			}
			if (StringUtil.isNotBlank(value)) {
				authMessage.append("SpEL[").append(value).append("] ");
			}
			// 记录日志
			List<Object> authArgs = new ArrayList<>();
			String authLogger = "\n\n================  PreAuth Start  ================" +
				"\nPreAuth : {}" +
				"\nClientId : {}" +
				"\nBladeAuth : {}" +
				"\nBladeUser : {}" +
				"\nRequestURI : {}" +
				"\nRequestIP: {}" +
				"\nRequestParam: {}" +
				"\n================  PreAuth  End   ================\n";
			authArgs.add(authMessage.toString());
			authArgs.add(AuthUtil.getClientId(request));
			authArgs.add(WebUtil.getHeader(AUTH_HEADER));
			authArgs.add(AuthUtil.getUser());
			authArgs.add(WebUtil.getRequestURI(request));
			authArgs.add(WebUtil.getIP(request));
			authArgs.add(WebUtil.getRequestContent(request));
			log.warn(authLogger, authArgs.toArray());
		}
	}

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
