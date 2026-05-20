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

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 防抖注解
 *
 * <p>
 * 在指定时间窗口内，相同 key 的请求只会执行一次，后续请求将被忽略。
 * 适用于防止重复提交、频繁调用等场景。
 * </p>
 *
 * <p>使用示例：</p>
 * <pre>
 * {@code
 * @RedisDebounce(key = "sms:send", param = "#phone", interval = 60)
 * public void sendSms(String phone) {
 *     // 60秒内同一手机号只能发送一次短信
 * }
 * }
 * </pre>
 *
 * @author BladeX
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RedisDebounce {

	/**
	 * 防抖的 key，必须：请保持唯一性
	 *
	 * @return key
	 */
	String key();

	/**
	 * 防抖参数，可选，支持 spring el # 读取方法参数和 @ 读取 spring bean
	 *
	 * @return param
	 */
	String param() default "";

	/**
	 * 防抖时间间隔，默认60秒
	 *
	 * @return 时间间隔
	 */
	long interval() default 60;

	/**
	 * 时间单位，默认为秒
	 *
	 * @return 时间单位
	 */
	TimeUnit timeUnit() default TimeUnit.SECONDS;

	/**
	 * 是否返回剩余时间信息，默认false
	 * 如果为true，当防抖生效时会抛出包含剩余时间信息的异常
	 *
	 * @return 是否返回剩余时间
	 */
	boolean includeRemainingTime() default false;

	/**
	 * 自定义防抖生效时的提示信息
	 *
	 * @return 提示信息
	 */
	String message() default "操作过于频繁，请稍后再试";

}
