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

import org.springblade.core.literule.engine.BladeRuleEngineExecutor;
import org.springblade.core.literule.engine.RuleEngineExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.*;

/**
 * 规则引擎自动配置类
 * 用于自动配置规则引擎相关组件
 *
 * @author BladeX
 */
@Configuration
@EnableScheduling
@Import({EngineComponentRegistrar.class, RuleComponentRegistrar.class})
@EnableConfigurationProperties(RuleEngineProperties.class)
public class RuleEngineAutoConfiguration {

	/**
	 * 注册规则引擎执行器
	 *
	 * @return 规则引擎执行器
	 */
	@Bean
	@ConditionalOnMissingBean
	public RuleEngineExecutor ruleEngineExecutor(ApplicationContext applicationContext, RuleEngineProperties properties) {
		return new BladeRuleEngineExecutor(applicationContext, properties);
	}

	/**
	 * 注册规则执行线程池
	 *
	 * @param properties 规则引擎配置属性
	 * @return 线程池
	 */
	@Bean
	@ConditionalOnMissingBean(name = "ruleExecutorThreadPool")
	public ThreadPoolExecutor ruleExecutorThreadPool(RuleEngineProperties properties) {
		ThreadFactory threadFactory = r -> {
			Thread thread = new Thread(r);
			thread.setName("rule-executor-" + thread.getId());
			thread.setDaemon(true);
			return thread;
		};

		// 创建队列
		ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor(properties, threadFactory);

		// 设置拒绝策略
		String rejectedPolicy = properties.getExecution().getRejectedPolicy();
		RejectedExecutionHandler handler = switch (rejectedPolicy) {
			case "ABORT" -> new ThreadPoolExecutor.AbortPolicy();
			case "DISCARD" -> new ThreadPoolExecutor.DiscardPolicy();
			case "DISCARD_OLDEST" -> new ThreadPoolExecutor.DiscardOldestPolicy();
			default -> new ThreadPoolExecutor.CallerRunsPolicy();
		};

		threadPoolExecutor.setRejectedExecutionHandler(handler);

		return threadPoolExecutor;
	}

	private static ThreadPoolExecutor getThreadPoolExecutor(RuleEngineProperties properties, ThreadFactory threadFactory) {
		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(properties.getExecution().getQueueCapacity());
		return new ThreadPoolExecutor(
			properties.getExecution().getMaxParallelThreads(), // 核心线程数
			properties.getExecution().getMaxParallelThreads(), // 最大线程数
			properties.getExecution().getKeepAliveSeconds(),   // 空闲线程保持时间
			TimeUnit.SECONDS,                                  // 时间单位
			workQueue,                                         // 任务队列
			threadFactory                                      // 线程工厂
		);
	}
}
