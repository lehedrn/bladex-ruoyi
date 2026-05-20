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
package org.springblade.core.datarecord.processor;

import org.springblade.core.datarecord.annotation.DataRecord;
import org.springblade.core.datarecord.model.DataRecordInfo;

/**
 * 数据审计处理器接口
 * <p>
 * 用于扩展数据审计的处理逻辑，比如：
 * 1. 保存到审计表
 * 2. 发送到消息队列
 * 3. 调用外部审计服务
 * 4. 发送通知等
 *
 * @author BladeX
 */
public interface DataRecordHandler {

	/**
	 * 处理数据审计
	 *
	 * @param recordInfo 记录信息
	 * @param dataRecord 数据审计配置
	 */
	void handle(DataRecordInfo recordInfo, DataRecord dataRecord);
}
