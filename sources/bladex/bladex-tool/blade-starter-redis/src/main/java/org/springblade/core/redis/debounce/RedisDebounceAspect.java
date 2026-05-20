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

package org.springblade.core.redis.debounce;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springblade.core.tool.spel.BladeExpressionEvaluator;
import org.springblade.core.tool.utils.CharPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Redis 防抖切面
 *
 * @author BladeX
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class RedisDebounceAspect implements ApplicationContextAware {

	/**
	 * 表达式处理
	 */
	private static final BladeExpressionEvaluator EVALUATOR = new BladeExpressionEvaluator();

	/**
	 * Redis 防抖服务
	 */
	private final DebounceClient debounceClient;

	/**
	 * Redis 防抖配置
	 */
	private final RedisDebounceProperties properties;

	private ApplicationContext applicationContext;

	/**
	 * AOP 环切 注解 @RedisDebounce
	 */
	@Around("@annotation(redisDebounce)")
	public Object aroundRedisDebounce(ProceedingJoinPoint point, RedisDebounce redisDebounce) throws Throwable {
		String debounceKey = redisDebounce.key();
		Assert.hasText(debounceKey, "@RedisDebounce key must have length; it must not be null or empty");

		// 构建完整的防抖键
		String fullDebounceKey = buildDebounceKey(point, redisDebounce);

		// 防抖参数
		long interval = redisDebounce.interval();
		TimeUnit timeUnit = redisDebounce.timeUnit();
		String message = redisDebounce.message();
		boolean includeRemainingTime = redisDebounce.includeRemainingTime();

		// 尝试防抖
		boolean canExecute = debounceClient.tryDebounce(fullDebounceKey, interval, timeUnit);

		if (!canExecute) {
			// 在防抖期内，不执行方法
			if (properties.getEnableDebugLog()) {
				log.debug("Method execution skipped due to debounce: {}", fullDebounceKey);
			}

			if (includeRemainingTime) {
				long remainingTime = debounceClient.getRemainingTime(fullDebounceKey, timeUnit);
				throw new DebounceException(message, fullDebounceKey, remainingTime, timeUnit);
			} else {
				throw new DebounceException(message, fullDebounceKey);
			}
		}

		// 不在防抖期内，执行方法
		if (properties.getEnableDebugLog()) {
			log.debug("Method execution allowed: {}", fullDebounceKey);
		}
		return point.proceed();
	}

	/**
	 * 构建防抖键
	 *
	 * @param point         切点
	 * @param redisDebounce 防抖注解
	 * @return 防抖键
	 */
	private String buildDebounceKey(ProceedingJoinPoint point, RedisDebounce redisDebounce) {
		String baseKey = redisDebounce.key();
		String param = redisDebounce.param();

		// 如果没有参数，直接返回基础键
		if (StringUtil.isBlank(param)) {
			return baseKey;
		}

		// 解析参数表达式
		String paramValue = evalDebounceParam(point, param);
		return baseKey + CharPool.COLON + paramValue;
	}

	/**
	 * 计算参数表达式
	 *
	 * @param point       ProceedingJoinPoint
	 * @param debounceParam 防抖参数
	 * @return 结果
	 */
	private String evalDebounceParam(ProceedingJoinPoint point, String debounceParam) {
		MethodSignature ms = (MethodSignature) point.getSignature();
		Method method = ms.getMethod();
		Object[] args = point.getArgs();
		Object target = point.getTarget();
		Class<?> targetClass = target.getClass();
		EvaluationContext context = EVALUATOR.createContext(method, args, target, targetClass, applicationContext);
		AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
		return EVALUATOR.evalAsText(debounceParam, elementKey, context);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
