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
package org.springblade.core.tool.jackson;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import org.springblade.core.tool.sensitive.SensitiveType;
import org.springblade.core.tool.sensitive.SensitiveUtil;
import org.springblade.core.tool.sensitive.SensitiveWord;
import org.springblade.core.tool.utils.StringPool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 敏感信息处理注解类
 *
 * @author BladeX
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface Sensitive {
	// 敏感词类型处理
	SensitiveType type() default SensitiveType.NONE;

	// 敏感词匹配处理
	SensitiveWord word() default SensitiveWord.NONE;

	// 自定义敏感词组
	String[] words() default {};

	// 自定义正则匹配
	String regex() default StringPool.EMPTY;

	// 自定义替换符
	String replacement() default SensitiveUtil.DEFAULT_REPLACEMENT;
}
