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
package org.springblade.core.tool.utils;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 类操作工具
 *
 * @author L.cm
 */
public class ClassUtil extends org.springframework.util.ClassUtils {

	private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

	/**
	 * 获取方法参数信息
	 *
	 * @param constructor    构造器
	 * @param parameterIndex 参数序号
	 * @return {MethodParameter}
	 */
	public static MethodParameter getMethodParameter(Constructor<?> constructor, int parameterIndex) {
		MethodParameter methodParameter = new SynthesizingMethodParameter(constructor, parameterIndex);
		methodParameter.initParameterNameDiscovery(PARAMETER_NAME_DISCOVERER);
		return methodParameter;
	}

	/**
	 * 获取方法参数信息
	 *
	 * @param method         方法
	 * @param parameterIndex 参数序号
	 * @return {MethodParameter}
	 */
	public static MethodParameter getMethodParameter(Method method, int parameterIndex) {
		MethodParameter methodParameter = new SynthesizingMethodParameter(method, parameterIndex);
		methodParameter.initParameterNameDiscovery(PARAMETER_NAME_DISCOVERER);
		return methodParameter;
	}

	/**
	 * 获取Annotation
	 *
	 * @param method         Method
	 * @param annotationType 注解类
	 * @param <A>            泛型标记
	 * @return {Annotation}
	 */
	public static <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
		Class<?> targetClass = method.getDeclaringClass();
		// The method may be on an interface, but we need attributes from the target class.
		// If the target class is null, the method will be unchanged.
		Method specificMethod = ClassUtil.getMostSpecificMethod(method, targetClass);
		// If we are dealing with method with generic parameters, find the original method.
		specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
		// 先找方法，再找方法上的类
		A annotation = AnnotatedElementUtils.findMergedAnnotation(specificMethod, annotationType);
		;
		if (null != annotation) {
			return annotation;
		}
		// 获取类上面的Annotation，可能包含组合注解，故采用spring的工具类
		return AnnotatedElementUtils.findMergedAnnotation(specificMethod.getDeclaringClass(), annotationType);
	}

	/**
	 * 获取Annotation
	 *
	 * @param handlerMethod  HandlerMethod
	 * @param annotationType 注解类
	 * @param <A>            泛型标记
	 * @return {Annotation}
	 */
	public static <A extends Annotation> A getAnnotation(HandlerMethod handlerMethod, Class<A> annotationType) {
		// 先找方法，再找方法上的类
		A annotation = handlerMethod.getMethodAnnotation(annotationType);
		if (null != annotation) {
			return annotation;
		}
		// 获取类上面的Annotation，可能包含组合注解，故采用spring的工具类
		Class<?> beanType = handlerMethod.getBeanType();
		return AnnotatedElementUtils.findMergedAnnotation(beanType, annotationType);
	}


	/**
	 * 判断是否有注解 Annotation
	 *
	 * @param method         Method
	 * @param annotationType 注解类
	 * @param <A>            泛型标记
	 * @return {boolean}
	 */
	public static <A extends Annotation> boolean isAnnotated(Method method, Class<A> annotationType) {
		// 先找方法，再找方法上的类
		boolean isMethodAnnotated = AnnotatedElementUtils.isAnnotated(method, annotationType);
		if (isMethodAnnotated) {
			return true;
		}
		// 获取类上面的Annotation，可能包含组合注解，故采用spring的工具类
		Class<?> targetClass = method.getDeclaringClass();
		return AnnotatedElementUtils.isAnnotated(targetClass, annotationType);
	}

	/**
	 * 获取类的字段名合集
	 *
	 * @param clazz 类
	 * @param <T>   泛型标记
	 * @return 字段名合集
	 */
	public static <T> Set<String> getClassFieldNames(Class<T> clazz) {
		Set<String> fieldNames = new HashSet<>();
		// 循环遍历 clazz 及其父类，获取字段
		while (clazz != null && clazz != Object.class) {
			Field[] fields = clazz.getDeclaredFields(); // 获取当前类的所有字段
			fieldNames.addAll(
				Set.of(fields)   // 使用 Set.of() 创建不可变集合
					.stream()
					.map(Field::getName) // 提取字段名
					.collect(Collectors.toSet()) // 收集成 Set
			);
			clazz = (Class<T>) clazz.getSuperclass(); // 获取父类
		}
		return fieldNames;
	}

}
