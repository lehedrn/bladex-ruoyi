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

package org.springblade.core.redis.debounce;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 防抖自动化配置
 *
 * @author BladeX
 */
@AutoConfiguration
@ConditionalOnBean(StringRedisTemplate.class)
@EnableConfigurationProperties(RedisDebounceProperties.class)
@ConditionalOnProperty(value = RedisDebounceProperties.PREFIX + ".enabled", havingValue = "true", matchIfMissing = true)
public class BladeDebounceAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public DebounceClient redisDebounceClient(StringRedisTemplate stringRedisTemplate, RedisDebounceProperties properties) {
		return new RedisDebounceClient(stringRedisTemplate, properties);
	}

	@Bean
	@ConditionalOnMissingBean
	public RedisDebounceAspect redisDebounceAspect(DebounceClient debounceClient, RedisDebounceProperties properties) {
		return new RedisDebounceAspect(debounceClient, properties);
	}

}
