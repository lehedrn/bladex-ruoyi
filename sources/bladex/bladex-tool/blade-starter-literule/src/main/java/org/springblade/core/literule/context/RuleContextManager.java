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
package org.springblade.core.literule.context;

import org.springblade.core.literule.provider.RuleConfig;
import org.springblade.core.literule.provider.RuleContext;

/**
 * 规则上下文管理器
 * 用于在线程内共享规则上下文和配置
 *
 * @author BladeX
 */
public class RuleContextManager {
	/**
	 * 支持子线程继承父线程的上下文
	 */
	private static final InheritableThreadLocal<RuleContext> RULE_CONTEXT = new InheritableThreadLocal<>();

	/**
	 * 支持子线程继承父线程的配置
	 */
	private static final InheritableThreadLocal<RuleConfig> RULE_CONFIG = new InheritableThreadLocal<>() {
		@Override
		protected RuleConfig initialValue() {
			return RuleConfig.getDefault();
		}
	};

	/**
	 * 支持子线程继承父线程的额外参数
	 */
	private static final InheritableThreadLocal<Object> REQUEST_DATA = new InheritableThreadLocal<>();

	/**
	 * 设置上下文和配置
	 *
	 * @param requestData 额外参数
	 * @param context     规则上下文
	 * @param config      规则配置
	 */
	public static void set(Object requestData, RuleContext context, RuleConfig config) {
		RULE_CONTEXT.set(context);
		if (requestData != null) {
			REQUEST_DATA.set(requestData);
		}
		if (config != null) {
			RULE_CONFIG.set(config);
		}
	}

	/**
	 * 设置上下文
	 *
	 * @param context 规则上下文
	 */
	public static void setContext(RuleContext context) {
		RULE_CONTEXT.set(context);
	}

	/**
	 * 设置配置
	 *
	 * @param config 规则配置
	 */
	public static void setConfig(RuleConfig config) {
		if (config != null) {
			RULE_CONFIG.set(config);
		}
	}

	/**
	 * 设置额外参数
	 *
	 * @param data 额外参数
	 */
	public static void setRequestData(Object data) {
		REQUEST_DATA.set(data);
	}

	/**
	 * 获取额外参数
	 *
	 * @param <T> 参数类型
	 * @return 额外参数
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getRequestData() {
		return (T) REQUEST_DATA.get();
	}

	/**
	 * 获取上下文
	 *
	 * @return 规则上下文
	 */
	public static RuleContext getContext() {
		return RULE_CONTEXT.get();
	}

	/**
	 * 获取配置
	 *
	 * @return 规则配置
	 */
	public static RuleConfig getConfig() {
		return RULE_CONFIG.get();
	}

	/**
	 * 清理上下文
	 */
	public static void removeContext() {
		RULE_CONTEXT.remove();
	}

	/**
	 * 清理配置
	 */
	public static void removeConfig() {
		RULE_CONFIG.remove();
	}

	/**
	 * 清理额外参数
	 */
	public static void removeRequestData() {
		REQUEST_DATA.remove();
	}

	/**
	 * 清理上下文和配置
	 */
	public static void clear() {
		RULE_CONTEXT.remove();
		RULE_CONFIG.remove();
		REQUEST_DATA.remove();
	}

	/**
	 * 检查上下文是否已设置
	 *
	 * @return 上下文是否已设置
	 */
	public static boolean isSet() {
		return RULE_CONTEXT.get() != null;
	}

	private RuleContextManager() {
		// 私有构造函数
	}
}
