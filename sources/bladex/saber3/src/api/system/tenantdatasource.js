import request from '@/axios';

export const getList = (current, size, params) => {
  return request({
    url: '/blade-system/tenant-datasource/list',
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
    url: '/blade-system/tenant-datasource/detail',
    method: 'get',
    params: {
      id,
    },
  });
};

export const remove = ids => {
  return request({
    url: '/blade-system/tenant-datasource/remove',
    method: 'post',
    params: {
      ids,
    },
  });
};

export const add = row => {
  return request({
    url: '/blade-system/tenant-datasource/submit',
    method: 'post',
    data: row,
  });
};

export const update = row => {
  return request({
    url: '/blade-system/tenant-datasource/submit',
    method: 'post',
    data: row,
  });
};
