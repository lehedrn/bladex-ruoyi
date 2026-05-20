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
 * Author: DreamLu (596392912@qq.com)
 */

package org.springblade.core.redis.pubsub;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 基于 redis pub sub 事件对象
 *
 * @author L.cm
 */
@Getter
@ToString
@RequiredArgsConstructor
public class RPubSubEvent<M> {
	/**
	 * 匹配模式时的正则
	 */
	private final CharSequence pattern;
	/**
	 * channel
	 */
	private final CharSequence channel;
	/**
	 * pub 的消息对象
	 */
	private final M msg;
}
