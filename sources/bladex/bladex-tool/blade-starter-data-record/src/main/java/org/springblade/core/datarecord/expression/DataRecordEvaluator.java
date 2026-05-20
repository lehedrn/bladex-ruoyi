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
package org.springblade.core.datarecord.expression;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.datarecord.constant.DataRecordConstant;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表达式评估器
 * <p>
 * 支持简单的条件表达式评估，用于DataRecord和FieldRecord的条件判断。
 * <p>
 * 特性：
 * - 支持基本的比较操作符：==, !=, >, <, >=, <=
 * - 支持类级别变量访问：#oldData.field, #newData.field
 * - 支持字段级别变量访问：#oldValue, #newValue
 * - 支持大小写不敏感的字段访问：#oldData.category 可以匹配 CATEGORY、Category 等
 * - 表达式缓存机制提升性能
 * - 宽松的错误处理，失败时默认允许记录
 *
 * @author BladeX
 */
@Slf4j
public class DataRecordEvaluator {

	/**
	 * 表达式缓存
	 */
	private static final Map<String, CompiledExpression> EXPRESSION_CACHE = new ConcurrentHashMap<>();

	/**
	 * 变量模式 - 匹配 #variable 或 #object.property 格式
	 */
	private static final Pattern VARIABLE_PATTERN = Pattern.compile(DataRecordConstant.VARIABLE_PATTERN_REGEX);

	/**
	 * 数值模式 - 匹配整数和小数
	 */
	private static final Pattern NUMBER_PATTERN = Pattern.compile(DataRecordConstant.NUMBER_PATTERN_REGEX);

	/**
	 * 字符串模式 - 匹配被引号包围的字符串
	 */
	private static final Pattern STRING_PATTERN = Pattern.compile(DataRecordConstant.STRING_PATTERN_REGEX);

	/**
	 * 评估实体级别的条件表达式
	 *
	 * @param condition 条件表达式
	 * @param oldData   修改前数据
	 * @param newData   修改后数据
	 * @return 是否满足条件
	 */
	public static boolean evaluateEntityCondition(String condition, Map<String, Object> oldData, Map<String, Object> newData) {
		if (StringUtil.isBlank(condition)) {
			return true;
		}

		try {
			// 创建上下文
			EvaluationContext context = new EvaluationContext();
			context.putVariable(DataRecordConstant.OLD_DATA, oldData);
			context.putVariable(DataRecordConstant.NEW_DATA, newData);

			return evaluateCondition(condition, context);
		} catch (Exception e) {
			if (!Func.hasEmpty(oldData, newData)) {
				log.warn("评估实体条件表达式失败: {}, 错误: {}", condition, e.getMessage());
			}
			return true; // 表达式错误时默认允许记录
		}
	}

	/**
	 * 评估字段级别的条件表达式
	 *
	 * @param condition 条件表达式
	 * @param oldValue  旧值
	 * @param newValue  新值
	 * @return 是否满足条件
	 */
	public static boolean evaluateFieldCondition(String condition, Object oldValue, Object newValue) {
		if (StringUtil.isBlank(condition)) {
			return true;
		}

		try {
			// 创建上下文
			EvaluationContext context = new EvaluationContext();
			context.putVariable(DataRecordConstant.OLD_VALUE, oldValue);
			context.putVariable(DataRecordConstant.NEW_VALUE, newValue);

			return evaluateCondition(condition, context);
		} catch (Exception e) {
			if (!Func.hasEmpty(oldValue, newValue)) {
				log.warn("评估字段条件表达式失败: {}, 错误: {}", condition, e.getMessage());
			}
			return true; // 表达式错误时默认允许记录
		}
	}

	/**
	 * 验证表达式语法
	 *
	 * @param condition 条件表达式
	 * @return 验证结果
	 */
	public static ExpressionValidationResult validateExpression(String condition) {
		if (StringUtil.isBlank(condition)) {
			return ExpressionValidationResult.valid();
		}

		try {
			// 编译表达式检查语法
			CompiledExpression compiled = compileExpression(condition);

			// 检查是否包含有效的操作符
			String template = compiled.template;
			boolean hasOperator = template.contains(DataRecordConstant.EQUALS) || template.contains(DataRecordConstant.NOT_EQUALS) ||
				template.contains(DataRecordConstant.GREATER_THAN) || template.contains(DataRecordConstant.LESS_THAN) ||
				template.contains(DataRecordConstant.GREATER_THAN_OR_EQUAL) || template.contains(DataRecordConstant.LESS_THAN_OR_EQUAL);

			if (!hasOperator && !compiled.variables.isEmpty()) {
				return ExpressionValidationResult.invalid("表达式缺少有效的比较操作符");
			}

			return ExpressionValidationResult.valid();
		} catch (Exception e) {
			return ExpressionValidationResult.invalid("表达式语法错误: " + e.getMessage());
		}
	}

	/**
	 * 评估条件表达式
	 *
	 * @param condition 条件表达式
	 * @param context   评估上下文
	 * @return 是否满足条件
	 */
	private static boolean evaluateCondition(String condition, EvaluationContext context) {
		// 从缓存获取或编译表达式
		CompiledExpression compiled = EXPRESSION_CACHE.computeIfAbsent(condition, DataRecordEvaluator::compileExpression);

		// 替换变量
		String expression = compiled.template;
		for (Map.Entry<String, String> entry : compiled.variables.entrySet()) {
			String variable = entry.getKey();
			String placeholder = entry.getValue();
			// 如果 value 为 null，则代表此变量未参与记录，将跳过表达式验证
			Object value = context.getVariableValue(variable);
			String valueStr = convertToString(value);
			expression = expression.replace(placeholder, valueStr);
		}

		// 评估表达式
		return evaluateSimpleExpression(expression);
	}

	/**
	 * 编译表达式
	 *
	 * @param condition 条件表达式
	 * @return 编译后的表达式
	 */
	private static CompiledExpression compileExpression(String condition) {
		CompiledExpression compiled = new CompiledExpression();
		compiled.template = condition;
		compiled.variables = new ConcurrentHashMap<>();

		Matcher matcher = VARIABLE_PATTERN.matcher(condition);
		int index = 0;
		while (matcher.find()) {
			String variable = matcher.group(1);
			String placeholder = DataRecordConstant.VARIABLE_PLACEHOLDER_PREFIX + (index++) + DataRecordConstant.VARIABLE_PLACEHOLDER_SUFFIX;
			compiled.variables.put(variable, placeholder);
			compiled.template = compiled.template.replace("#" + variable, placeholder);
		}

		return compiled;
	}

	/**
	 * 评估简单表达式
	 *
	 * @param expression 表达式
	 * @return 评估结果
	 */
	private static boolean evaluateSimpleExpression(String expression) {
		expression = expression.trim();

		// 处理 != 操作符（优先级高，避免与 == 冲突）
		if (expression.contains(DataRecordConstant.NOT_EQUALS)) {
			return evaluateEqualityExpression(expression, DataRecordConstant.NOT_EQUALS, true);
		}

		// 处理 == 操作符
		if (expression.contains(DataRecordConstant.EQUALS)) {
			return evaluateEqualityExpression(expression, DataRecordConstant.EQUALS, false);
		}

		// 处理 >= 操作符（优先级高，避免与 > 冲突）
		if (expression.contains(DataRecordConstant.GREATER_THAN_OR_EQUAL)) {
			return evaluateNumericExpression(expression, DataRecordConstant.GREATER_THAN_OR_EQUAL, (left, right) -> left >= right);
		}

		// 处理 <= 操作符（优先级高，避免与 < 冲突）
		if (expression.contains(DataRecordConstant.LESS_THAN_OR_EQUAL)) {
			return evaluateNumericExpression(expression, DataRecordConstant.LESS_THAN_OR_EQUAL, (left, right) -> left <= right);
		}

		// 处理 > 操作符
		if (expression.contains(DataRecordConstant.GREATER_THAN)) {
			return evaluateNumericExpression(expression, DataRecordConstant.GREATER_THAN, (left, right) -> left > right);
		}

		// 处理 < 操作符
		if (expression.contains(DataRecordConstant.LESS_THAN)) {
			return evaluateNumericExpression(expression, DataRecordConstant.LESS_THAN, (left, right) -> left < right);
		}

		// 如果无法解析，默认返回true
		return true;
	}

	/**
	 * 评估等式表达式
	 *
	 * @param expression 表达式
	 * @param operator   操作符
	 * @param negate     是否取反
	 * @return 评估结果
	 */
	private static boolean evaluateEqualityExpression(String expression, String operator, boolean negate) {
		String[] parts = expression.split(operator, 2);
		if (parts.length == 2) {
			Object left = parseValue(parts[0].trim());
			Object right = parseValue(parts[1].trim());
			boolean result = objectEquals(left, right);
			return negate != result;
		}
		return true;
	}

	/**
	 * 评估数值表达式
	 *
	 * @param expression 表达式
	 * @param operator   操作符
	 * @param comparator 比较函数
	 * @return 评估结果
	 */
	private static boolean evaluateNumericExpression(String expression, String operator, NumericComparator comparator) {
		String[] parts = expression.split(operator, 2);
		if (parts.length == 2) {
			Number left = parseNumber(parts[0].trim());
			Number right = parseNumber(parts[1].trim());
			if (left != null && right != null) {
				return comparator.compare(left.doubleValue(), right.doubleValue());
			}
		}
		return true;
	}

	/**
	 * 数值比较函数接口
	 */
	private interface NumericComparator {
		boolean compare(double left, double right);
	}

	/**
	 * 解析值
	 *
	 * @param value 值字符串
	 * @return 解析后的对象
	 */
	private static Object parseValue(String value) {
		if (value == null || DataRecordConstant.NULL_VALUE.equals(value)) {
			return null;
		}

		// 使用 NUMBER_PATTERN 精确匹配数字
		if (NUMBER_PATTERN.matcher(value).matches()) {
			return parseNumber(value);
		}

		// 使用 STRING_PATTERN 匹配被引号包围的字符串
		Matcher stringMatcher = STRING_PATTERN.matcher(value);
		if (stringMatcher.matches()) {
			return stringMatcher.group(1); // 返回引号内的内容
		}

		// 移除引号（兼容旧格式）
		if ((value.startsWith(DataRecordConstant.QUOTE) && value.endsWith(DataRecordConstant.QUOTE)) ||
			(value.startsWith(DataRecordConstant.SINGLE_QUOTE) && value.endsWith(DataRecordConstant.SINGLE_QUOTE))) {
			return value.substring(1, value.length() - 1);
		}

		return value;
	}

	/**
	 * 解析数字
	 *
	 * @param value 值字符串
	 * @return 数字或null
	 */
	private static Number parseNumber(String value) {
		if (value == null) {
			return null;
		}

		try {
			// 使用 NUMBER_PATTERN 验证格式
			if (!NUMBER_PATTERN.matcher(value).matches()) {
				return null;
			}

			if (value.contains(".")) {
				return Double.parseDouble(value);
			} else {
				// 优先使用 Long，如果超出范围则使用 Double
				try {
					return Long.parseLong(value);
				} catch (NumberFormatException e) {
					return Double.parseDouble(value);
				}
			}
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * 对象相等比较
	 *
	 * @param left  左值
	 * @param right 右值
	 * @return 是否相等
	 */
	private static boolean objectEquals(Object left, Object right) {
		if (left == null && right == null) {
			return true;
		}
		if (left == null || right == null) {
			return false;
		}

		// 如果都是数字类型，使用数值比较
		if (left instanceof Number && right instanceof Number) {
			return ((Number) left).doubleValue() == ((Number) right).doubleValue();
		}

		return left.toString().equals(right.toString());
	}

	/**
	 * 转换为字符串
	 *
	 * @param value 值
	 * @return 字符串表示
	 */
	private static String convertToString(Object value) {
		if (value == null) {
			return DataRecordConstant.NULL_VALUE;
		}
		if (value instanceof String) {
			return DataRecordConstant.QUOTE + value + DataRecordConstant.QUOTE;
		}
		return value.toString();
	}

	/**
	 * 编译后的表达式
	 */
	private static class CompiledExpression {
		String template;
		Map<String, String> variables;
	}

	/**
	 * 评估上下文
	 */
	private static class EvaluationContext {
		private final Map<String, Object> variables = new ConcurrentHashMap<>();

		void putVariable(String name, Object value) {
			variables.put(name, value);
		}

		Object getVariableValue(String path) {
			String[] parts = path.split("\\.");
			Object current = variables.get(parts[0]);

			if (current == null || parts.length == 1) {
				return current;
			}

			// 简单的属性访问
			for (int i = 1; i < parts.length; i++) {
				if (current instanceof Map<?, ?> map) {
					String fieldName = parts[i];

					// 优先尝试精确匹配
					current = map.get(fieldName);

					// 如果精确匹配失败，尝试大小写不敏感匹配
					if (current == null) {
						current = getCaseInsensitiveMapValue(map, fieldName);
					}
				} else {
					// 通过反射访问属性
					try {
						String fieldName = parts[i];
						java.lang.reflect.Field field = current.getClass().getDeclaredField(fieldName);
						field.setAccessible(true);
						current = field.get(current);
					} catch (Exception e) {
						return null;
					}
				}
				if (current == null) {
					break;
				}
			}

			return current;
		}

		/**
		 * 大小写不敏感地从Map中获取值
		 *
		 * @param map       Map对象
		 * @param fieldName 字段名
		 * @return 匹配的值，如果没找到返回null
		 */
		private Object getCaseInsensitiveMapValue(Map<?, ?> map, String fieldName) {
			// 首先尝试转换为大写匹配（数据库字段通常是大写）
			Object value = map.get(fieldName.toUpperCase());
			if (value != null) {
				return value;
			}

			// 如果大写匹配失败，遍历所有key进行大小写不敏感匹配
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				Object key = entry.getKey();
				if (key instanceof String && fieldName.equalsIgnoreCase((String) key)) {
					return entry.getValue();
				}
			}

			return null;
		}
	}

	/**
	 * 表达式验证结果
	 */
	@Getter
	public static class ExpressionValidationResult {
		private final boolean valid;
		private final String errorMessage;

		private ExpressionValidationResult(boolean valid, String errorMessage) {
			this.valid = valid;
			this.errorMessage = errorMessage;
		}

		public static ExpressionValidationResult valid() {
			return new ExpressionValidationResult(true, null);
		}

		public static ExpressionValidationResult invalid(String errorMessage) {
			return new ExpressionValidationResult(false, errorMessage);
		}

	}
}
