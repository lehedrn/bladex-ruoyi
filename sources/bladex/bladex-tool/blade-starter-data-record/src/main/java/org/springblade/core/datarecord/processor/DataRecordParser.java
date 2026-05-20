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

import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import org.springblade.core.datarecord.annotation.DataRecord;
import org.springblade.core.datarecord.model.DataRecordInfo;

/**
 * 数据审计解析器接口
 * <p>
 * 用于解析数据库操作结果为数据审计记录
 *
 * @author BladeX
 */
public interface DataRecordParser {

	/**
	 * 解析OperationResult为DataRecordInfo
	 *
	 * @param operationResult 操作结果
	 * @param dataRecord      数据审计配置
	 * @return 数据审计信息
	 */
	DataRecordInfo parseOperationResult(DataChangeRecorderInnerInterceptor.OperationResult operationResult, DataRecord dataRecord);
}
