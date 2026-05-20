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
package org.springblade.core.secure.nonce;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 基于本地缓存的Nonce存储实现
 * <p>
 * 使用ConcurrentHashMap + ScheduledExecutorService实现
 * <p>
 *
 * @author Chill
 */
@Slf4j
public class LocalNonceStore implements NonceStore {

	/**
	 * 本地缓存，存储nonce及其过期时间
	 */
	private final ConcurrentHashMap<String, Long> nonceCache = new ConcurrentHashMap<>();

	/**
	 * 定时清理任务执行器
	 */
	private final ScheduledExecutorService cleanupExecutor;

	/**
	 * 清理间隔（秒）
	 */
	private static final long CLEANUP_INTERVAL_SECONDS = 60;

	public LocalNonceStore() {
		// 创建单线程定时执行器用于清理过期nonce
		this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread thread = new Thread(r, "nonce-cleanup");
			thread.setDaemon(true);
			return thread;
		});
		// 定期清理过期的nonce
		this.cleanupExecutor.scheduleAtFixedRate(
			this::cleanupExpired,
			CLEANUP_INTERVAL_SECONDS,
			CLEANUP_INTERVAL_SECONDS,
			TimeUnit.SECONDS
		);
	}

	@Override
	public boolean tryStore(String nonce, long expireSeconds) {
		String key = buildKey(nonce);
		long expireTime = System.currentTimeMillis() + (expireSeconds * 1000);
		// putIfAbsent 返回null表示key不存在，设置成功
		// 返回非null表示key已存在，设置失败（重放攻击）
		Long existing = nonceCache.putIfAbsent(key, expireTime);
		if (existing == null) {
			return true;
		}
		// 检查是否已过期，如果过期则可以覆盖
		if (System.currentTimeMillis() > existing) {
			nonceCache.put(key, expireTime);
			return true;
		}
		return false;
	}

	@Override
	public boolean exists(String nonce) {
		String key = buildKey(nonce);
		Long expireTime = nonceCache.get(key);
		if (expireTime == null) {
			return false;
		}
		// 检查是否已过期
		if (System.currentTimeMillis() > expireTime) {
			nonceCache.remove(key);
			return false;
		}
		return true;
	}

	/**
	 * 清理过期的nonce
	 */
	private void cleanupExpired() {
		long now = System.currentTimeMillis();
		nonceCache.entrySet().removeIf(entry -> now > entry.getValue());
	}

	/**
	 * 构建缓存key
	 *
	 * @param nonce 随机数
	 * @return 完整的缓存key
	 */
	private String buildKey(String nonce) {
		return CACHE_PREFIX + nonce;
	}

	/**
	 * 关闭清理执行器
	 */
	public void shutdown() {
		if (cleanupExecutor != null && !cleanupExecutor.isShutdown()) {
			cleanupExecutor.shutdown();
		}
	}

}
