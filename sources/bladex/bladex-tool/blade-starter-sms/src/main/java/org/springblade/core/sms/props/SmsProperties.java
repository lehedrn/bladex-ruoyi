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
package org.springblade.core.sms.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 云短信配置
 *
 * @author Chill
 */
@Data
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {

	/**
	 * 是否启用
	 */
	private Boolean enabled;

	/**
	 * 短信服务名称
	 */
	private String name;

	/**
	 * 短信模板ID
	 */
	private String templateId;

	/**
	 * appId
	 * <p>
	 * 目前为腾讯云SDKAppID专属字段
	 */
	private String appId;

	/**
	 * regionId
	 */
	private String regionId = "cn-hangzhou";

	/**
	 * accessKey
	 * <p>
	 * 阿里云-AccessKeyID
	 * 腾讯云-SecretId
	 * 七牛云-AccessKey
	 * 云片-ApiKey
	 */
	private String accessKey;

	/**
	 * secretKey
	 * <p>
	 * 阿里云-AccessSecret
	 * 腾讯云-SecretKey
	 * 七牛云-SecretKey
	 */
	private String secretKey;

	/**
	 * 短信签名
	 */
	private String signName;

}
