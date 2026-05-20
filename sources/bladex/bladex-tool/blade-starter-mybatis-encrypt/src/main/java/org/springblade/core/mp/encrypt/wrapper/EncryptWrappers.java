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

/**
 * EncryptWrappers
 *
 * @author hubin miemie HCL BladeX
 */
public final class EncryptWrappers {

	/**
	 * 获取 QueryWrapper&lt;T&gt;
	 *
	 * @param <T> 实体类泛型
	 * @return QueryWrapper&lt;T&gt;
	 */
	public static <T> EncryptQueryWrapper<T> query() {
		return new EncryptQueryWrapper<>();
	}

	/**
	 * 获取 QueryWrapper&lt;T&gt;
	 *
	 * @param entity 实体类
	 * @param <T>    实体类泛型
	 * @return QueryWrapper&lt;T&gt;
	 */
	public static <T> EncryptQueryWrapper<T> query(T entity) {
		return new EncryptQueryWrapper<>(entity);
	}

	/**
	 * 获取 QueryWrapper&lt;T&gt;
	 *
	 * @param entityClass 实体类class
	 * @param <T>         实体类泛型
	 * @return QueryWrapper&lt;T&gt;
	 */
	public static <T> EncryptQueryWrapper<T> query(Class<T> entityClass) {
		return new EncryptQueryWrapper<>(entityClass);
	}

	/**
	 * 获取 LambdaQueryWrapper&lt;T&gt;
	 *
	 * @return LambdaQueryWrapper&lt;T&gt;
	 */
	public static <T> EncryptLambdaQueryWrapper<T> lambdaQuery() {
		return new EncryptLambdaQueryWrapper<>();
	}

	/**
	 * 获取 LambdaQueryWrapper&lt;T&gt;
	 *
	 * @param entity 实体类
	 * @param <T>    实体类泛型
	 * @return LambdaQueryWrapper&lt;T&gt;
	 */
	public static <T> EncryptLambdaQueryWrapper<T> lambdaQuery(T entity) {
		return new EncryptLambdaQueryWrapper<>(entity);
	}

	/**
	 * 获取 LambdaQueryWrapper&lt;T&gt;
	 *
	 * @param entityClass 实体类class
	 * @param <T>         实体类泛型
	 * @return LambdaQueryWrapper&lt;T&gt;
	 */
	public static <T> EncryptLambdaQueryWrapper<T> lambdaQuery(Class<T> entityClass) {
		return new EncryptLambdaQueryWrapper<>(entityClass);
	}
}
