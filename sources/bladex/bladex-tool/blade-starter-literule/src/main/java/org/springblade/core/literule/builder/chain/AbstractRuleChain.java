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
package org.springblade.core.literule.builder.chain;

import lombok.Getter;
import org.springblade.core.literule.builder.RuleBuilderExecutor;
import org.springblade.core.literule.exception.RuleException;
import org.springblade.core.literule.provider.Rule;
import org.springblade.core.tool.utils.SpringUtil;
import org.springblade.core.tool.utils.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 规则链抽象基类
 * 封装规则链的共同特性和行为
 *
 * @author BladeX
 */
@Getter
public abstract class AbstractRuleChain {

	/**
	 * 规则链ID
	 */
	protected String id;

	/**
	 * 缓存已经检查过的路径，避免重复检测
	 */
	private static final Map<String, Boolean> CIRCULAR_DEPENDENCY_CACHE = new ConcurrentHashMap<>();

	/**
	 * 构建规则链
	 *
	 * @return 规则实例
	 */
	public abstract RuleChain build();

	/**
	 * 从Spring容器中获取规则实例
	 *
	 * @param ruleId 规则ID
	 * @return 规则实例
	 */
	protected Rule getRuleFromSpring(String ruleId) {
		Rule rule = SpringUtil.getBean(ruleId, Rule.class);
		if (rule == null) {
			throw new RuleException("Rule not found: " + ruleId);
		}
		return rule;
	}

	/**
	 * 预加载规则实例
	 *
	 * @param ruleIds 规则ID集合
	 * @return 规则实例映射
	 */
	protected Map<String, Rule> preloadRules(Iterable<String> ruleIds) {
		Map<String, Rule> ruleInstances = new HashMap<>();
		for (String ruleId : ruleIds) {
			Rule rule = SpringUtil.getBean(ruleId, Rule.class);
			if (rule != null) {
				ruleInstances.put(ruleId, rule);
			}
		}
		return ruleInstances;
	}

	/**
	 * 检测循环依赖
	 * 使用RuleBuilderExecutor中的方法，确保线程安全
	 *
	 * @param ruleId 规则ID
	 * @throws RuleException 如果检测到循环依赖
	 */
	protected void checkCircularDependency(String ruleId) {
		if (RuleBuilderExecutor.hasCircularDependency(ruleId)) {
			// 构建依赖路径，帮助调试
			String path = String.join("->", RuleBuilderExecutor.getExecutionStack()) + "->" + ruleId;
			throw new RuleException("Circular dependency detected: " + path);
		}
	}

	/**
	 * 检测全局循环依赖
	 * 用于跨线程检测
	 *
	 * @param ruleId      规则ID
	 * @param globalStack 全局执行栈
	 * @throws RuleException 如果检测到循环依赖
	 */
	protected void checkGlobalCircularDependency(String ruleId, Set<String> globalStack) {
		if (globalStack.contains(ruleId)) {
			throw new RuleException("Global circular dependency detected: " + ruleId);
		}

		// 构建路径签名
		String pathSignature = String.join("->", globalStack) + "->" + ruleId;

		// 检查缓存
		if (CIRCULAR_DEPENDENCY_CACHE.containsKey(pathSignature)) {
			// 这个路径已经检测过，不会有循环依赖
			return;
		}

		// 将结果放入缓存
		CIRCULAR_DEPENDENCY_CACHE.put(pathSignature, Boolean.TRUE);
	}

	/**
	 * 执行规则，并处理执行栈
	 * 使用同步块确保线程安全
	 *
	 * @param ruleId 规则ID
	 * @param rule   规则实例
	 * @throws Exception 执行过程中的异常
	 */
	protected void executeWithStackManagement(String ruleId, Rule rule) throws Exception {
		checkCircularDependency(ruleId);

		// 获取当前线程的执行栈
		Set<String> executionStack = RuleBuilderExecutor.getExecutionStack();
		// 加锁确保线程安全
		synchronized (executionStack) {
			try {
				// 添加到执行栈
				executionStack.add(ruleId);

				// 执行规则
				rule.execute();
			} finally {
				// 从执行栈中移除
				executionStack.remove(ruleId);
			}
		}
	}

	/**
	 * 获取规则实例，优先从缓存中获取
	 *
	 * @param ruleId        规则ID
	 * @param ruleInstances 规则实例缓存
	 * @return 规则实例
	 */
	protected Rule getRuleInstance(String ruleId, Map<String, Rule> ruleInstances) {
		Rule rule = ruleInstances.get(ruleId);
		if (rule == null) {
			rule = getRuleFromSpring(ruleId);
		}
		return rule;
	}

	/**
	 * 获取规则列表实例
	 *
	 * @param rules 规则列表
	 * @return 规则实例映射
	 */
	protected Map<String, Rule> getListRuleInstances(List<Object> rules) {
		// 预先获取所有规则实例
		Map<String, Rule> branchRuleInstances = new HashMap<>();

		// 处理不同类型的规则
		for (Object rule : rules) {
			if (rule instanceof String ruleId) {
				// 如果是规则ID，添加到预加载列表
				Rule ruleBean = SpringUtil.getBean(ruleId, Rule.class);
				if (ruleBean != null) {
					branchRuleInstances.put(ruleId, ruleBean);
				}
			} else if (rule instanceof RuleChain ruleChain) {
				// 如果是规则链，使用适配器后添加到规则链列表
				String id = StringUtil.isNotBlank(ruleChain.id()) ? ruleChain.id() : ruleChain.getClass().getSimpleName();
				branchRuleInstances.put(id, new RuleChainAdapter(ruleChain));
			}
		}

		return branchRuleInstances;
	}
}
