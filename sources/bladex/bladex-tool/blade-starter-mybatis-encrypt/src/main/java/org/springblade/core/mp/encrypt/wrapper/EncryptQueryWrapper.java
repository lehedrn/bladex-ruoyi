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

import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义加密查询Wrapper
 * 主要用于提供lambda()方法转换到EncryptLambdaQueryWrapper
 *
 * @author hubin miemie HCL BladeX
 */
public class EncryptQueryWrapper<T> extends QueryWrapper<T> {

	public EncryptQueryWrapper() {
		super();
	}

	public EncryptQueryWrapper(T entity) {
		super.setEntity(entity);
		super.initNeed();
	}

	public EncryptQueryWrapper(Class<T> entityClass) {
		super.setEntityClass(entityClass);
		super.initNeed();
	}

	public EncryptQueryWrapper(T entity, String... columns) {
		super.setEntity(entity);
		super.initNeed();
		this.select(columns);
	}

	/**
	 * 非对外公开的构造方法,只用于生产嵌套 sql
	 */
	private EncryptQueryWrapper(T entity, Class<T> entityClass, AtomicInteger paramNameSeq,
								Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments, SharedString paramAlias,
								SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
		super.setEntity(entity);
		super.setEntityClass(entityClass);
		this.paramNameSeq = paramNameSeq;
		this.paramNameValuePairs = paramNameValuePairs;
		this.expression = mergeSegments;
		this.paramAlias = paramAlias;
		this.lastSql = lastSql;
		this.sqlComment = sqlComment;
		this.sqlFirst = sqlFirst;
	}

	/**
	 * 返回一个支持 lambda 函数写法的加密 wrapper
	 */
	@Override
	public EncryptLambdaQueryWrapper<T> lambda() {
		return new EncryptLambdaQueryWrapper<>(getEntity(), getEntityClass(), sqlSelect, paramNameSeq, paramNameValuePairs,
			expression, paramAlias, lastSql, sqlComment, sqlFirst);
	}

	/**
	 * 用于生成嵌套 sql
	 * <p>
	 * 故 sqlSelect 不向下传递
	 * </p>
	 */
	@Override
	protected EncryptQueryWrapper<T> instance() {
		return new EncryptQueryWrapper<>(getEntity(), getEntityClass(), paramNameSeq, paramNameValuePairs, new MergeSegments(),
			paramAlias, SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString());
	}
}
