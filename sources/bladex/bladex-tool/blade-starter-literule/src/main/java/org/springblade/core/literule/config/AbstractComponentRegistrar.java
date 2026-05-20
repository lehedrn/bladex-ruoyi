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
package org.springblade.core.literule.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 组件注册器抽象基类
 * 提供通用的组件扫描和注册功能
 *
 * @author BladeX
 */
@Slf4j
public abstract class AbstractComponentRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {
		// 获取扫描包路径
		Set<String> basePackages = getBasePackages(importingClassMetadata);

		// 创建扫描器
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(getTypeFilter());

		// 扫描所有符合条件的类
		for (String basePackage : basePackages) {
			for (BeanDefinition beanDefinition : scanner.findCandidateComponents(basePackage)) {
				try {
					// 获取类
					Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());

					// 检查类是否符合要求
					if (isValidComponent(clazz)) {
						// 获取Bean名称
						String beanName = getBeanName(clazz);

						// 检查Bean名称冲突
						if (registry.containsBeanDefinition(beanName)) {
							handleBeanNameConflict(registry, beanDefinition, clazz, beanName);
						} else {
							// 注册bean
							registry.registerBeanDefinition(beanName, beanDefinition);
							log.info("Registered {}: {} with name: {}",
								getComponentType(), clazz.getName(), beanName);
						}
					} else {
						log.warn("Class {} is annotated with {} but does not implement required interface",
							clazz.getName(), getAnnotationType().getSimpleName());
					}
				} catch (ClassNotFoundException e) {
					log.error("Failed to load class: {}", beanDefinition.getBeanClassName(), e);
				}
			}
		}
	}

	/**
	 * 获取扫描包路径
	 *
	 * @param importingClassMetadata 导入类的元数据
	 * @return 扫描包路径集合
	 */
	protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
		// 获取@ComponentScan注解的属性
		Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(
			"org.springframework.context.annotation.ComponentScan");

		Set<String> basePackages = new HashSet<>();

		// 如果有@ComponentScan注解，获取其basePackages属性
		if (annotationAttributes != null) {
			String[] basePackagesArray = (String[]) annotationAttributes.get("basePackages");
			if (basePackagesArray != null) {
				Collections.addAll(basePackages, basePackagesArray);
			}
		}

		// 如果没有指定basePackages，使用导入类所在的包
		if (basePackages.isEmpty()) {
			basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
		}

		return basePackages;
	}

	/**
	 * 处理Bean名称冲突
	 *
	 * @param registry       Bean定义注册器
	 * @param beanDefinition Bean定义
	 * @param clazz          类
	 * @param beanName       Bean名称
	 */
	protected void handleBeanNameConflict(BeanDefinitionRegistry registry, BeanDefinition beanDefinition,
										  Class<?> clazz, String beanName) {
		BeanDefinition existingBean = registry.getBeanDefinition(beanName);
		String existingClassName = existingBean.getBeanClassName();
		log.warn("Bean name conflict: {} is already registered for class {}. Skipping registration for class {}.",
			beanName, existingClassName, clazz.getName());

		// 使用类名作为后缀，避免冲突
		String newBeanName = beanName + "_" + clazz.getSimpleName();
		log.info("Registering with alternative name: {}", newBeanName);
		registry.registerBeanDefinition(newBeanName, beanDefinition);
	}

	/**
	 * 获取类型过滤器
	 *
	 * @return 类型过滤器
	 */
	protected abstract TypeFilter getTypeFilter();

	/**
	 * 检查类是否符合要求
	 *
	 * @param clazz 类
	 * @return 是否符合要求
	 */
	protected abstract boolean isValidComponent(Class<?> clazz);

	/**
	 * 获取Bean名称
	 *
	 * @param clazz 类
	 * @return Bean名称
	 */
	protected abstract String getBeanName(Class<?> clazz);

	/**
	 * 获取组件类型名称
	 *
	 * @return 组件类型名称
	 */
	protected abstract String getComponentType();

	/**
	 * 获取注解类型
	 *
	 * @return 注解类型
	 */
	protected abstract Class<? extends Annotation> getAnnotationType();
}
