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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Redis 防抖客户端实现
 *
 * @author BladeX
 */
@Slf4j
@RequiredArgsConstructor
public class RedisDebounceClient implements DebounceClient {

	private static final String DEBOUNCE_VALUE = "1";

	private final StringRedisTemplate redisTemplate;
	private final RedisDebounceProperties properties;

	@Override
	public boolean tryDebounce(String key, long interval, TimeUnit timeUnit) {
		String fullKey = buildKey(key);
		Duration duration = Duration.of(interval, timeUnit.toChronoUnit());

		// 使用 SET NX EX 命令，如果键不存在则设置并返回true，存在则返回false
		Boolean result = redisTemplate.opsForValue().setIfAbsent(fullKey, DEBOUNCE_VALUE, duration);

		return Boolean.TRUE.equals(result);
	}

	@Override
	public long getRemainingTime(String key, TimeUnit timeUnit) {
		String fullKey = buildKey(key);
		long ttl = redisTemplate.getExpire(fullKey, TimeUnit.SECONDS);

		if (ttl <= 0) {
			return -1;
		}

		return timeUnit.convert(ttl, TimeUnit.SECONDS);
	}

	@Override
	public boolean clearDebounce(String key) {
		String fullKey = buildKey(key);
		return redisTemplate.delete(fullKey);
	}

	@Override
	public boolean isInDebounce(String key) {
		String fullKey = buildKey(key);
		return redisTemplate.hasKey(fullKey);
	}

	@Override
	public void setDebounce(String key, long interval, TimeUnit timeUnit) {
		String fullKey = buildKey(key);
		Duration duration = Duration.of(interval, timeUnit.toChronoUnit());
		redisTemplate.opsForValue().set(fullKey, DEBOUNCE_VALUE, duration);
	}

	/**
	 * 构建完整的 Redis 键
	 *
	 * @param key 原始键
	 * @return 完整的 Redis 键
	 */
	private String buildKey(String key) {
		return properties.getKeyPrefix() + key;
	}

}
