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
package org.springblade.core.mp.encrypt.support;

import org.springblade.core.launch.constant.TokenConstant;
import org.springblade.core.mp.encrypt.wrapper.EncryptQueryWrapper;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.SqlKeyword;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ClassUtil;
import org.springblade.core.tool.utils.StringPool;

import java.util.Map;
import java.util.Set;

/**
 * 加密分页工具
 *
 * @author Chill
 */
public class EncryptCondition extends Condition {

	/**
	 * 获取mybatis plus中的QueryWrapper
	 *
	 * @param entity 实体
	 * @param <T>    类型
	 * @return QueryWrapper
	 */
	public static <T> EncryptQueryWrapper<T> getEncryptQueryWrapper(T entity) {
		return new EncryptQueryWrapper<>(entity);
	}

	/**
	 * 获取mybatis plus中的QueryWrapper
	 *
	 * @param query 查询条件
	 * @param clazz 实体类
	 * @param <T>   类型
	 * @return QueryWrapper
	 */
	public static <T> EncryptQueryWrapper<T> getEncryptQueryWrapper(Map<String, Object> query, Class<T> clazz) {
		Kv exclude = Kv.create().set(TokenConstant.AUTH_HEADER, TokenConstant.AUTH_HEADER)
			.set("current", "current").set("size", "size").set("ascs", "ascs").set("descs", "descs");
		return getEncryptQueryWrapper(query, exclude, clazz);
	}

	/**
	 * 获取mybatis plus中的QueryWrapper
	 *
	 * @param query   查询条件
	 * @param exclude 排除的查询条件
	 * @param clazz   实体类
	 * @param <T>     类型
	 * @return QueryWrapper
	 */
	public static <T> EncryptQueryWrapper<T> getEncryptQueryWrapper(Map<String, Object> query, Map<String, Object> exclude, Class<T> clazz) {
		// 移除 query 中在 exclude 里有的字段
		exclude.forEach((k, v) -> query.remove(k));
		// 获取 clazz 的所有字段名，构造成 Set
		Set<String> fieldNames = ClassUtil.getClassFieldNames(clazz);
		// 移除 query 中不属于 clazz 字段的键
		query.keySet().removeIf(key -> !fieldNames.contains(key.split(StringPool.UNDERSCORE)[0]));
		// 构造 QueryWrapper
		EncryptQueryWrapper<T> qw = new EncryptQueryWrapper<>();
		qw.setEntity(BeanUtil.newInstance(clazz));
		// 使用 SQL 关键字条件构建
		SqlKeyword.buildCondition(query, qw);
		// 返回构造类
		return qw;
	}

}
