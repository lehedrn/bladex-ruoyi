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
package org.springblade.core.literule.engine;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.literule.builder.RuleBuilder;
import org.springblade.core.literule.builder.RuleBuilderExecutor;
import org.springblade.core.literule.builder.chain.RuleChain;
import org.springblade.core.literule.config.RuleEngineProperties;
import org.springblade.core.literule.context.RuleContextHolder;
import org.springblade.core.literule.context.RuleContextPropagator;
import org.springblade.core.literule.provider.LiteRuleResponse;
import org.springblade.core.literule.provider.RuleConfig;
import org.springblade.core.literule.provider.RuleContext;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 默认规则引擎执行器实现
 * 提供规则链的同步和异步执行能力
 *
 * @author BladeX
 */
@Slf4j
@RequiredArgsConstructor
public class BladeRuleEngineExecutor implements RuleEngineExecutor {

	private final ApplicationContext applicationContext;

	private final RuleEngineProperties properties;

	/**
	 * 规则链缓存
	 * 使用永久缓存策略，缓存一旦创建不会过期
	 */
	private final Map<String, RuleChain> ruleChainCache = new ConcurrentHashMap<>();

	@Override
	public <T extends RuleContext> LiteRuleResponse<T> execute(String chainId, Object requestData, T context, RuleConfig config) {
		long startTime = System.currentTimeMillis();

		// 参数校验
		if (chainId == null || chainId.isEmpty()) {
			return buildResponse(context, false, "Chain ID cannot be empty", startTime);
		}
		// 上下文校验
		if (context == null) {
			return buildResponse(null, false, "Context cannot be null", startTime);
		}

		try {
			// 从缓存获取规则链
			RuleChain chain = null;
			if (properties.getCache().isEnabled()) {
				chain = ruleChainCache.get(chainId);
			}

			// 缓存未命中，构建规则链
			if (chain == null) {
				RuleBuilder ruleBuilder = applicationContext.getBean(chainId, RuleBuilder.class);

				chain = ruleBuilder.build();

				// 放入缓存
				if (properties.getCache().isEnabled()) {
					addToCache(chainId, chain);
				}
			}

			// 设置上下文并执行规则链
			try (RuleContextHolder contextHolder = new RuleContextHolder(requestData, context, config)) {
				// 使用contextHolder执行规则链
				contextHolder.execute(chain);

				// 构建成功响应
				return buildResponse(context, context.isSuccess(),
					context.isSuccess() ? "Rule execution succeed" : "Rule execution failed", startTime);
			}

		} catch (Exception e) {
			String message = "Rule chain execution error: " + e.getMessage();
			log.error(message, e);

			// 设置上下文执行失败
			context.setSuccess(false);

			return buildResponse(context, false, message, startTime);
		} finally {
			// 清理执行栈
			RuleBuilderExecutor.clearExecutionStack();
		}
	}

	@Override
	public <T extends RuleContext> CompletableFuture<LiteRuleResponse<T>> executeAsync(String chainId, Object requestData, T context, RuleConfig config, Executor executor) {
		// 参数校验
		if (chainId == null || chainId.isEmpty()) {
			return CompletableFuture.completedFuture(
				buildResponse(context, false, "Chain ID cannot be empty", System.currentTimeMillis())
			);
		}
		// 上下文校验
		if (context == null) {
			return CompletableFuture.completedFuture(
				buildResponse(null, false, "Context cannot be null", System.currentTimeMillis())
			);
		}

		// 使用ContextPropagator包装任务，确保上下文正确传递
		Supplier<LiteRuleResponse<T>> asyncTask = RuleContextPropagator.wrapSupplier(
			// 异步执行逻辑
			() -> execute(chainId, requestData, context, config),
			// 传递当前线程的上下文
			context,
			// 传递规则配置
			config,
			// 传递额外参数
			requestData
		);

		// 提交异步任务
		return CompletableFuture.supplyAsync(asyncTask, executor)
			.orTimeout(properties.getExecution().getTimeout(), TimeUnit.MILLISECONDS)
			.exceptionally(e -> {
				String message = "Async rule execution error: " + e.getMessage();
				log.error(message, e);

				// 设置上下文执行失败
				context.setSuccess(false);

				long errorTime = System.currentTimeMillis();
				return buildResponse(context, false, message, errorTime);
			});
	}

	/**
	 * 构建规则执行响应
	 *
	 * @param context   规则上下文
	 * @param success   是否成功
	 * @param message   错误消息
	 * @param startTime 开始时间
	 * @param <T>       上下文类型
	 * @return 规则执行响应
	 */
	private <T extends RuleContext> LiteRuleResponse<T> buildResponse(T context, boolean success, String message, long startTime) {
		return LiteRuleResponse.<T>builder()
			.success(success)
			.message(message)
			.context(context)
			.executionTime(System.currentTimeMillis() - startTime)
			.build();
	}

	/**
	 * 清理缓存
	 */
	public void clearCache() {
		ruleChainCache.clear();
	}

	/**
	 * 从缓存中移除指定规则链
	 *
	 * @param chainId 规则链ID
	 */
	public void removeFromCache(String chainId) {
		ruleChainCache.remove(chainId);
	}

	/**
	 * 将规则链添加到缓存
	 *
	 * @param chainId 规则链ID
	 * @param rule    规则实例
	 */
	public void addToCache(String chainId, RuleChain rule) {
		if (properties.getCache().isEnabled()) {
			ruleChainCache.put(chainId, rule);
		}
	}

	/**
	 * 获取缓存大小
	 *
	 * @return 缓存中的规则链数量
	 */
	public int getCacheSize() {
		return ruleChainCache.size();
	}

	/**
	 * 应用关闭时清理资源
	 */
	@PreDestroy
	public void destroy() {
		clearCache();
	}
}
