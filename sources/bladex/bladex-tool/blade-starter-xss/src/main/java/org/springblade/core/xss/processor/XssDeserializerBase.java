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

package org.springblade.core.xss.processor;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.IOException;

/**
 * jackson xss 处理
 *
 * @author L.cm
 */
public abstract class XssDeserializerBase extends JsonDeserializer<String> {

	@Override
	public String deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
		// json 字段名
		String name = p.currentName();
		// 字符串类型
		if (p.hasToken(JsonToken.VALUE_STRING)) {
			String text = p.getText();
			if (text == null) {
				return null;
			}
			return clean(name, text);
		}
		JsonToken jsonToken = p.getCurrentToken();
		if (jsonToken.isScalarValue()) {
			String text = p.getValueAsString();
			if (text != null) {
				return text;
			}
		}
		throw MismatchedInputException.from(p, String.class, "blade-xss: can't deserialize json name:" + name + " value of type java.lang.String from " + jsonToken);
	}

	/**
	 * 清理 xss
	 *
	 * @param name  json name
	 * @param value json value
	 * @return String
	 */
	public abstract String clean(String name, String value) throws IOException;

}
