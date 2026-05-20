/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springblade.core.mp.encrypt.wrapper;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.springblade.core.mp.encrypt.annotation.SearchableFieldEncrypt;
import org.springblade.core.mp.encrypt.handler.EncryptTypeHandler;
import org.springblade.core.mp.encrypt.utils.EncryptUtil;
import org.springblade.core.mp.encrypt.utils.SlidingUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * 自定义加密查询Wrapper
 * 自动对查询条件进行加密处理
 *
 * @author hubin miemie HCL BladeX
 */
public class EncryptLambdaQueryWrapper<T> extends LambdaQueryWrapper<T> {

	private Class<T> entityClass;

	private SharedString sqlSelect = new SharedString();

	/**
	 * 缓存字段是否需要加密的信息
	 */
	private static final Map<String, Boolean> FIELD_ENCRYPT_CACHE = new ConcurrentHashMap<>();

	public EncryptLambdaQueryWrapper() {
		super();
	}

	public EncryptLambdaQueryWrapper(T entity) {
		super.setEntity(entity);
		super.initNeed();
	}

	public EncryptLambdaQueryWrapper(Class<T> entityClass) {
		super.setEntityClass(entityClass);
		super.initNeed();
	}

	/**
	 * 用于从 EncryptQueryWrapper 转换的构造方法
	 */
	EncryptLambdaQueryWrapper(T entity, Class<T> entityClass, SharedString sqlSelect, AtomicInteger paramNameSeq,
							  Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments, SharedString paramAlias,
							  SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
		super.setEntity(entity);
		super.setEntityClass(entityClass);
		this.paramNameSeq = paramNameSeq;
		this.paramNameValuePairs = paramNameValuePairs;
		this.expression = mergeSegments;
		this.sqlSelect = sqlSelect;
		this.paramAlias = paramAlias;
		this.lastSql = lastSql;
		this.sqlComment = sqlComment;
		this.sqlFirst = sqlFirst;
		this.entityClass = entityClass;
	}

	@Override
	public LambdaQueryWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
		if (entityClass == null) {
			entityClass = getEntityClass();
		} else {
			setEntityClass(entityClass);
		}
		this.sqlSelect.setStringValue(TableInfoHelper.getTableInfo(entityClass).chooseSelect(predicate));
		return typedThis;
	}

	protected LambdaQueryWrapper<T> doSelect(boolean condition, List<SFunction<T, ?>> columns) {
		if (condition && CollectionUtils.isNotEmpty(columns)) {
			this.sqlSelect.setStringValue(columnsToString(false, columns));
		}
		return typedThis;
	}

	@Override
	public String getSqlSelect() {
		return sqlSelect.getStringValue();
	}

	@Override
	public LambdaQueryWrapper<T> eq(boolean condition, SFunction<T, ?> column, Object val) {
		if (condition && val != null) {
			Object encryptedVal = applyConditionalEncryption(column, val);
			return super.eq(true, column, encryptedVal);
		}
		return super.eq(condition, column, val);
	}

	@Override
	public LambdaQueryWrapper<T> ne(boolean condition, SFunction<T, ?> column, Object val) {
		if (condition && val != null) {
			Object encryptedVal = applyConditionalEncryption(column, val);
			return super.ne(true, column, encryptedVal);
		}
		return super.ne(condition, column, val);
	}

	@Override
	public LambdaQueryWrapper<T> in(boolean condition, SFunction<T, ?> column, Object... values) {
		if (condition && values != null && values.length > 0) {
			Object[] encryptedValues = new Object[values.length];
			for (int i = 0; i < values.length; i++) {
				encryptedValues[i] = applyConditionalEncryption(column, values[i]);
			}
			return super.in(true, column, encryptedValues);
		}
		return super.in(condition, column, values);
	}

	/**
	 * 模糊查询（使用滑动窗口加密）
	 * 注意：需要配合特殊的数据库字段存储方式使用
	 */
	@Override
	public LambdaQueryWrapper<T> like(SFunction<T, ?> column, Object val) {
		return like(true, column, val, EncryptUtil.getWindowSize());
	}

	/**
	 * 模糊查询（使用滑动窗口加密）
	 * 注意：需要配合特殊的数据库字段存储方式使用
	 */
	@Override
	public LambdaQueryWrapper<T> like(boolean condition, SFunction<T, ?> column, Object val) {
		return like(condition, column, val, EncryptUtil.getWindowSize());
	}

	/**
	 * 模糊查询（使用滑动窗口加密）
	 *
	 * @param condition  条件
	 * @param column     列
	 * @param val        值
	 * @param windowSize 窗口大小
	 */
	public LambdaQueryWrapper<T> like(boolean condition, SFunction<T, ?> column, Object val, int windowSize) {
		if (condition && val != null && requiresEncryption(column)) {
			String strVal = val.toString();
			String encryptedVal = SlidingUtil.segment(strVal, windowSize);
			return super.like(true, column, encryptedVal);
		}
		return super.like(condition, column, val);
	}

	@Override
	public Class<T> getEntityClass() {
		if (this.entityClass != null) {
			return this.entityClass;
		}
		return super.getEntityClass();
	}

	/**
	 * 用于生成嵌套 sql
	 * <p>故 sqlSelect 不向下传递</p>
	 */
	@Override
	protected EncryptLambdaQueryWrapper<T> instance() {
		return new EncryptLambdaQueryWrapper<>(getEntity(), getEntityClass(), null, paramNameSeq, paramNameValuePairs,
			new MergeSegments(), paramAlias, SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString());
	}

	@Override
	public void clear() {
		super.clear();
		sqlSelect.toNull();
	}

	/**
	 * 对字段值进行条件性加密处理
	 *
	 * @param column 字段函数引用
	 * @param value  待处理的值
	 * @return 加密后的值或原值
	 */
	private Object applyConditionalEncryption(SFunction<T, ?> column, Object value) {
		if (value == null || StringUtil.isBlank(value.toString())) {
			return value;
		}
		Class<T> currentEntityClass = getEntityClassFromColumn(column);
		if (currentEntityClass == null) {
			return value;
		}
		return requiresEncryption(column)
			? EncryptUtil.encrypt(value.toString())
			: value;
	}

	/**
	 * 检查字段是否启用加密功能
	 *
	 * @param column 字段函数引用
	 * @return true 如果字段需要加密，否则返回 false
	 */
	private boolean requiresEncryption(SFunction<T, ?> column) {
		Class<T> currentEntityClass = getEntityClassFromColumn(column);
		if (currentEntityClass == null) {
			return false;
		}
		String fieldName = resolveLambdaFieldName(column);
		if (fieldName == null) {
			return false;
		}

		String cacheKey = buildCacheKey(fieldName);
		return FIELD_ENCRYPT_CACHE.computeIfAbsent(cacheKey, key ->
			detectEncryptionHandler(fieldName) || detectEncryptFieldAnnotation(fieldName));
	}

	/**
	 * 构建缓存键
	 */
	private String buildCacheKey(String fieldName) {
		return getEntityClass().getName() + StringPool.DOT + fieldName;
	}

	/**
	 * 检测字段是否配置了加密处理器
	 */
	private boolean detectEncryptionHandler(String fieldName) {
		try {
			Field field = getEntityClass().getDeclaredField(fieldName);
			TableField tableField = field.getAnnotation(TableField.class);

			return tableField != null
				&& tableField.typeHandler() != null
				&& tableField.typeHandler().getName().contains(EncryptTypeHandler.class.getName());
		} catch (NoSuchFieldException | SecurityException e) {
			// 字段不存在或访问受限，默认不加密
			return false;
		}
	}

	/**
	 * 检测字段是否配置了EncryptField注解且开启了模糊查询
	 *
	 * @param fieldName 字段名称
	 * @return true 如果字段配置了EncryptField注解且开启了模糊查询，否则返回 false
	 */
	private boolean detectEncryptFieldAnnotation(String fieldName) {
		try {
			Field field = getEntityClass().getDeclaredField(fieldName);
			SearchableFieldEncrypt searchableFieldEncrypt = field.getAnnotation(SearchableFieldEncrypt.class);

			return searchableFieldEncrypt != null && searchableFieldEncrypt.enabled();
		} catch (NoSuchFieldException | SecurityException e) {
			// 字段不存在或访问受限，默认不启用模糊查询
			return false;
		}
	}

	/**
	 * 解析Lambda表达式获取字段名称
	 *
	 * @param column Lambda表达式函数引用
	 * @return 字段名称，解析失败返回null
	 */
	private String resolveLambdaFieldName(SFunction<T, ?> column) {
		try {
			Method method = column.getClass().getDeclaredMethod("writeReplace");
			method.setAccessible(true);
			SerializedLambda serializedLambda = (SerializedLambda) method.invoke(column);
			String methodName = serializedLambda.getImplMethodName();
			return extractFieldFromAccessor(methodName);
		} catch (ReflectiveOperationException e) {
			// Lambda表达式解析失败
			return null;
		}
	}

	/**
	 * 从访问器方法名中提取字段名
	 *
	 * @param accessorName getter/setter方法名
	 * @return 字段名
	 */
	private String extractFieldFromAccessor(String accessorName) {
		if (accessorName.startsWith("get") && accessorName.length() > 3) {
			return StringUtil.firstCharToLower(accessorName.substring(3));
		} else if (accessorName.startsWith("is") && accessorName.length() > 2) {
			return StringUtil.firstCharToLower(accessorName.substring(2));
		}
		return accessorName;
	}

	/**
	 * 通过SFunction解析出实体类的Class
	 *
	 * @param column 字段函数引用
	 * @return 实体类Class
	 */
	@SuppressWarnings("unchecked")
	private Class<T> getEntityClassFromColumn(SFunction<T, ?> column) {
		if (this.entityClass != null) {
			return this.entityClass;
		}
		this.entityClass = (Class<T>) LambdaUtils.extract(column).getInstantiatedClass();
		return this.entityClass;
	}
}
