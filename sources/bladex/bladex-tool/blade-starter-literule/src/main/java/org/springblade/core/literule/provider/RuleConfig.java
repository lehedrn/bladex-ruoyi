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
package org.springblade.core.literule.provider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 规则执行配置类
 * 用于控制规则执行的行为，如是否启用时间监控、日志等
 *
 * @author BladeX
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleConfig {
	/**
	 * 是否启用执行时间监控
	 */
	private boolean enableTimeMonitor = true;

	/**
	 * 是否打印执行时间
	 */
	private boolean printExecutionTime = true;

	/**
	 * 是否启用日志
	 */
	private boolean enableLogging = true;

	/**
	 * 默认超时时间30秒
	 */
	private int timeout = 30000;

	/**
	 * 获取默认配置
	 *
	 * @return 默认配置实例
	 */
	public static RuleConfig getDefault() {
		return RuleConfig.builder()
			.enableTimeMonitor(true)
			.printExecutionTime(true)
			.enableLogging(true)
			.timeout(30000)
			.build();
	}
}
