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
package org.springblade.core.tool.sensitive;

import lombok.Builder;
import lombok.Data;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 敏感信息处理配置类
 *
 * @author BladeX
 */
@Builder
@Data
public class SensitiveConfig {
	// 启用的内置正则脱敏类型
	private Set<SensitiveType> sensitiveTypes;

	// 启用的内置敏感词分组
	private Set<SensitiveWord> sensitiveWords;

	// 自定义敏感词列表
	private List<String> customSensitiveWords;

	// 自定义正则表达式脱敏规则
	private Map<String, Pattern> customPatterns;

	// 自定义替换文本（可选，有默认值）
	private String replacement;

	// 是否按行处理
	private boolean processLineByLine;
}
