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
 * Author: DreamLu (596392912@qq.com)
 */

package org.springblade.core.redis.config;


import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.redis.pubsub.RPubSubListenerDetector;
import org.springblade.core.redis.pubsub.RPubSubListenerLazyFilter;
import org.springblade.core.redis.pubsub.RPubSubPublisher;
import org.springblade.core.redis.pubsub.RedisPubSubPublisher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redisson pub/sub 发布配置
 *
 * @author L.cm
 */
@AutoConfiguration
public class RedisPubSubConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		return container;
	}

	@Bean
	public RPubSubPublisher topicEventPublisher(BladeRedis bladeRedis,
												RedisSerializer<Object> redisSerializer) {
		return new RedisPubSubPublisher(bladeRedis, redisSerializer);
	}

	@Bean
	@ConditionalOnBean(RedisSerializer.class)
	public RPubSubListenerDetector topicListenerDetector(RedisMessageListenerContainer redisMessageListenerContainer,
														 RedisSerializer<Object> redisSerializer) {
		return new RPubSubListenerDetector(redisMessageListenerContainer, redisSerializer);
	}

	@Bean
	public RPubSubListenerLazyFilter rPubSubListenerLazyFilter() {
		return new RPubSubListenerLazyFilter();
	}

}
