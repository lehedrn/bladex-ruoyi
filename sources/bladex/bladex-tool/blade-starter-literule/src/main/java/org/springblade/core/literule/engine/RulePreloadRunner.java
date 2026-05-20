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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.literule.annotation.LiteRuleComponent;
import org.springblade.core.literule.annotation.RuleEngineComponent;
import org.springblade.core.literule.builder.RuleBuilder;
import org.springblade.core.literule.builder.chain.RuleChain;
import org.springblade.core.literule.config.RuleEngineProperties;
import org.springblade.core.literule.provider.Rule;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 规则预加载器
 * 在应用启动时预加载所有规则
 *
 * @author BladeX
 */
@Slf4j
@Order
@Component
@RequiredArgsConstructor
public class RulePreloadRunner implements ApplicationRunner {

	private final RuleEngineProperties properties;
	private final ApplicationContext applicationContext;
	private final RuleEngineExecutor ruleEngineExecutor;

	/**
	 * 应用启动时执行预加载
	 *
	 * @param args 应用参数
	 */
	@Override
	public void run(ApplicationArguments args) {
		if (!properties.getPreload().isEnabled()) {
			log.info("Rule preload is disabled, skipping preload step");
			return;
		}

		log.info("Starting to preload blade rules");
		long startTime = System.currentTimeMillis();

		Map<String, Rule> rules = preloadRules();
		Map<String, RuleChain> flows = preloadFlowBuilders();
		warmupCache(flows);
		log.info("Rule preload completed, loaded {} rules and {} rule flows, cost {}ms",
			rules.size(), flows.size(), System.currentTimeMillis() - startTime);
	}

	/**
	 * 预加载所有规则
	 *
	 * @return 规则实例映射表
	 */
	private Map<String, Rule> preloadRules() {
		Map<String, Rule> rules = new HashMap<>();

		// 先加载高优先级的规则
		List<String> highPriorityRules = properties.getPreload().getHighPriorityRules();
		if (!highPriorityRules.isEmpty()) {
			log.info("Starting to load high-priority rules: {}", highPriorityRules);
			for (String ruleId : highPriorityRules) {
				try {
					Rule rule = applicationContext.getBean(ruleId, Rule.class);
					rules.put(ruleId, rule);
					log.debug("Loaded high-priority rule: {}", ruleId);
				} catch (Exception e) {
					log.warn("Failed to load high-priority rule: {}, reason: {}", ruleId, e.getMessage());
				}
			}
		}

		// 加载其他所有标注了@RuleComponent的规则
		Map<String, Object> ruleComponents = applicationContext.getBeansWithAnnotation(LiteRuleComponent.class);
		for (Map.Entry<String, Object> entry : ruleComponents.entrySet()) {
			if (entry.getValue() instanceof Rule) {
				String ruleId = entry.getKey();
				if (!rules.containsKey(ruleId)) {
					rules.put(ruleId, (Rule) entry.getValue());
					log.debug("Loaded rule: {}", ruleId);
				}
			}
		}

		return rules;
	}

	/**
	 * 预加载所有规则流构建器
	 *
	 * @return 规则流实例映射表
	 */
	private Map<String, RuleChain> preloadFlowBuilders() {
		Map<String, RuleChain> flows = new HashMap<>();
		Map<String, Object> builderBeans = applicationContext.getBeansWithAnnotation(RuleEngineComponent.class);

		for (Map.Entry<String, Object> entry : builderBeans.entrySet()) {
			String beanName = entry.getKey();
			Object bean = entry.getValue();

			try {
				if (bean instanceof RuleBuilder ruleBuilder) {
					// 构建规则链
					RuleChain rule = ruleBuilder.build();
					flows.put(beanName, rule);
				}
				log.debug("Loaded rule flow, builder: {}", beanName);
			} catch (Exception e) {
				log.warn("Failed to load rule flow, builder: {}, reason: {}", beanName, e.getMessage());
			}
		}

		return flows;
	}

	/**
	 * 预热缓存
	 *
	 * @param preloadedFlows 预加载的规则流
	 */
	private void warmupCache(Map<String, RuleChain> preloadedFlows) {
		if (preloadedFlows.isEmpty() || !properties.getCache().isEnabled()) {
			return;
		}

		log.info("Starting to warm up the blade rule cache");
		int count = 0;

		// 将规则流添加到缓存中
		for (Map.Entry<String, RuleChain> entry : preloadedFlows.entrySet()) {
			try {
				// 调用执行器的缓存方法
				if (ruleEngineExecutor instanceof BladeRuleEngineExecutor) {
					((BladeRuleEngineExecutor) ruleEngineExecutor).addToCache(entry.getKey(), entry.getValue());
					count++;
				}
			} catch (Exception e) {
				log.warn("Failed to cache rule flow: {}, reason: {}", entry.getKey(), e.getMessage());
			}
		}

		log.info("Rule cache warmup completed, cached {} rule flows", count);
	}
}
