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
package org.springblade.core.secure.provider;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * API Key 信息实体
 *
 * @author Chill
 */
@Data
public class ApiKeyInfo implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 租户ID
	 */
	private String tenantId;

	/**
	 * 用户ID
	 */
	private Long userId;

	/**
	 * API Key
	 */
	private String apiKey;

	/**
	 * 访问路径
	 */
	private String apiPath;

	/**
	 * 过期时间
	 */
	private Date expireTime;

	/**
	 * 扩展参数
	 */
	private String extParams;

	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * 账号
	 */
	private String account;

	/**
	 * 用户名
	 */
	private String name;

	/**
	 * 真实姓名
	 */
	private String realName;

	/**
	 * 部门ID
	 */
	private String deptId;

	/**
	 * 岗位ID
	 */
	private String postId;

	/**
	 * 角色ID
	 */
	private String roleId;

}
