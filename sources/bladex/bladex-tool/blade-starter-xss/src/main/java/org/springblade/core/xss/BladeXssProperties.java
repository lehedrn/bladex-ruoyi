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

package org.springblade.core.xss;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Xss配置类
 *
 * @author L.cm
 */
@Getter
@Setter
@RefreshScope
@ConfigurationProperties(BladeXssProperties.PREFIX)
public class BladeXssProperties {
	public static final String PREFIX = "blade.xss";

	/**
	 * 开启xss
	 */
	private boolean enabled = true;
	/**
	 * 全局：对文件进行首尾 trim
	 */
	private boolean trimText = true;
	/**
	 * 模式：clear 清理（默认），escape 转义
	 */
	private Mode mode = Mode.CLEAR;
	/**
	 * [clear 专用] prettyPrint，默认关闭： 保留换行
	 */
	private boolean prettyPrint = false;
	/**
	 * [clear 专用] 使用转义，默认关闭
	 */
	private boolean enableEscape = false;
	/**
	 * 拦截的路由，默认为所有
	 */
	private List<String> blockUrl = Collections.singletonList("/**");
	/**
	 * 放行的路由，默认为空
	 */
	private List<String> skipUrl = new ArrayList<>();

	public enum Mode {
		/**
		 * 清理
		 */
		CLEAR,
		/**
		 * 转义
		 */
		ESCAPE,
		/**
		 * 校验，抛出异常
		 */
		VALIDATE
	}

}
