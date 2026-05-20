package org.springblade.core.mp.support;

import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;

import java.util.regex.Pattern;

/**
 * SQLServerDialect
 */
public class SQLServerOrderDialect implements IDialect {

	@Override
	public DialectModel buildPaginationSql(String originalSql, long offset, long limit) {
		String sqlWithOrderBy = appendOrderBy(originalSql);
		String sql = sqlWithOrderBy + " OFFSET " + FIRST_MARK + " ROWS FETCH NEXT " + SECOND_MARK + " ROWS ONLY";
		return new DialectModel(sql, offset, limit).setConsumerChain();
	}

	private static String appendOrderBy(String sql) {
		final Pattern p = Pattern.compile(".*\\s+order\\s+by\\s+.*", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
		if (p.matcher(sql).find()) {
			return sql;
		} else {
			return sql + " ORDER BY CURRENT_TIMESTAMP";
		}
	}
}
