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
package org.springblade.core.datarecord.interceptor;

import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.springblade.core.datarecord.annotation.DataRecord;
import org.springblade.core.datarecord.constant.DataRecordConstant;
import org.springblade.core.datarecord.model.DataRecordInfo;
import org.springblade.core.datarecord.processor.DataRecordDetector;
import org.springblade.core.datarecord.processor.DataRecordHandler;
import org.springblade.core.datarecord.processor.DataRecordParser;
import org.springblade.core.datarecord.expression.DataRecordEvaluator;

import java.sql.Connection;
import java.util.concurrent.CompletableFuture;

/**
 * 数据变更记录插件
 *
 * @author BladeX
 */
@Slf4j
@RequiredArgsConstructor
public class BladeRecorderInterceptor extends DataChangeRecorderInnerInterceptor {

	private final DataRecordDetector dataRecordDetector;

	private final DataRecordParser dataRecordParser;

	private final DataRecordHandler dataRecordHandler;

	@Override
	public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
		// 如果没有配置检测器，则跳过
		if (dataRecordDetector == null) {
			return;
		}

		// 获取表名（这里需要从SQL中解析，暂时简化处理）
		String sql = sh.getBoundSql().getSql();
		String tableName = extractTableName(sql);

		if (tableName == null || dataRecordDetector.skipRecord(tableName)) {
			// 如果表不需要记录，则跳过
			return;
		}

		// 调用父类方法执行数据审计
		super.beforePrepare(sh, connection, transactionTimeout);
	}

	@Override
	protected void dealOperationResult(OperationResult operationResult) {
		if (operationResult == null) {
			return;
		}

		// 检测是否需要记录
		String tableName = operationResult.getTableName();
		if (dataRecordDetector == null || dataRecordDetector.skipRecord(tableName)) {
			return;
		}

		// 获取DataRecord配置
		DataRecord dataRecord = dataRecordDetector.getTableDataRecord(tableName);
		if (dataRecord == null) {
			return;
		}

		// 解析操作结果
		if (dataRecordParser != null) {
			DataRecordInfo recordInfo = dataRecordParser.parseOperationResult(operationResult, dataRecord);
			if (recordInfo != null) {
				// 评估实体级别的条件表达式
				if (DataRecordEvaluator.evaluateEntityCondition(dataRecord.condition(), recordInfo.getOldData(), recordInfo.getNewData())) {
					// 根据配置决定同步还是异步处理
					if (dataRecord.async()) {
						processRecordAsync(recordInfo, dataRecord);
					} else {
						processRecordSync(recordInfo, dataRecord);
					}
				}
			}
		} else {
			// 如果没有解析器，则使用默认处理
			log.debug("数据变更记录: {}", operationResult);
		}
	}

	/**
	 * 同步处理数据审计
	 *
	 * @param recordInfo 记录信息
	 * @param dataRecord 数据审计配置
	 */
	protected void processRecordSync(DataRecordInfo recordInfo, DataRecord dataRecord) {
		try {
			// 可以在这里添加其他处理逻辑，比如：
			// 1. 保存到数据库
			// 2. 发送到消息队列
			// 3. 调用审计服务
			handleDataRecord(recordInfo, dataRecord);

		} catch (Exception e) {
			log.error("processRecordSync 同步处理数据审计失败", e);
		}
	}

	/**
	 * 异步处理数据审计
	 *
	 * @param recordInfo 记录信息
	 * @param dataRecord 数据审计配置
	 */
	protected void processRecordAsync(DataRecordInfo recordInfo, DataRecord dataRecord) {
		CompletableFuture.runAsync(() -> processRecordSync(recordInfo, dataRecord))
			.exceptionally(throwable -> {
				log.error("processRecordAsync 异步处理数据审计失败", throwable);
				return null;
			});
	}

	/**
	 * 处理数据审计（扩展点）
	 * <p>
	 * 子类可以重写此方法来实现自定义的数据审计处理逻辑
	 *
	 * @param recordInfo 记录信息
	 * @param dataRecord 数据审计配置
	 */
	protected void handleDataRecord(DataRecordInfo recordInfo, DataRecord dataRecord) {
		// 调用所有注册的DataRecordHandler
		if (dataRecordHandler != null) {
			dataRecordHandler.handle(recordInfo, dataRecord);
		}
	}

	/**
	 * 从SQL中提取表名（简化实现）
	 *
	 * @param sql SQL语句
	 * @return 表名
	 */
	private String extractTableName(String sql) {
		if (sql == null || sql.trim().isEmpty()) {
			return null;
		}

		String upperSql = sql.toUpperCase().trim();

		// INSERT语句
		if (upperSql.startsWith(DataRecordConstant.OPERATION_INSERT)) {
			int intoIndex = upperSql.indexOf(DataRecordConstant.SQL_INTO);
			if (intoIndex > 0) {
				String afterInto = sql.substring(intoIndex + 4).trim();
				String[] parts = afterInto.split("\\s+");
				if (parts.length > 0) {
					return parts[0].replaceAll(DataRecordConstant.SQL_CHAR_CLEANUP_REGEX, "");
				}
			}
		}
		// UPDATE语句
		else if (upperSql.startsWith(DataRecordConstant.OPERATION_UPDATE)) {
			String[] parts = sql.substring(6).trim().split("\\s+");
			if (parts.length > 0) {
				return parts[0].replaceAll(DataRecordConstant.SQL_CHAR_CLEANUP_REGEX, "");
			}
		}
		// DELETE语句
		else if (upperSql.startsWith(DataRecordConstant.OPERATION_DELETE)) {
			int fromIndex = upperSql.indexOf(DataRecordConstant.SQL_FROM);
			if (fromIndex > 0) {
				String afterFrom = sql.substring(fromIndex + 4).trim();
				String[] parts = afterFrom.split("\\s+");
				if (parts.length > 0) {
					return parts[0].replaceAll(DataRecordConstant.SQL_CHAR_CLEANUP_REGEX, "");
				}
			}
		}

		return null;
	}
}
