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

package org.springblade.core.redis.pubsub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.redis.cache.BladeRedis;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redisson pub/sub 发布器
 *
 * @author L.cm
 */
@Slf4j
@RequiredArgsConstructor
public class RedisPubSubPublisher implements InitializingBean, RPubSubPublisher {
	private final BladeRedis bladeRedis;
	private final RedisSerializer<Object> redisSerializer;

	@Override
	public <T> Long publish(String channel, T message) {
		return bladeRedis.publish(channel, message, redisSerializer::serialize);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("RPubSubPublisher init success.");
	}
}
