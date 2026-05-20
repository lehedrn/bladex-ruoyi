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
 * Author: DreamLu (596392912@qq.com)
 */
package org.springblade.core.tool.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.springblade.core.tool.sensitive.SensitiveType;
import org.springblade.core.tool.sensitive.SensitiveUtil;
import org.springblade.core.tool.sensitive.SensitiveWord;
import org.springblade.core.tool.utils.StringUtil;

import java.io.IOException;
import java.util.Arrays;

/**
 * 敏感信息序列化
 *
 * @author BladeX
 */
@JacksonStdImpl
public class SensitiveSerializer extends JsonSerializer<String> implements ContextualSerializer {
	public final static SensitiveSerializer instance = new SensitiveSerializer();

	private Sensitive sensitive;

	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		if (StringUtil.isBlank(value)) {
			gen.writeString(value);
			return;
		}
		if (sensitive == null) {
			gen.writeString(value);
			return;
		}

		// 根据注解配置处理脱敏
		if (sensitive.type() != SensitiveType.NONE) {
			// 类型脱敏
			gen.writeString(SensitiveUtil.process(value, sensitive.type()));
		} else if (sensitive.word() != SensitiveWord.NONE) {
			// 敏感词脱敏
			gen.writeString(SensitiveUtil.processWithWords(value, sensitive.word().getWords(), sensitive.replacement(), true));
		} else if (sensitive.words().length > 0) {
			// 自定义敏感词脱敏
			gen.writeString(SensitiveUtil.processWithWords(value, Arrays.asList(sensitive.words()), sensitive.replacement(), true));
		} else if (StringUtil.isNotBlank(sensitive.regex())) {
			// 正则脱敏
			gen.writeString(SensitiveUtil.processWithRegex(value, sensitive.regex(), sensitive.replacement()));
		} else {
			// 默认值
			gen.writeString(value);
		}
	}


	@Override
	public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
		JsonSerializer<?> valueSerializer = prov.findValueSerializer(String.class);
		if (property == null) {
			return valueSerializer;
		}
		Sensitive sensitive = property.getAnnotation(Sensitive.class);
		if (sensitive != null) {
			SensitiveSerializer serializer = new SensitiveSerializer();
			serializer.sensitive = sensitive;
			return serializer;
		}
		return valueSerializer;
	}
}
