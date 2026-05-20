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
package org.springblade.core.secure.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springblade.core.secure.exception.SecureException;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.ResultCode;
import org.springblade.core.tool.utils.WebUtil;

import java.util.ArrayList;
import java.util.List;

import static org.springblade.core.launch.constant.TokenConstant.AUTH_HEADER;

/**
 * AOP 鉴权
 *
 * @author Chill
 */
@Slf4j
@Aspect
public class AdminAspect {

	/**
	 * 切 方法 和 类上的 @IsAdmin 注解
	 *
	 * @param point 切点
	 * @return Object
	 * @throws Throwable 没有权限的异常
	 */
	@Around(
		"@annotation(org.springblade.core.secure.annotation.IsAdmin) || " +
			"@within(org.springblade.core.secure.annotation.IsAdmin)"
	)
	public Object preAuth(ProceedingJoinPoint point) throws Throwable {
		if (AuthUtil.isAdmin() || AuthUtil.isAdministrator()) {
			return point.proceed();
		}
		this.handleLogger();
		throw new SecureException(ResultCode.UN_AUTHORIZED);
	}

	/**
	 * 记录日志
	 */
	private void handleLogger() {
		HttpServletRequest request = WebUtil.getRequest();
		if (request != null && AuthUtil.hasAuth()) {
			// 记录日志
			List<Object> authArgs = new ArrayList<>();
			String authLogger = "\n\n================  IsAdmin Start  ================" +
				"\nPreAuth : IsAdmin" +
				"\nClientId : {}" +
				"\nBladeAuth : {}" +
				"\nBladeUser : {}" +
				"\nRequestURI : {}" +
				"\nRequestIP: {}" +
				"\nRequestParam: {}" +
				"\n================  IsAdmin  End   ================\n";
			authArgs.add(AuthUtil.getClientId(request));
			authArgs.add(WebUtil.getHeader(AUTH_HEADER));
			authArgs.add(AuthUtil.getUser());
			authArgs.add(WebUtil.getRequestURI(request));
			authArgs.add(WebUtil.getIP(request));
			authArgs.add(WebUtil.getRequestContent(request));
			log.warn(authLogger, authArgs.toArray());
		}
	}

}
