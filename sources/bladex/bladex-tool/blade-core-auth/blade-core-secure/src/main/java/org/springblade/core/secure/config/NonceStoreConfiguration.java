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
package org.springblade.core.secure.config;

import lombok.extern.slf4j.Slf4j;
import org.springblade.core.secure.nonce.LocalNonceStore;
import org.springblade.core.secure.nonce.NonceStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

/**
 * NonceStore自动配置
 * <p>
 * 用于防重放攻击检测的Nonce存储配置
 * 默认使用本地缓存实现，外部可自行实现NonceStore接口并注册Bean覆盖
 *
 * @author Chill
 */
@Slf4j
@Order
@AutoConfiguration(before = RegistryConfiguration.class)
public class NonceStoreConfiguration {

	/**
	 * 默认的NonceStore实现（本地缓存）
	 * <p>
	 * 使用ConcurrentHashMap存储nonce，适用于单机环境
	 * 分布式环境可自行实现NonceStore接口（如Redis实现）并注册Bean覆盖
	 *
	 * @return LocalNonceStore
	 */
	@Bean
	@ConditionalOnMissingBean(NonceStore.class)
	public NonceStore nonceStore() {
		return new LocalNonceStore();
	}

}
