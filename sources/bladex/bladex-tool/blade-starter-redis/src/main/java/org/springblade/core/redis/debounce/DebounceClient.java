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

import java.util.concurrent.TimeUnit;

/**
 * 防抖客户端
 *
 * @author BladeX
 */
public interface DebounceClient {

	/**
	 * 尝试防抖，如果在防抖时间内则返回false
	 *
	 * @param key      防抖键
	 * @param interval 防抖间隔
	 * @param timeUnit 时间单位
	 * @return true-可以执行，false-在防抖期内
	 */
	boolean tryDebounce(String key, long interval, TimeUnit timeUnit);

	/**
	 * 获取防抖剩余时间
	 *
	 * @param key      防抖键
	 * @param timeUnit 时间单位
	 * @return 剩余时间，-1表示不在防抖期内
	 */
	long getRemainingTime(String key, TimeUnit timeUnit);

	/**
	 * 清除防抖记录
	 *
	 * @param key 防抖键
	 * @return 是否成功清除
	 */
	boolean clearDebounce(String key);

	/**
	 * 检查是否在防抖期内
	 *
	 * @param key 防抖键
	 * @return true-在防抖期内，false-不在防抖期内
	 */
	boolean isInDebounce(String key);

	/**
	 * 设置防抖记录（不管是否已存在）
	 *
	 * @param key      防抖键
	 * @param interval 防抖间隔
	 * @param timeUnit 时间单位
	 */
	void setDebounce(String key, long interval, TimeUnit timeUnit);

}
