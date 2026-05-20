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
package org.springblade.core.literule.builder;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 规则构建器辅助类
 * 提供执行栈管理等公共功能
 *
 * @author BladeX
 */
public class RuleBuilderExecutor {

	/**
	 * 使用线程本地执行栈，初始化为线程安全的空集合
	 * LinkedHashSet能保持插入顺序，在循环依赖检测时更有用
	 */
	private static final ThreadLocal<Set<String>> EXECUTION_STACK = ThreadLocal.withInitial(() ->
		Collections.synchronizedSet(new LinkedHashSet<>())
	);

	/**
	 * 记录规则ID的索引，用于优化循环依赖检测
	 */
	private static final Map<String, Integer> RULE_ID_INDICES = new ConcurrentHashMap<>();
	private static final AtomicInteger NEXT_INDEX = new AtomicInteger(0);

	/**
	 * 获取或创建规则ID对应的索引
	 *
	 * @param ruleId 规则ID
	 * @return 规则ID索引
	 */
	public static int getRuleIdIndex(String ruleId) {
		return RULE_ID_INDICES.computeIfAbsent(ruleId, id -> NEXT_INDEX.getAndIncrement());
	}

	/**
	 * 获取执行栈
	 *
	 * @return 执行栈
	 */
	public static Set<String> getExecutionStack() {
		return EXECUTION_STACK.get();
	}

	/**
	 * 清理执行栈
	 * 在规则执行完成后调用，避免ThreadLocal资源泄漏
	 */
	public static void clearExecutionStack() {
		EXECUTION_STACK.remove();
	}

	/**
	 * 检查执行栈中是否包含指定的规则ID
	 * 使用线程安全方式检查
	 *
	 * @param ruleId 规则ID
	 * @return 是否存在循环依赖
	 */
	public static boolean hasCircularDependency(String ruleId) {
		Set<String> stack = EXECUTION_STACK.get();
		synchronized (stack) {
			return stack.contains(ruleId);
		}
	}

	private RuleBuilderExecutor() {
		// 私有构造函数，防止实例化
	}
}
