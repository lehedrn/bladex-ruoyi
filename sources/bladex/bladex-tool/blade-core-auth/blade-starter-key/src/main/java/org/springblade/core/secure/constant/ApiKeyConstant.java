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
package org.springblade.core.secure.constant;

/**
 * API Key 常量
 *
 * @author Chill
 */
public interface ApiKeyConstant {

	/**
	 * API Key 前缀
	 */
	String API_KEY_PREFIX = "ak-";

	/**
	 * 启用状态
	 */
	int STATUS_ACTIVE = 1;

	/**
	 * 缓存名称
	 */
	String API_KEY_CACHE = "blade:apikey";

	/**
	 * 用户缓存前缀
	 */
	String CACHE_USER_PREFIX = "apikey:user:";

	/**
	 * 路径缓存前缀
	 */
	String CACHE_PATH_PREFIX = "apikey:path:";

	/**
	 * 全路径匹配
	 */
	String FULL_PATH = "/**";

	/**
	 * 查询 API Key 信息的 SQL 语句
	 */
	String API_KEY_SELECT_STATEMENT = """
		SELECT a.id, a.tenant_id, a.user_id, a.api_key, a.api_path, a.expire_time, a.ext_params, a.status,
			u.account, u.name, u.real_name, u.dept_id, u.post_id, u.role_id
		FROM blade_api_key a LEFT JOIN blade_user u ON a.user_id = u.id WHERE a.api_key = ?
		""";

}
