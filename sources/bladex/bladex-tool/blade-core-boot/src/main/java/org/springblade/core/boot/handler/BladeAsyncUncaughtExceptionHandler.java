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
package org.springblade.core.boot.handler;


import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.boot.error.ErrorType;
import org.springblade.core.boot.error.ErrorUtil;
import org.springblade.core.context.BladeContext;
import org.springblade.core.launch.props.BladeProperties;
import org.springblade.core.log.constant.EventConstant;
import org.springblade.core.log.event.ErrorLogEvent;
import org.springblade.core.log.model.LogError;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 异步任务异常处理器
 *
 * @author BladeX
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class BladeAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
	private final BladeContext bladeContext;
	private final BladeProperties bladeProperties;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	public void handleUncaughtException(@Nonnull Throwable error, @Nonnull Method method, @Nonnull Object... params) {
		log.error("Unexpected exception occurred invoking async method: {}", method, error);
		LogError logError = new LogError();
		// 服务信息、环境、异常类型
		logError.setParams(ErrorType.ASYNC.getType());
		logError.setEnv(bladeProperties.getEnv());
		logError.setServiceId(bladeProperties.getName());
		logError.setRequestUri(bladeContext.getRequestId());
		// 堆栈信息
		ErrorUtil.initErrorInfo(error, logError);
		Map<String, Object> event = new HashMap<>(16);
		event.put(EventConstant.EVENT_LOG, logError);
		eventPublisher.publishEvent(new ErrorLogEvent(event));
	}
}
