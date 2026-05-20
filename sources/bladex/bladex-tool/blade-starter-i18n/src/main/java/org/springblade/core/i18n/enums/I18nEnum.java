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
package org.springblade.core.i18n.enums;

import org.springblade.core.i18n.utils.I18nUtil;

import java.util.Locale;

/**
 * 国际化枚举基础接口
 * <p>
 * 为枚举类提供完整的国际化支持能力。实现接口并提供 {@link #getCode()} 方法，
 * 即可自动获得所有国际化消息获取功能。
 * </p>
 *
 * <h3>设计特点：</h3>
 * <ul>
 *   <li>提供完整的getMessage和getMessageOrDefault方法族</li>
 *   <li>支持参数化消息和多语言</li>
 *   <li>建议覆盖toString方法返回国际化消息</li>
 * </ul>
 *
 * <h3>实现示例：</h3>
 * <pre>{@code
 * @Getter
 * @AllArgsConstructor
 * public enum BladeMessage implements I18nEnum {
 *     HELLO("hello.world"),
 *     WELCOME("welcome.message");
 *
 *     private final String code;
 *
 *     @Override
 *     public String toString() {
 *         return getMessage();
 *     }
 * }
 *
 * // 使用示例
 * String msg = BladeMessage.HELLO.getMessage();
 * String paramMsg = BladeMessage.WELCOME.getMessage("张三");
 * String enMsg = BladeMessage.HELLO.getMessage(Locale.ENGLISH);
 * "结果：" + BladeMessage.HELLO  // 自动显示国际化消息
 * }</pre>
 *
 * @author BladeX
 */
public interface I18nEnum {

	/**
	 * 获取国际化消息代码
	 * <p>
	 * 这是唯一需要实现的抽象方法，返回的代码将作为国际化资源文件的key。
	 * </p>
	 *
	 * @return 国际化消息代码
	 */
	String getCode();

	// ==================== 基础getMessage方法 ====================

	/**
	 * 获取当前语言环境下的国际化消息
	 *
	 * @return 国际化后的消息内容
	 */
	default String getMessage() {
		return I18nUtil.get(getCode());
	}

	/**
	 * 获取带参数的国际化消息
	 * <p>
	 * 参数将替换消息模板中的占位符，支持以下格式：
	 * <ul>
	 *   <li>MessageFormat格式：{0}, {1}, {2}...</li>
	 *   <li>Spring格式：{min}, {max}, {name}...</li>
	 * </ul>
	 * </p>
	 *
	 * @param args 消息参数，将替换消息模板中的占位符
	 * @return 格式化后的国际化消息
	 */
	default String getMessage(Object... args) {
		return I18nUtil.get(getCode(), args);
	}

	/**
	 * 获取指定语言环境下的国际化消息
	 *
	 * @param locale 目标语言环境
	 * @return 指定语言的国际化消息
	 */
	default String getMessage(Locale locale) {
		return I18nUtil.get(getCode(), locale);
	}

	/**
	 * 获取带参数的指定语言环境下的国际化消息
	 *
	 * @param args   消息参数
	 * @param locale 目标语言环境
	 * @return 格式化后的指定语言国际化消息
	 */
	default String getMessage(Object[] args, Locale locale) {
		return I18nUtil.get(getCode(), args, locale);
	}

	// ==================== getMessageOrDefault方法 ====================

	/**
	 * 获取带默认值的国际化消息
	 * <p>
	 * 当消息未找到或获取失败时，返回提供的默认值。
	 * </p>
	 *
	 * @param defaultValue 当消息未找到时返回的默认值
	 * @return 国际化消息或默认值
	 */
	default String getMessageOrDefault(String defaultValue) {
		return I18nUtil.getOrDefault(getCode(), defaultValue);
	}

	/**
	 * 获取带参数和默认值的国际化消息
	 *
	 * @param args         消息参数
	 * @param defaultValue 默认值
	 * @return 格式化后的国际化消息或默认值
	 */
	default String getMessageOrDefault(Object[] args, String defaultValue) {
		return I18nUtil.getOrDefault(getCode(), args, defaultValue);
	}

	/**
	 * 获取带默认值的指定语言环境下的国际化消息
	 *
	 * @param locale       目标语言环境
	 * @param defaultValue 默认值
	 * @return 指定语言的国际化消息或默认值
	 */
	default String getMessageOrDefault(Locale locale, String defaultValue) {
		return I18nUtil.getOrDefault(getCode(), locale, defaultValue);
	}

	/**
	 * 获取带参数和默认值的指定语言环境下的国际化消息
	 *
	 * @param args         消息参数
	 * @param locale       目标语言环境
	 * @param defaultValue 默认值
	 * @return 格式化后的指定语言国际化消息或默认值
	 */
	default String getMessageOrDefault(Object[] args, Locale locale, String defaultValue) {
		return I18nUtil.getOrDefault(getCode(), args, locale, defaultValue);
	}

	// ==================== 存在性检查方法 ====================

	/**
	 * 检查消息是否存在
	 * <p>
	 * 用于在获取消息前验证消息是否已定义，避免返回未翻译的代码。
	 * </p>
	 *
	 * @return 如果消息存在返回true，否则返回false
	 */
	default boolean exists() {
		return I18nUtil.exists(getCode());
	}

	/**
	 * 检查指定语言环境下的消息是否存在
	 *
	 * @param locale 目标语言环境
	 * @return 如果消息存在返回true，否则返回false
	 */
	default boolean exists(Locale locale) {
		return I18nUtil.exists(getCode(), locale);
	}

}
