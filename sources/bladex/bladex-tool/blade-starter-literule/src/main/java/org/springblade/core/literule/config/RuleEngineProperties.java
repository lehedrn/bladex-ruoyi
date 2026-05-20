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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.ArrayList;
import java.util.List;

/**
 * 规则引擎配置属性
 * 集中管理规则引擎的配置项
 *
 * @author BladeX
 */
@Data
@ConfigurationProperties(prefix = "literule")
public class RuleEngineProperties {

	/**
	 * 执行配置
	 */
	private Execution execution = new Execution();

	/**
	 * 缓存配置
	 */
	private Cache cache = new Cache();

	/**
	 * 预加载配置
	 */
	private Preload preload = new Preload();

	/**
	 * 执行配置
	 */
	@Data
	public static class Execution {
		/**
		 * 执行超时时间（毫秒）
		 */
		private long timeout = 30000;

		/**
		 * 并行执行的最大线程数
		 */
		private int maxParallelThreads = 10;

		/**
		 * 线程池队列容量
		 */
		private int queueCapacity = 1000;

		/**
		 * 线程池拒绝策略
		 * ABORT: 抛出异常
		 * CALLER_RUNS: 在调用者线程中执行
		 * DISCARD: 丢弃任务
		 * DISCARD_OLDEST: 丢弃最旧的任务
		 */
		private String rejectedPolicy = "CALLER_RUNS";

		/**
		 * 线程池线程保持活跃时间（秒）
		 */
		private int keepAliveSeconds = 60;
	}

	/**
	 * 缓存配置
	 */
	@Data
	public static class Cache {
		/**
		 * 是否启用缓存
		 */
		private boolean enabled = true;
	}

	/**
	 * 预加载配置
	 */
	@Data
	public static class Preload {
		/**
		 * 是否启用预加载
		 */
		private boolean enabled = true;

		/**
		 * 高优先级规则ID列表，优先预加载
		 */
		private List<String> highPriorityRules = new ArrayList<>();
	}
}
