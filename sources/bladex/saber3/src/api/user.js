import request from '@/axios';

import website from '@/config/website';
import func from '@/utils/func';

export const loginByUsername = (tenantId, deptId, roleId, username, password, type, key, code) =>
  request({
    url: '/blade-auth/oauth/token',
    method: 'post',
    headers: {
      'Tenant-Id': tenantId,
      'Dept-Id': func.toStr(deptId),
      'Role-Id': func.toStr(roleId),
      'Captcha-Key': key,
      'Captcha-Code': code,
    },
    params: {
      tenantId,
      username,
      password,
      grant_type: website.captchaMode ? 'captcha' : 'password',
      scope: 'all',
      type,
    },
  });

export const loginBySocial = (tenantId, source, code, state) =>
  request({
    url: '/blade-auth/oauth/token',
    method: 'post',
    headers: {
      'Tenant-Id': tenantId,
    },
    params: {
      tenantId,
      source,
      code,
      state,
      grant_type: 'social',
      scope: 'all',
    },
  });

export const loginBySso = (state, code) =>
  request({
    url: '/blade-auth/oauth/token',
    method: 'post',
    headers: {
      'Tenant-Id': state,
    },
    params: {
      tenantId: state,
      code,
      grant_type: 'authorization_code',
      scope: 'all',
      redirect_uri: website.oauth2.redirectUri,
    },
  });

export const loginByPhone = (tenantId, phone, id, value) =>
  request({
    url: '/blade-auth/oauth/token',
    method: 'post',
    headers: {
      'Tenant-Id': tenantId,
    },
    params: {
      tenantId,
      phone,
      id,
      value,
      grant_type: 'sms_code',
      scope: 'all',
    },
  });

export const refreshToken = (refresh_token, tenantId, deptId, roleId) =>
  request({
    url: '/blade-auth/oauth/token',
    method: 'post',
    headers: {
      'Tenant-Id': tenantId,
      'Dept-Id': func.toStr(deptId),
      'Role-Id': func.toStr(roleId),
    },
    params: {
      tenantId,
      refresh_token,
      grant_type: 'refresh_token',
      scope: 'all',
    },
  });

export const registerUser = (tenantId, name, account, password, phone, email) =>
  request({
    url: '/blade-auth/oauth/token',
    method: 'post',
    headers: {
      'Tenant-Id': tenantId,
    },
    params: {
      name,
      username: account,
      account,
      password,
      phone,
      email,
      grant_type: 'register',
      scope: 'all',
    },
  });

export const registerGuest = (form, oauthId) =>
  request({
    url: '/blade-system/user/register-guest',
    method: 'post',
    params: {
      tenantId: form.tenantId,
      name: form.name,
      account: form.account,
      password: form.password,
      oauthId,
    },
  });

export const getButtons = () =>
  request({
    url: '/blade-system/menu/buttons',
    method: 'get',
  });

export const getCaptcha = () =>
  request({
    url: '/blade-auth/oauth/captcha',
    method: 'get',
    authorization: false,
  });

export const logout = () =>
  request({
    url: '/blade-auth/oauth/logout',
    method: 'get',
    authorization: false,
  });

export const getUserInfo = () =>
  request({
    url: '/blade-auth/oauth/user-info',
    method: 'get',
  });

export const sendLogs = list =>
  request({
    url: '/blade-auth/oauth/logout',
    method: 'post',
    data: list,
  });

export const clearCache = () =>
  request({
    url: '/blade-auth/oauth/clear-cache',
    method: 'get',
    authorization: false,
  });

export const sendSms = (tenantId, phone) =>
  request({
    url: '/blade-auth/oauth/sms/send-validate',
    method: 'post',
    params: {
      tenantId,
      phone,
    },
  });
