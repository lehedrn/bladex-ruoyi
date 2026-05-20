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
package org.springblade.core.excel.listener;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springblade.core.excel.support.ExcelException;
import org.springblade.core.excel.support.ExcelImporter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Excel监听器
 *
 * @author Chill
 */
@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImportListener<T> extends AnalysisEventListener<T> {

	/**
	 * 默认每隔3000条存储数据库
	 */
	private int batchCount = 3000;
	/**
	 * 缓存的数据列表
	 */
	private List<T> list = new ArrayList<>();
	/**
	 * 数据导入类
	 */
	private final ExcelImporter<T> importer;

	/**
	 * 数据映射类
	 */
	private final Class<T> clazz;

	/**
	 * 是否校验
	 */
	private final Boolean check;

	/**
	 * 表头缓存映射 - 静态缓存避免重复反射，提供懒加载机制
	 */
	private static final Map<Class<?>, List<String>> HEADER_CACHE = new ConcurrentHashMap<>();

	@Override
	public void invoke(T data, AnalysisContext analysisContext) {
		list.add(data);
		// 达到BATCH_COUNT，则调用importer方法入库，防止数据几万条数据在内存，容易OOM
		if (list.size() >= batchCount) {
			// 调用importer方法
			importer.save(list);
			// 存储完成清理list
			list.clear();
		}
	}

	@Override
	public void doAfterAllAnalysed(AnalysisContext analysisContext) {
		// 调用importer方法
		importer.save(list);
		// 存储完成清理list
		list.clear();
	}

	@Override
	public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
		if (!Boolean.TRUE.equals(check)) {
			super.invokeHeadMap(headMap, context);
			return;
		}
		List<String> expectedHeaders = extractExpectedHeaders();
		validateHeaderConsistency(headMap, expectedHeaders);
		super.invokeHeadMap(headMap, context);
	}

	/**
	 * 提取期望的表头列表
	 * 使用静态缓存实现懒加载，避免重复反射操作
	 */
	private List<String> extractExpectedHeaders() {
		return HEADER_CACHE.computeIfAbsent(clazz, this::parseHeadersFromClass);
	}

	/**
	 * 从类中解析表头信息
	 * 排除带有@ExcelIgnore注解的字段
	 */
	private List<String> parseHeadersFromClass(Class<?> targetClass) {
		return Arrays.stream(targetClass.getDeclaredFields())
			.filter(field -> field.isAnnotationPresent(ExcelProperty.class))
			.filter(field -> !field.isAnnotationPresent(ExcelIgnore.class))
			.map(this::extractHeaderFromField)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toList());
	}

	/**
	 * 从字段中提取表头名称
	 */
	private Optional<String> extractHeaderFromField(Field field) {
		ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
		String[] headerValues = annotation.value();
		return headerValues.length > 0 ? Optional.of(headerValues[0]) : Optional.empty();
	}

	/**
	 * 校验表头一致性
	 * 检查实际表头是否都存在于期望表头集合中
	 */
	private void validateHeaderConsistency(Map<Integer, String> actualHeaders, List<String> expectedHeaders) {
		// 获取期望表头的集合
		Set<String> expectedHeaderSet = new HashSet<>(expectedHeaders);
		// 检查实际表头是否有不在预期表头列表中
		actualHeaders.values().stream()
			.filter(actualHeader -> !expectedHeaderSet.contains(actualHeader))
			.findFirst()
			.ifPresent(invalidHeader -> {
				throw new ExcelException(
					String.format("表头校验失败：[%s] 不在预期的表头列表中，预期表头：%s", invalidHeader, expectedHeaders)
				);
			});
		// 检查是否有缺失的必要表头
		if (actualHeaders.size() != expectedHeaders.size()) {
			Set<String> actualHeaderSet = new HashSet<>(actualHeaders.values());
			List<String> missingHeaders = expectedHeaders.stream()
				.filter(expected -> !actualHeaderSet.contains(expected))
				.toList();
			if (!missingHeaders.isEmpty()) {
				throw new ExcelException(
					String.format("表头校验失败：缺少必要的表头：%s", missingHeaders)
				);
			}
		}
	}

}
