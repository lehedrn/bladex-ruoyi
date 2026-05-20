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

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis 防抖配置属性
 *
 * @author BladeX
 */
@Getter
@Setter
@ConfigurationProperties(RedisDebounceProperties.PREFIX)
public class RedisDebounceProperties {
	public static final String PREFIX = "blade.redis.debounce";

	/**
	 * 是否开启防抖功能，默认为：true
	 */
	private Boolean enabled = Boolean.TRUE;

	/**
	 * 防抖键前缀，默认为：blade:debounce:
	 */
	private String keyPrefix = "blade:debounce:";

	/**
	 * 默认防抖间隔时间（秒），默认为：60秒
	 */
	private Long defaultInterval = 60L;

	/**
	 * 是否启用调试日志，默认为：false
	 */
	private Boolean enableDebugLog = Boolean.FALSE;

}
