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
package org.springblade.core.mp.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

/**
 * 基础业务接口
 *
 * @param <T>
 * @author Chill
 */
public interface BaseService<T> extends IService<T> {

	/**
	 * 根据条件查询单条记录
	 *
	 * @param queryWrapper 查询条件
	 * @return 查询结果实体，无结果时返回null
	 */
	T queryOne(QueryWrapper<T> queryWrapper);

	/**
	 * 根据实体对象查询详情
	 *
	 * @param entity 查询实体对象，包含查询条件
	 * @return 查询结果实体，无结果时返回null
	 */
	T queryDetail(T entity);

	/**
	 * 根据Map条件查询详情
	 *
	 * @param entity 查询条件Map，key为字段名，value为字段值
	 * @return 查询结果实体，无结果时返回null
	 */
	T queryDetail(Map<String, Object> entity);

	/**
	 * 批量逻辑删除
	 *
	 * @param ids id集合（不能为空）
	 * @return 删除是否成功
	 */
	boolean deleteLogic(@NotEmpty List<Long> ids);

	/**
	 * 批量变更状态
	 *
	 * @param ids    id集合（不能为空）
	 * @param status 状态值
	 * @return 变更是否成功
	 */
	boolean changeStatus(@NotEmpty List<Long> ids, Integer status);

	/**
	 * 判断字段值是否重复
	 *
	 * @param field 字段的Lambda表达式
	 * @param value 字段值
	 * @return true-存在重复，false-不存在重复
	 */
	boolean isFieldDuplicate(SFunction<T, ?> field, Object value);

	/**
	 * 判断字段值是否重复（排除指定ID）
	 *
	 * @param field      字段的Lambda表达式
	 * @param value      字段值
	 * @param excludedId 排除的记录ID（通常用于编辑时排除自身）
	 * @return true-存在重复，false-不存在重复
	 */
	boolean isFieldDuplicate(SFunction<T, ?> field, Object value, Long excludedId);

}
