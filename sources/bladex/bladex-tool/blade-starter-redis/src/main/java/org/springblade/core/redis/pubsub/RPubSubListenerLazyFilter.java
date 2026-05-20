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

package org.springblade.core.redis.pubsub;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * mqtt 客户端订阅延迟加载排除
 *
 * @author L.cm
 */
public class RPubSubListenerLazyFilter implements LazyInitializationExcludeFilter {

	@Override
	public boolean isExcluded(String beanName, BeanDefinition beanDefinition, Class<?> beanType) {
		// 类上有注解的情况
		RPubSubListener subscribe = AnnotationUtils.findAnnotation(beanType, RPubSubListener.class);
		if (subscribe != null) {
			return true;
		}
		// 方法上的注解
		List<Method> methodList = new ArrayList<>();
		ReflectionUtils.doWithMethods(beanType, method -> {
			RPubSubListener clientSubscribe = AnnotationUtils.findAnnotation(method, RPubSubListener.class);
			if (clientSubscribe != null) {
				methodList.add(method);
			}
		}, ReflectionUtils.USER_DECLARED_METHODS);
		return !methodList.isEmpty();
	}

}
