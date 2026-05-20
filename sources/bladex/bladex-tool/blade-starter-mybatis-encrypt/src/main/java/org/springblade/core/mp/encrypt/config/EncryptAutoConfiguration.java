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
package org.springblade.core.mp.encrypt.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.mp.encrypt.algorithm.EncryptAlgorithm;
import org.springblade.core.mp.encrypt.algorithm.EncryptAlgorithmFactory;
import org.springblade.core.mp.encrypt.exception.EncryptException;
import org.springblade.core.mp.encrypt.props.EncryptProperties;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.util.List;


/**
 * 字段加密自动配置
 *
 * @author BladeX
 */
@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = EncryptProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class EncryptAutoConfiguration {

	private final EncryptProperties properties;

	@PostConstruct
	public void init() {
		// 启动时验证配置
		if (StringUtil.isBlank(properties.getSecretKey())) {
			throw new EncryptException(
				"MyBatis field encryption is enabled but no secret key is configured. " +
					"Please configure 'blade.mybatis-plus.encrypt.secret-key' in application.yml " +
					"or disable encryption by setting 'blade.mybatis-plus.encrypt.enabled=false'");
		}
		log.info("MyBatis field encryption enabled successfully with algorithm: {}", properties.getAlgorithm());
	}

	@Bean
	@ConditionalOnMissingBean
	public EncryptAlgorithmFactory encryptAlgorithmFactory(List<EncryptAlgorithm> encryptors) {
		return new EncryptAlgorithmFactory(encryptors, properties);
	}

}
