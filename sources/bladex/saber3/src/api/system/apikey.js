import request from '@/axios';

export const getList = (current, size, params) => {
  return request({
    url: '/blade-system/api-key/list',
    method: 'get',
    params: {
      ...params,
      current,
      size,
    },
  });
};

export const getDetail = id => {
  return request({
    url: '/blade-system/api-key/detail',
    method: 'get',
    params: {
      id,
    },
  });
};

export const remove = ids => {
  return request({
    url: '/blade-system/api-key/remove',
    method: 'post',
    params: {
      ids,
    },
  });
};

export const add = row => {
  return request({
    url: '/blade-system/api-key/save',
    method: 'post',
    data: row,
  });
};

export const update = row => {
  return request({
    url: '/blade-system/api-key/update',
    method: 'post',
    data: row,
  });
};

export const submit = row => {
  return request({
    url: '/blade-system/api-key/submit',
    method: 'post',
    data: row,
  });
};

export const enable = ids => {
  return request({
    url: '/blade-system/api-key/enable',
    method: 'post',
    params: {
      ids,
    },
  });
};

export const disable = ids => {
  return request({
    url: '/blade-system/api-key/disable',
    method: 'post',
    params: {
      ids,
    },
  });
};
