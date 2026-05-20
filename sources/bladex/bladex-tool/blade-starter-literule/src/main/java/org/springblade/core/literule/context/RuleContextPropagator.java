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
package org.springblade.core.literule.context;

import org.springblade.core.literule.builder.RuleBuilderExecutor;
import org.springblade.core.literule.provider.RuleConfig;
import org.springblade.core.literule.provider.RuleContext;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * 上下文传播器
 * 用于在异步或并行执行中传递和管理上下文
 *
 * @author BladeX
 */
public class RuleContextPropagator {

	/**
	 * 规则上下文
	 */
	private final RuleContext context;
	
	/**
	 * 规则配置
	 */
	private final RuleConfig config;
	
	/**
	 * 额外参数
	 */
	private final Object requestData;
	
	/**
	 * 构造函数
	 *
	 * @param context     规则上下文
	 * @param config      规则配置
	 * @param requestData 额外参数
	 */
	public RuleContextPropagator(RuleContext context, RuleConfig config, Object requestData) {
		this.context = context;
		this.config = config;
		this.requestData = requestData;
	}
	
	/**
	 * 恢复上下文
	 */
	public void restore() {
		RuleContextManager.setContext(context);
		RuleContextManager.setConfig(config);
		RuleContextManager.setRequestData(requestData);
	}

	/**
	 * 包装Runnable，确保在子线程中正确传递和清理上下文
	 *
	 * @param task    原始任务
	 * @param context 规则上下文
	 * @param config  规则配置
	 * @return 包装后的Runnable
	 */
	public static Runnable wrapRunnable(Runnable task, RuleContext context, RuleConfig config) {
		return wrapRunnable(task, context, config, null);
	}
	
	/**
	 * 包装Runnable，确保在子线程中正确传递和清理上下文
	 *
	 * @param task        原始任务
	 * @param context     规则上下文
	 * @param config      规则配置
	 * @param requestData 额外参数
	 * @return 包装后的Runnable
	 */
	public static Runnable wrapRunnable(Runnable task, RuleContext context, RuleConfig config, Object requestData) {
		return () -> {
			// 设置上下文
			RuleContextManager.setContext(context);
			RuleContextManager.setConfig(config);
			RuleContextManager.setRequestData(requestData);
			try {
				// 执行任务
				task.run();
			} finally {
				// 清理上下文
				RuleContextManager.clear();
				RuleBuilderExecutor.clearExecutionStack();
			}
		};
	}

	/**
	 * 包装Callable，确保在子线程中正确传递和清理上下文
	 *
	 * @param task    原始任务
	 * @param context 规则上下文
	 * @param config  规则配置
	 * @param <V>     返回值类型
	 * @return 包装后的Callable
	 */
	public static <V> Callable<V> wrapCallable(Callable<V> task, RuleContext context, RuleConfig config) {
		return wrapCallable(task, context, config, null);
	}
	
	/**
	 * 包装Callable，确保在子线程中正确传递和清理上下文
	 *
	 * @param task        原始任务
	 * @param context     规则上下文
	 * @param config      规则配置
	 * @param requestData 额外参数
	 * @param <V>         返回值类型
	 * @return 包装后的Callable
	 */
	public static <V> Callable<V> wrapCallable(Callable<V> task, RuleContext context, RuleConfig config, Object requestData) {
		return () -> {
			// 设置上下文
			RuleContextManager.setContext(context);
			RuleContextManager.setConfig(config);
			RuleContextManager.setRequestData(requestData);
			try {
				// 执行任务
				return task.call();
			} finally {
				// 清理上下文
				RuleContextManager.clear();
				RuleBuilderExecutor.clearExecutionStack();
			}
		};
	}

	/**
	 * 包装Supplier，确保在子线程中正确传递和清理上下文
	 *
	 * @param supplier 原始供应商
	 * @param context  规则上下文
	 * @param config   规则配置
	 * @param <V>      返回值类型
	 * @return 包装后的Supplier
	 */
	public static <V> Supplier<V> wrapSupplier(Supplier<V> supplier, RuleContext context, RuleConfig config) {
		return wrapSupplier(supplier, context, config, null);
	}
	
	/**
	 * 包装Supplier，确保在子线程中正确传递和清理上下文
	 *
	 * @param supplier    原始供应商
	 * @param context     规则上下文
	 * @param config      规则配置
	 * @param requestData 额外参数
	 * @param <V>         返回值类型
	 * @return 包装后的Supplier
	 */
	public static <V> Supplier<V> wrapSupplier(Supplier<V> supplier, RuleContext context, RuleConfig config, Object requestData) {
		return () -> {
			// 设置上下文
			RuleContextManager.setContext(context);
			RuleContextManager.setConfig(config);
			RuleContextManager.setRequestData(requestData);
			try {
				// 执行任务
				return supplier.get();
			} finally {
				// 清理上下文
				RuleContextManager.clear();
			}
		};
	}
}
