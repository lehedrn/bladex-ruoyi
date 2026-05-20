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

/**
 * Nonce存储接口，用于防重放攻击检测
 * <p>
 * 默认使用本地缓存实现 {@link LocalNonceStore}
 * <p>
 * 分布式环境可自行实现此接口（如Redis实现）并注册为Spring Bean覆盖默认实现：
 * <pre>
 * {@code
 * @Bean
 * public NonceStore nonceStore(BladeRedis bladeRedis) {
 *     return new RedisNonceStore(bladeRedis);
 * }
 * }
 * </pre>
 *
 * @author Chill
 */
public interface NonceStore {

	/**
	 * 缓存前缀
	 */
	String CACHE_PREFIX = "blade:secure:nonce:";

	/**
	 * 尝试存储nonce值
	 * <p>
	 * 如果nonce已存在，返回false（表示重放攻击）
	 * 如果nonce不存在，存储并返回true
	 *
	 * @param nonce          随机数
	 * @param expireSeconds  过期时间（秒）
	 * @return true-存储成功（首次请求），false-已存在（重放攻击）
	 */
	boolean tryStore(String nonce, long expireSeconds);

	/**
	 * 检查nonce是否存在
	 *
	 * @param nonce 随机数
	 * @return true-存在，false-不存在
	 */
	boolean exists(String nonce);

}
