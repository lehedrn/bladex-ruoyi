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
package org.springblade.core.sms;

import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.sms.model.SmsCode;
import org.springblade.core.sms.model.SmsData;
import org.springblade.core.sms.model.SmsResponse;
import org.springblade.core.sms.props.SmsProperties;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.http.HttpStatus;

import java.time.Duration;
import java.util.Collection;

/**
 * 腾讯云短信发送类
 *
 * @author Chill
 */
@Slf4j
@AllArgsConstructor
public class TencentSmsTemplate implements SmsTemplate {

	private static final String SUCCESS_MESSAGE = "send success";
	private static final String OK = "ok";

	private final SmsProperties smsProperties;
	private final SmsClient smsClient;
	private final BladeRedis bladeRedis;


	@Override
	public SmsResponse sendMessage(SmsData smsData, Collection<String> phones) {
		try {
			Collection<String> values = smsData.getParams().values();
			String[] params = StringUtil.toStringArray(values);

			String[] phoneNumbers = StringUtil.toStringArray(phones);

			SendSmsRequest req = new SendSmsRequest();
			req.setSmsSdkAppId(smsProperties.getAppId());
			req.setSignName(smsProperties.getSignName());
			req.setTemplateId(smsProperties.getTemplateId());
			req.setTemplateParamSet(params);
			req.setPhoneNumberSet(phoneNumbers);

			SendSmsResponse resp = smsClient.SendSms(req);
			SendStatus[] statuses = resp.getSendStatusSet();
			if (statuses == null || statuses.length == 0) {
				return new SmsResponse(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR.value(), "短信发送失败");
			} else {
				SendStatus status = statuses[0];
				String code = status.getCode();
				String message = status.getMessage();
				if (message.equalsIgnoreCase(SUCCESS_MESSAGE) && code.equalsIgnoreCase(OK)) {
					return new SmsResponse(Boolean.TRUE, HttpStatus.OK.value(), status.toString());
				} else {
					log.warn("短信发送失败，phones:{}，code:{}, response:{}", phoneNumbers, code, message);
					return new SmsResponse(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
				}
			}
		} catch (TencentCloudSDKException e) {
			e.printStackTrace();
			return new SmsResponse(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
		}
	}

	@Override
	public SmsCode sendValidate(SmsData smsData, String phone) {
		SmsCode smsCode = new SmsCode();
		boolean temp = sendSingle(smsData, phone);
		if (temp && StringUtil.isNotBlank(smsData.getKey())) {
			String id = StringUtil.randomUUID();
			String value = smsData.getParams().get(smsData.getKey());
			bladeRedis.setEx(cacheKey(phone, id), value, Duration.ofMinutes(30));
			smsCode.setId(id).setValue(value);
		} else {
			smsCode.setSuccess(Boolean.FALSE);
		}
		return smsCode;
	}

	@Override
	public boolean validateMessage(SmsCode smsCode) {
		String id = smsCode.getId();
		String value = smsCode.getValue();
		String phone = smsCode.getPhone();
		String cache = bladeRedis.get(cacheKey(phone, id));
		if (StringUtil.isNotBlank(value) && StringUtil.equalsIgnoreCase(cache, value)) {
			bladeRedis.del(cacheKey(phone, id));
			return true;
		}
		return false;
	}

}
