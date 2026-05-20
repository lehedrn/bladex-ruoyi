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
import org.springblade.core.tool.utils.Func;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * 权限菜单类
 *
 * @author Chill
 */
@Data
public class PermissionMenu implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	private Long id;

	/**
	 * 菜单父主键
	 */
	private Long parentId;

	/**
	 * 菜单编号
	 */
	private String code;

	/**
	 * 菜单名称
	 */
	private String name;

	/**
	 * 菜单别名
	 */
	private String alias;

	/**
	 * 请求地址
	 */
	private String path;

	/**
	 * 菜单资源
	 */
	private String source;

	/**
	 * 组件资源
	 */
	private String component;

	/**
	 * 排序
	 */
	private Integer sort;

	/**
	 * 菜单类型
	 */
	private Integer category;

	/**
	 * 操作按钮类型
	 */
	private Integer action;

	/**
	 * 是否打开新页面
	 */
	private Integer isOpen;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 是否已删除
	 */
	private Integer isDeleted;


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		PermissionMenu other = (PermissionMenu) obj;
		return Func.equals(this.getId(), other.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, parentId, code);
	}


}
