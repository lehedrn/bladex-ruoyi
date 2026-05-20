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
package org.springblade.core.literule.core;

import lombok.Data;
import org.springblade.core.literule.provider.RuleContext;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 规则上下文基类
 * 提供上下文数据存储、错误信息管理和执行时间记录等功能
 *
 * @author BladeX
 */
@Data
public abstract class RuleContextComponent implements RuleContext {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 上下文数据，使用线程安全的ConcurrentHashMap
	 */
	private final Map<String, Object> contextData = new ConcurrentHashMap<>();

	/**
	 * 错误消息列表，使用线程安全的同步列表
	 */
	private final List<String> errorMessages = Collections.synchronizedList(new ArrayList<>());

	/**
	 * 是否执行成功，使用AtomicBoolean保证线程安全
	 */
	private final AtomicBoolean success = new AtomicBoolean(true);

	/**
	 * 规则执行时间记录，使用线程安全的ConcurrentHashMap
	 */
	private final Map<String, Long> executionTimes = new ConcurrentHashMap<>();

	/**
	 * 添加错误信息
	 *
	 * @param message 错误信息
	 */
	public void addError(String message) {
		this.errorMessages.add(message);
		this.success.set(false);
	}

	/**
	 * 记录规则执行时间
	 *
	 * @param ruleId        规则ID
	 * @param executionTime 执行时间(毫秒)
	 */
	public void recordExecutionTime(String ruleId, long executionTime) {
		executionTimes.put(ruleId, executionTime);
	}

	/**
	 * 获取上下文数据
	 *
	 * @param key 数据键
	 * @param <T> 数据类型
	 * @return 数据值
	 */
	@SuppressWarnings("unchecked")
	public <T> T getData(String key) {
		return (T) contextData.get(key);
	}

	/**
	 * 设置上下文数据
	 *
	 * @param key   数据键
	 * @param value 数据值
	 */
	public void setData(String key, Object value) {
		contextData.put(key, value);
	}

	/**
	 * 获取执行状态
	 *
	 * @return 是否成功
	 */
	@Override
	public boolean isSuccess() {
		return success.get();
	}

	/**
	 * 设置执行状态
	 *
	 * @param success 执行状态
	 */
	@Override
	public void setSuccess(boolean success) {
		this.success.set(success);
	}
}
