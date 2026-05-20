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
package org.springblade.core.literule.core;

import org.springblade.core.literule.annotation.LiteRuleComponent;
import org.springblade.core.literule.context.RuleContextManager;
import org.springblade.core.literule.exception.RuleException;

import lombok.extern.slf4j.Slf4j;
import org.springblade.core.literule.provider.RuleConfig;
import org.springblade.core.literule.provider.RuleContext;

/**
 * 规则基类
 * 提供公共能力，如上下文管理、配置获取等
 *
 * @author BladeX
 */
@Slf4j
public abstract class AbstractRuleComponent {

	/**
	 * 获取规则ID
	 * 优先从注解获取，如果没有注解则使用类名
	 *
	 * @return 规则ID
	 */
	protected String getRuleId() {
		LiteRuleComponent annotation = this.getClass().getAnnotation(LiteRuleComponent.class);
		if (annotation != null) {
			String id = annotation.id();
			if (!id.isEmpty()) {
				return id;
			}
			return annotation.value();
		}
		return this.getClass().getSimpleName();
	}

	/**
	 * 获取额外参数
	 *
	 * @param <T> 参数类型
	 * @return 额外参数
	 */
	protected <T> T getRequestData() {
		return RuleContextManager.getRequestData();
	}

	/**
	 * 获取上下文
	 *
	 * @param clazz 上下文类型
	 * @param <T>   上下文类型
	 * @return 上下文实例
	 */
	protected <T extends RuleContext> T getContextBean(Class<T> clazz) {
		RuleContext context = RuleContextManager.getContext();
		if (context == null) {
			throw new RuleException("Rule context is not set");
		}

		// 直接类型匹配
		if (clazz.isInstance(context)) {
			return clazz.cast(context);
		}

		// 检查类名是否相同（忽略包名）
		if (clazz.getSimpleName().equals(context.getClass().getSimpleName())) {
			try {
				// 尝试强制转换
				@SuppressWarnings("unchecked")
				T result = (T) context;
				return result;
			} catch (ClassCastException e) {
				log.warn("Failed to cast context: {} to {}", context.getClass().getName(), clazz.getName());
			}
		}

		throw new RuleException("Context type not match: " + context.getClass().getName() + ", expected: " + clazz.getName());
	}

	/**
	 * 获取配置
	 *
	 * @return 规则配置
	 */
	protected RuleConfig getConfig() {
		return RuleContextManager.getConfig();
	}

}
