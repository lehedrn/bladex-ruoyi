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


import lombok.Getter;
import org.springblade.core.literule.builder.chain.RuleChain;
import org.springblade.core.literule.provider.RuleConfig;
import org.springblade.core.literule.provider.RuleContext;
import org.springblade.core.literule.builder.RuleBuilderExecutor;

/**
 * 规则上下文持有者
 * 实现AutoCloseable接口，用于安全管理ThreadLocal资源
 *
 * @author BladeX
 */
public class RuleContextHolder implements AutoCloseable {
	/**
	 * 获取参数
	 */
	@Getter
	private final Object requestData;
	/**
	 * 获取上下文
	 */
	@Getter
	private final RuleContext context;
	/**
	 * 获取配置
	 */
	@Getter
	private final RuleConfig config;

	/**
	 * 原始参数
	 */
	private final Object originalRequestData;
	/**
	 * 原始上下文
	 */
	private final RuleContext originalContext;
	/**
	 * 原始配置
	 */
	private final RuleConfig originalConfig;

	/**
	 * 构造函数
	 *
	 * @param requestData 传递参数
	 * @param context     规则上下文
	 * @param config      规则配置
	 */
	public RuleContextHolder(Object requestData, RuleContext context, RuleConfig config) {
		// 保存最新值
		this.requestData = requestData;
		this.context = context;
		this.config = config;

		// 保存原始值
		this.originalRequestData = RuleContextManager.getRequestData();
		this.originalContext = RuleContextManager.getContext();
		this.originalConfig = RuleContextManager.getConfig();

		// 设置新的上下文
		RuleContextManager.set(requestData, context, config);
	}

	/**
	 * 执行规则
	 *
	 * @param rule 要执行的规则
	 * @throws Exception 执行过程中的异常
	 */
	public void execute(RuleChain rule) throws Exception {
		if (rule != null) {
			rule.execute();
		}
	}

	/**
	 * 检查上下文是否执行成功
	 *
	 * @return 是否执行成功
	 */
	public boolean isSuccess() {
		return context != null && context.isSuccess();
	}

	/**
	 * 关闭方法，清理ThreadLocal资源
	 */
	@Override
	public void close() {
		// 清理执行数据，处理嵌套调用和并发处理的场景
		if (originalRequestData != null) {
			RuleContextManager.setRequestData(originalRequestData);
		} else {
			RuleContextManager.removeRequestData();
		}
		if (originalContext != null) {
			RuleContextManager.setContext(originalContext);
		} else {
			RuleContextManager.removeContext();
		}
		if (originalConfig != null) {
			RuleContextManager.setConfig(originalConfig);
		} else {
			RuleContextManager.removeConfig();
		}

		// 清理执行栈，避免ThreadLocal资源泄漏
		RuleBuilderExecutor.clearExecutionStack();
	}
}
