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

import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.StringUtil;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * 敏感信息处理工具类
 * <p>
 * 支持以下功能：
 * 1. 内置敏感类型处理（手机号、邮箱、身份证等）
 * 2. 自定义正则表达式处理
 * 3. 敏感词处理
 * 4. 支持按行处理或整体处理
 * 5. 支持自定义替换符
 * 6. 支持泛型返回值
 * </p>
 *
 * @author BladeX
 */
public class SensitiveUtil {
	// 默认替换符
	public static final String DEFAULT_REPLACEMENT = "******";
	// 换行符
	private static final String LINE_SEPARATOR = System.lineSeparator();

	// 预编译的默认配置
	private static final SensitiveConfig DEFAULT_CONFIG = SensitiveConfig.builder()
		.sensitiveTypes(EnumSet.of(
			SensitiveType.MOBILE,
			SensitiveType.ID_CARD,
			SensitiveType.EMAIL,
			SensitiveType.BANK_CARD,
			SensitiveType.CREDIT_CARD,
			SensitiveType.IP_ADDRESS,
			SensitiveType.MAC_ADDRESS
		))
		.sensitiveWords(EnumSet.of(
			SensitiveWord.SECURE,
			SensitiveWord.PAYMENT,
			SensitiveWord.AUTHENTICATION,
			SensitiveWord.SESSION
		))
		.processLineByLine(true)
		.replacement(DEFAULT_REPLACEMENT)
		.build();

	/**
	 * 使用默认配置处理敏感信息
	 *
	 * @param content 待处理内容
	 * @return 处理后的结果
	 */
	public static String process(String content) {
		return process(content, Function.identity());
	}

	/**
	 * 使用默认配置处理敏感信息
	 *
	 * @param content  待处理内容
	 * @param supplier 结果转换函数
	 * @param <T>      返回值类型
	 * @return 处理后的结果
	 */
	public static <T> T process(String content, Function<String, T> supplier) {
		return process(content, DEFAULT_CONFIG, supplier);
	}

	/**
	 * 使用自定义配置处理敏感信息
	 *
	 * @param content 待处理内容
	 * @param config  自定义配置
	 * @return 处理后的结果
	 */
	public static String process(String content, SensitiveConfig config) {
		return process(content, config, Function.identity());
	}

	/**
	 * 使用自定义配置处理敏感信息
	 *
	 * @param content  待处理内容
	 * @param config   自定义配置
	 * @param supplier 结果转换函数
	 * @param <T>      返回值类型
	 * @return 处理后的结果
	 */
	public static <T> T process(String content, SensitiveConfig config, Function<String, T> supplier) {
		if (StringUtil.isBlank(content)) {
			return supplier.apply(content);
		}

		String processedContent = content;
		String replacement = StringUtil.isBlank(config.getReplacement()) ?
			DEFAULT_REPLACEMENT : config.getReplacement();

		// 1. 处理内置敏感类型
		if (!CollectionUtil.isEmpty(config.getSensitiveTypes())) {
			processedContent = processRegexPatterns(processedContent, config.getSensitiveTypes());
		}

		// 2. 处理自定义正则
		if (!CollectionUtil.isEmpty(config.getCustomPatterns())) {
			processedContent = processCustomPatterns(processedContent,
				config.getCustomPatterns(),
				replacement
			);
		}

		// 3. 处理敏感词
		if (!CollectionUtil.isEmpty(config.getSensitiveWords())) {
			List<String> words = getSensitiveWords(config.getSensitiveWords());
			processedContent = processSensitiveWords(processedContent,
				words,
				replacement,
				config.isProcessLineByLine()
			);
		}

		return supplier.apply(processedContent);
	}

	/**
	 * 处理单个敏感类型
	 *
	 * @param content 待处理内容
	 * @param type    敏感类型
	 * @return 处理后的结果
	 */
	public static String process(String content, SensitiveType type) {
		return process(content, type, Function.identity());
	}

	/**
	 * 处理单个敏感类型
	 *
	 * @param content  待处理内容
	 * @param type     敏感类型
	 * @param supplier 结果转换函数
	 * @param <T>      返回值类型
	 * @return 处理后的结果
	 */
	public static <T> T process(String content, SensitiveType type, Function<String, T> supplier) {
		if (StringUtil.isBlank(content) || type == null) {
			return supplier.apply(content);
		}
		String processed = processRegexPatterns(content, Collections.singleton(type));
		return supplier.apply(processed);
	}

	/**
	 * 处理多个敏感类型
	 *
	 * @param content 待处理内容
	 * @param types   敏感类型集合
	 * @return 处理后的结果
	 */
	public static String process(String content, Set<SensitiveType> types) {
		return process(content, types, Function.identity());
	}

	/**
	 * 处理多个敏感类型
	 *
	 * @param content  待处理内容
	 * @param types    敏感类型集合
	 * @param supplier 结果转换函数
	 * @param <T>      返回值类型
	 * @return 处理后的结果
	 */
	public static <T> T process(String content, Set<SensitiveType> types, Function<String, T> supplier) {
		if (StringUtil.isBlank(content) || CollectionUtil.isEmpty(types)) {
			return supplier.apply(content);
		}
		String processed = processRegexPatterns(content, types);
		return supplier.apply(processed);
	}

	/**
	 * 使用自定义正则处理（使用默认替换符）
	 *
	 * @param content 待处理内容
	 * @param regex   正则表达式
	 * @return 处理后的结果
	 */
	public static String processWithRegex(String content, String regex) {
		return processWithRegex(content, regex, Function.identity());
	}

	/**
	 * 使用自定义正则处理（使用默认替换符）
	 *
	 * @param content  待处理内容
	 * @param regex    正则表达式
	 * @param supplier 结果转换函数
	 * @param <T>      返回值类型
	 * @return 处理后的结果
	 */
	public static <T> T processWithRegex(String content, String regex, Function<String, T> supplier) {
		return processWithRegex(content, regex, DEFAULT_REPLACEMENT, supplier);
	}

	/**
	 * 使用自定义正则处理（使用自定义替换符）
	 *
	 * @param content     待处理内容
	 * @param regex       正则表达式
	 * @param replacement 替换内容
	 * @return 处理后的结果
	 */
	public static String processWithRegex(String content, String regex, String replacement) {
		return processWithRegex(content, regex, replacement, Function.identity());
	}

	/**
	 * 使用自定义正则处理（使用自定义替换符）
	 *
	 * @param content     待处理内容
	 * @param regex       正则表达式
	 * @param replacement 替换内容
	 * @param supplier    结果转换函数
	 * @param <T>         返回值类型
	 * @return 处理后的结果
	 */
	public static <T> T processWithRegex(String content, String regex, String replacement, Function<String, T> supplier) {
		if (StringUtil.isBlank(content) || StringUtil.isBlank(regex)) {
			return supplier.apply(content);
		}
		Pattern pattern = Pattern.compile(regex);
		String processed = pattern.matcher(content).replaceAll(replacement);
		return supplier.apply(processed);
	}

	/**
	 * 处理敏感词（使用默认配置）
	 *
	 * @param content 待处理内容
	 * @param words   敏感词列表
	 * @return 处理后的结果
	 */
	public static String processWithWords(String content, List<String> words) {
		return processWithWords(content, words, Function.identity());
	}

	/**
	 * 处理敏感词（使用默认配置）
	 *
	 * @param content  待处理内容
	 * @param words    敏感词列表
	 * @param supplier 结果转换函数
	 * @param <T>      返回值类型
	 * @return 处理后的结果
	 */
	public static <T> T processWithWords(String content, List<String> words, Function<String, T> supplier) {
		return processWithWords(content, words, DEFAULT_REPLACEMENT, true, supplier);
	}

	/**
	 * 处理敏感词（使用完整参数）
	 *
	 * @param content           待处理内容
	 * @param words             敏感词列表
	 * @param replacement       替换符
	 * @param processLineByLine 是否按行处理
	 * @return 处理后的结果
	 */
	public static String processWithWords(String content,
										  List<String> words,
										  String replacement,
										  boolean processLineByLine) {
		return processWithWords(content, words, replacement, processLineByLine, Function.identity());
	}

	/**
	 * 处理敏感词（使用完整参数）
	 *
	 * @param content           待处理内容
	 * @param words             敏感词列表
	 * @param replacement       替换符
	 * @param processLineByLine 是否按行处理
	 * @param supplier          结果转换函数
	 * @param <T>               返回值类型
	 * @return 处理后的结果
	 */
	public static <T> T processWithWords(String content,
										 List<String> words,
										 String replacement,
										 boolean processLineByLine,
										 Function<String, T> supplier) {
		if (StringUtil.isBlank(content) || CollectionUtil.isEmpty(words)) {
			return supplier.apply(content);
		}
		String processed = processSensitiveWords(content, words, replacement, processLineByLine);
		return supplier.apply(processed);
	}

	/**
	 * 处理正则表达式
	 *
	 * @param content 待处理内容
	 * @param types   敏感类型集合
	 * @return 处理后的结果
	 */
	private static String processRegexPatterns(String content, Set<SensitiveType> types) {
		String result = content;
		for (SensitiveType type : types) {
			result = type.replaceAll(result);
		}
		return result;
	}

	/**
	 * 处理自定义正则表达式
	 *
	 * @param content     待处理内容
	 * @param patterns    自定义正则表达式
	 * @param replacement 替换符
	 * @return 处理后的结果
	 */
	private static String processCustomPatterns(String content,
												Map<String, Pattern> patterns,
												String replacement) {
		String result = content;
		for (Pattern pattern : patterns.values()) {
			result = pattern.matcher(result).replaceAll(replacement);
		}
		return result;
	}

	/**
	 * 获取敏感词列表
	 *
	 * @param groups 敏感词分组
	 * @return 敏感词列表
	 */
	private static List<String> getSensitiveWords(Set<SensitiveWord> groups) {
		List<String> words = new ArrayList<>();
		for (SensitiveWord group : groups) {
			words.addAll(group.getWords());
		}
		return words;
	}

	/**
	 * 处理敏感词
	 *
	 * @param content           待处理内容
	 * @param words             敏感词列表
	 * @param replacement       替换符
	 * @param processLineByLine 是否按行处理
	 * @return 处理后的结果
	 */
	private static String processSensitiveWords(String content,
												List<String> words,
												String replacement,
												boolean processLineByLine) {
		return processLineByLine ?
			maskSensitiveLines(content, words, replacement) :
			maskSensitiveContent(content, words, replacement);
	}

	/**
	 * 按行处理敏感词
	 *
	 * @param content     待处理内容
	 * @param words       敏感词列表
	 * @param replacement 替换符
	 * @return 处理后的结果
	 */
	private static String maskSensitiveLines(String content,
											 List<String> words,
											 String replacement) {
		String[] lines = content.split(LINE_SEPARATOR);
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			boolean containsSensitive = words.stream()
				.anyMatch(word -> line.toLowerCase().contains(word.toLowerCase()));

			result.append(containsSensitive ? replacement : line);

			if (i < lines.length - 1) {
				result.append(LINE_SEPARATOR);
			}
		}

		return result.toString();
	}

	/**
	 * 处理整体内容中的敏感词
	 *
	 * @param content     待处理内容
	 * @param words       敏感词列表
	 * @param replacement 替换符
	 * @return 处理后的结果
	 */
	private static String maskSensitiveContent(String content,
											   List<String> words,
											   String replacement) {
		boolean containsSensitive = words.stream()
			.anyMatch(word -> content.toLowerCase().contains(word.toLowerCase()));

		return containsSensitive ? replacement : content;
	}
}
