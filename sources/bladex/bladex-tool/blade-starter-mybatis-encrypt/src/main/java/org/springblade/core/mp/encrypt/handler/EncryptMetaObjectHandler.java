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
package org.springblade.core.mp.encrypt.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springblade.core.mp.encrypt.annotation.SearchableFieldEncrypt;
import org.springblade.core.mp.encrypt.utils.SlidingUtil;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

/**
 * 加密搜索字段自定义填充
 *
 * @author Chill
 */
@Slf4j
@Component
public class EncryptMetaObjectHandler implements MetaObjectHandler {

	@Override
	public void insertFill(MetaObject metaObject) {
		processEncryptFields(metaObject);
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		processEncryptFields(metaObject);
	}

	/**
	 * 处理带有SearchableEncryptField注解且开启模糊查询的字段
	 *
	 * @param metaObject 元对象
	 */
	private void processEncryptFields(MetaObject metaObject) {
		if (metaObject == null || metaObject.getOriginalObject() == null) {
			return;
		}
		Field[] declaredFields = metaObject.getOriginalObject().getClass().getDeclaredFields();
		Arrays.stream(declaredFields).forEach(field -> processField(field, metaObject));
	}

	/**
	 * 处理单个字段
	 *
	 * @param field      字段
	 * @param metaObject 元对象
	 */
	private void processField(Field field, MetaObject metaObject) {
		Optional.ofNullable(field.getAnnotation(SearchableFieldEncrypt.class))
			.filter(SearchableFieldEncrypt::enabled)
			.ifPresent(annotation -> processSearchableField(field, annotation, metaObject));
	}

	/**
	 * 处理可搜索的加密字段
	 *
	 * @param field      字段
	 * @param annotation 注解
	 * @param metaObject 元对象
	 */
	private void processSearchableField(Field field, SearchableFieldEncrypt annotation, MetaObject metaObject) {
		String sourceFieldName = StringUtil.replace(field.getName(), annotation.encName(), StringPool.EMPTY);

		Optional.ofNullable(this.getFieldValByName(sourceFieldName, metaObject))
			.filter(String.class::isInstance)
			.map(String.class::cast)
			.map(SlidingUtil::segment)
			.ifPresent(segmentedValue -> this.setFieldValByName(field.getName(), segmentedValue, metaObject));
	}

}
