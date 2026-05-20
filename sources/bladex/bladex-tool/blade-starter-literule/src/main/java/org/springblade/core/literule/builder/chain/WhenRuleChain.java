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
import lombok.RequiredArgsConstructor;
import org.springblade.core.literule.builder.RuleBuilderExecutor;
import org.springblade.core.literule.context.RuleContextPropagator;
import org.springblade.core.literule.context.RuleContextManager;
import org.springblade.core.literule.exception.RuleException;
import org.springblade.core.literule.provider.Rule;
import org.springblade.core.literule.provider.RuleConfig;
import org.springblade.core.literule.provider.RuleContext;

import java.util.*;
import java.util.concurrent.*;

/**
 * 并行规则链构建器
 * 用于构建并行执行的规则链
 *
 * @author BladeX
 */
@Getter
public class WhenRuleChain extends AbstractRuleChain {
	private final List<String> ruleIds = new ArrayList<>();
	private Executor executor;
	private int timeout = 30000; // 默认超时时间30秒

	/**
	 * 设置规则链ID
	 * @param id 规则链ID
	 * @return 构建器实例
	 */
	public WhenRuleChain ID(String id) {
		this.id = id;
		return this;
	}

	/**
	 * 添加规则
	 *
	 * @param ruleIds 规则ID数组
	 * @return 构建器实例
	 */
	public WhenRuleChain WHEN(String... ruleIds) {
		this.ruleIds.addAll(Arrays.asList(ruleIds));
		return this;
	}

	/**
	 * 设置执行器
	 *
	 * @param executor 执行器
	 * @return 构建器实例
	 */
	public WhenRuleChain EXECUTOR(Executor executor) {
		this.executor = executor;
		return this;
	}

	/**
	 * 设置超时时间
	 *
	 * @param timeout 超时时间(毫秒)
	 * @return 构建器实例
	 */
	public WhenRuleChain timeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	/**
	 * 构建规则链
	 *
	 * @return 规则实例
	 */
	@Override
	public RuleChain build() {
		// 预先获取所有规则实例
		final Map<String, Rule> ruleInstances = preloadRules(ruleIds);
		return new ParallelRuleExecutor(ruleInstances);
	}

	/**
	 * 并行规则执行器内部类
	 */
	@RequiredArgsConstructor
	private class ParallelRuleExecutor implements RuleChain {
		private final Map<String, Rule> ruleInstances;

		@Override
		public String id() {
			return getId();
		}

		@Override
		public void execute() {
			// 获取执行器
			Executor exec = executor != null ? executor : ForkJoinPool.commonPool();

			// 获取当前上下文和配置，用于传递给子线程
			final RuleContext parentContext = RuleContextManager.getContext();
			final RuleConfig parentConfig = RuleContextManager.getConfig();

			// 获取超时时间，优先使用本地配置，否则使用全局配置
			long actualTimeout = timeout > 0 ? timeout :
				parentConfig != null ? parentConfig.getTimeout() : 30000;

			// 并行执行所有规则
			List<CompletableFuture<Void>> futures = new ArrayList<>();
			List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

			// 创建线程安全的全局执行栈，用于跨线程检测循环依赖
			final Set<String> globalStack = ConcurrentHashMap.newKeySet();
			// 将当前线程的执行栈复制到全局执行栈
			globalStack.addAll(RuleBuilderExecutor.getExecutionStack());

			for (String ruleId : ruleIds) {
				// 检测全局循环依赖
				checkGlobalCircularDependency(ruleId, globalStack);

				// 获取规则实例
				final Rule finalRule = getRuleInstance(ruleId, ruleInstances);
				final String finalRuleId = ruleId;

				// 添加到全局执行栈
				globalStack.add(finalRuleId);

				// 使用ContextPropagator包装任务，确保上下文正确传递
				Runnable ruleTask = RuleContextPropagator.wrapRunnable(() -> {
					try {
						// 添加到线程本地执行栈
						RuleBuilderExecutor.getExecutionStack().add(finalRuleId);

						// 执行规则
						finalRule.execute();
					} catch (Throwable e) {
						// 收集所有异常，包括Error
						exceptions.add(e);
						if (e instanceof Error) {
							throw (Error) e;
						} else {
							throw new CompletionException(e);
						}
					} finally {
						// 从线程本地执行栈中移除
						RuleBuilderExecutor.getExecutionStack().remove(finalRuleId);
					}
				}, parentContext, parentConfig);

				// 提交任务并配置超时
				CompletableFuture<Void> future = CompletableFuture
					.runAsync(ruleTask, exec)
					.orTimeout(actualTimeout, TimeUnit.MILLISECONDS)
					.exceptionally(ex -> {
						// 处理超时和其他异常
						Throwable cause = ex;
						if (ex instanceof CompletionException && ex.getCause() != null) {
							cause = ex.getCause();
						}

						// 添加额外的上下文信息
						if (cause instanceof TimeoutException) {
							String msg = String.format("Rule '%s' timed out after %d ms", finalRuleId, actualTimeout);
							exceptions.add(new RuleException(msg, cause));
						} else if (!exceptions.contains(cause)) {
							// 只添加还未被添加的异常
							exceptions.add(cause);
						}
						return null;
					});

				futures.add(future);
			}

			// 等待所有规则执行完成
			try {
				CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
			} catch (CompletionException e) {
				// 如果有多个异常，抛出一个包含所有异常的复合异常
				if (exceptions.size() > 1) {
					RuleException compositeException = new RuleException(
						String.format("%d rules failed in parallel execution", exceptions.size()));
					for (Throwable exception : exceptions) {
						compositeException.addSuppressed(exception instanceof Exception ?
							exception : new RuleException(exception.getMessage(), exception));
					}
					throw compositeException;
				} else if (exceptions.size() == 1) {
					// 如果只有一个异常，直接抛出
					Throwable ex = exceptions.get(0);
					if (ex instanceof RuntimeException) {
						throw (RuntimeException) ex;
					} else if (ex instanceof Error) {
						throw (Error) ex;
					} else if (ex instanceof Exception) {
						throw new RuleException("Rule execution failed", (Exception) ex);
					} else {
						throw new RuleException("Unknown error: " + ex.getMessage(), ex);
					}
				}
				throw e;
			} finally {
				// 清空全局执行栈，避免内存泄漏
				globalStack.clear();
			}
		}
	}

	/**
	 * 检测全局循环依赖
	 *
	 * @param ruleId      规则ID
	 * @param globalStack 全局执行栈
	 * @throws RuleException 如果检测到循环依赖
	 */
	public void checkGlobalCircularDependency(String ruleId, Set<String> globalStack) {
		if (globalStack.contains(ruleId)) {
			throw new RuleException("Global circular dependency detected: " + ruleId);
		}
	}
}
