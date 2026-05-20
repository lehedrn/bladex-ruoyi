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

import org.springblade.core.literule.annotation.LiteRuleComponent;
import org.springblade.core.literule.provider.Rule;
import org.springblade.core.literule.provider.SwitchRule;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

/**
 * 规则组件注册器
 * 用于扫描并注册带有@RuleComponent注解的类
 *
 * @author BladeX
 */
public class RuleComponentRegistrar extends AbstractComponentRegistrar {

	@Override
	protected TypeFilter getTypeFilter() {
		return new AnnotationTypeFilter(LiteRuleComponent.class);
	}

	@Override
	protected boolean isValidComponent(Class<?> clazz) {
		return Rule.class.isAssignableFrom(clazz) || SwitchRule.class.isAssignableFrom(clazz);
	}

	@Override
	protected String getBeanName(Class<?> clazz) {
		LiteRuleComponent annotation = clazz.getAnnotation(LiteRuleComponent.class);
		String beanName = annotation.id();
		
		// 如果id为空，则使用value
		if (beanName.isEmpty()) {
			beanName = annotation.value();
		}

		// 如果bean名称仍为空，使用类名首字母小写
		if (beanName.isEmpty()) {
			beanName = Character.toLowerCase(clazz.getSimpleName().charAt(0)) + clazz.getSimpleName().substring(1);
		}

		return beanName;
	}

	@Override
	protected String getComponentType() {
		return "Rule";
	}

	@Override
	protected Class<LiteRuleComponent> getAnnotationType() {
		return LiteRuleComponent.class;
	}
}
