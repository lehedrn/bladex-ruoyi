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

import org.springblade.core.literule.provider.RuleConfig;
import org.springblade.core.literule.provider.LiteRuleResponse;
import org.springblade.core.literule.provider.RuleContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 规则引擎执行器接口
 * 提供同步和异步执行规则链的方法
 *
 * @author BladeX
 */
public interface RuleEngineExecutor {
	/**
	 * 同步执行规则链
	 *
	 * @param chainId     规则链ID
	 * @param requestData 传递参数
	 * @param context     规则上下文
	 * @param config      规则配置
	 * @param <T>         上下文类型
	 * @return 规则执行响应
	 */
	<T extends RuleContext> LiteRuleResponse<T> execute(String chainId, Object requestData, T context, RuleConfig config);

	/**
	 * 使用默认配置同步执行规则链
	 *
	 * @param chainId     规则链ID
	 * @param requestData 传递参数
	 * @param context     规则上下文
	 * @param <T>         上下文类型
	 * @return 规则执行响应
	 */
	default <T extends RuleContext> LiteRuleResponse<T> execute(String chainId, Object requestData, T context) {
		return execute(chainId, requestData, context, RuleConfig.getDefault());
	}

	/**
	 * 同步执行规则链
	 *
	 * @param chainId 规则链ID
	 * @param context 规则上下文
	 * @param config  规则配置
	 * @param <T>     上下文类型
	 * @return 规则执行响应
	 */
	default <T extends RuleContext> LiteRuleResponse<T> execute(String chainId, T context, RuleConfig config) {
		return execute(chainId, null, context, config);
	}

	/**
	 * 使用默认配置同步执行规则链
	 *
	 * @param chainId 规则链ID
	 * @param context 规则上下文
	 * @param <T>     上下文类型
	 * @return 规则执行响应
	 */
	default <T extends RuleContext> LiteRuleResponse<T> execute(String chainId, T context) {
		return execute(chainId, null, context, RuleConfig.getDefault());
	}

	/**
	 * 异步执行规则链
	 *
	 * @param chainId     规则链ID
	 * @param requestData 传递参数
	 * @param context     规则上下文
	 * @param config      规则配置
	 * @param executor    执行器
	 * @param <T>         上下文类型
	 * @return 规则执行响应的Future
	 */
	<T extends RuleContext> CompletableFuture<LiteRuleResponse<T>> executeAsync(String chainId, Object requestData, T context, RuleConfig config, Executor executor);

	/**
	 * 使用默认配置异步执行规则链
	 *
	 * @param chainId     规则链ID
	 * @param requestData 传递参数
	 * @param context     规则上下文
	 * @param executor    执行器
	 * @param <T>         上下文类型
	 * @return 规则执行响应的Future
	 */
	default <T extends RuleContext> CompletableFuture<LiteRuleResponse<T>> executeAsync(String chainId, Object requestData, T context, Executor executor) {
		return executeAsync(chainId, requestData, context, RuleConfig.getDefault(), executor);
	}

	/**
	 * 异步执行规则链
	 *
	 * @param chainId  规则链ID
	 * @param context  规则上下文
	 * @param config   规则配置
	 * @param executor 执行器
	 * @param <T>      上下文类型
	 * @return 规则执行响应的Future
	 */
	default <T extends RuleContext> CompletableFuture<LiteRuleResponse<T>> executeAsync(String chainId, T context, RuleConfig config, Executor executor) {
		return executeAsync(chainId, null, context, config, executor);
	}

	/**
	 * 使用默认配置异步执行规则链
	 *
	 * @param chainId  规则链ID
	 * @param context  规则上下文
	 * @param executor 执行器
	 * @param <T>      上下文类型
	 * @return 规则执行响应的Future
	 */
	default <T extends RuleContext> CompletableFuture<LiteRuleResponse<T>> executeAsync(String chainId, T context, Executor executor) {
		return executeAsync(chainId, null, context, RuleConfig.getDefault(), executor);
	}
}
