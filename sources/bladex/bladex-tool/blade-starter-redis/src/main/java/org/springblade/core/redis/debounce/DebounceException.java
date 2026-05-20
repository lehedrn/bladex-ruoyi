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

import java.util.concurrent.TimeUnit;

/**
 * 防抖异常
 *
 * @author BladeX
 */
@Getter
public class DebounceException extends RuntimeException {

	/**
	 * 防抖 key
	 */
	private final String debounceKey;

	/**
	 * 剩余时间（秒）
	 */
	private final long remainingTime;

	/**
	 * 时间单位
	 */
	private final TimeUnit timeUnit;

	public DebounceException(String message, String debounceKey, long remainingTime, TimeUnit timeUnit) {
		super(message);
		this.debounceKey = debounceKey;
		this.remainingTime = remainingTime;
		this.timeUnit = timeUnit;
	}

	public DebounceException(String message, String debounceKey) {
		this(message, debounceKey, 0, TimeUnit.SECONDS);
	}

	/**
	 * 获取剩余时间（秒）
	 *
	 * @return 剩余时间
	 */
	public long getRemainingSeconds() {
		return timeUnit.toSeconds(remainingTime);
	}

	/**
	 * 获取剩余时间（毫秒）
	 *
	 * @return 剩余时间
	 */
	public long getRemainingMillis() {
		return timeUnit.toMillis(remainingTime);
	}

}
