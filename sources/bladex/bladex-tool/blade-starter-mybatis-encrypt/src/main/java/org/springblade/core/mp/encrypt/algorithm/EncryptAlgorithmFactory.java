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
package org.springblade.core.mp.encrypt.algorithm;

import lombok.AllArgsConstructor;
import org.springblade.core.mp.encrypt.props.EncryptProperties;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加密器工厂
 *
 * @author BladeX
 */
@AllArgsConstructor
public class EncryptAlgorithmFactory {

	/**
	 * 加密器集合
	 */
	private final List<EncryptAlgorithm> algorithms;

	/**
	 * 配置属性
	 */
	private final EncryptProperties properties;

	/**
	 * 加密器缓存池，使用 ConcurrentHashMap 保证线程安全
	 */
	private static final Map<String, EncryptAlgorithm> ENCRYPTOR_POOL = new ConcurrentHashMap<>();

	/**
	 * 根据算法类型获取加密器，如果缓存中不存在，则尝试创建
	 *
	 * @param algorithm 算法类型
	 * @return 加密器实例
	 */
	public EncryptAlgorithm create(Algorithm algorithm) {
		return create(algorithm.name());
	}

	/**
	 * 根据算法类型获取加密器
	 *
	 * @param algorithmType 算法类型字符串
	 * @return 加密器实例
	 */
	public EncryptAlgorithm create(String algorithmType) {
		// 使用 computeIfAbsent 实现延迟加载
		return ENCRYPTOR_POOL.computeIfAbsent(algorithmType, this::initializeAlgorithm);
	}

	/**
	 * 根据配置获取默认加密器
	 *
	 * @return 默认加密器实例
	 */
	public EncryptAlgorithm create() {
		Algorithm algorithm = properties.getAlgorithm();
		if (algorithm == null) {
			algorithm = Algorithm.AES; // 默认使用AES
		}
		return create(algorithm);
	}

	/**
	 * 初始化加密器
	 *
	 * @param algorithmType 算法类型
	 * @return 初始化的加密器
	 */
	private EncryptAlgorithm initializeAlgorithm(String algorithmType) {
		return algorithms.stream()
			.filter(algorithm -> algorithm.type().equals(algorithmType))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException(
				String.format("Unsupported encryption algorithm: %s", algorithmType)));
	}
}
