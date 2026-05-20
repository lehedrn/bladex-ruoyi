import request from '@/axios';

export const getList = (current, size, params) => {
  return request({
    url: '/blade-develop/code-setting/list',
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
    url: '/blade-develop/code-setting/detail',
    method: 'get',
    params: {
      id,
    },
  });
};

export const remove = ids => {
  return request({
    url: '/blade-develop/code-setting/remove',
    method: 'post',
    params: {
      ids,
    },
  });
};

export const add = row => {
  return request({
    url: '/blade-develop/code-setting/submit',
    method: 'post',
    data: row,
  });
};

export const update = row => {
  return request({
    url: '/blade-develop/code-setting/submit',
    method: 'post',
    data: row,
  });
};

export const enable = id => {
  return request({
    url: '/blade-develop/code-setting/enable',
    method: 'post',
    params: {
      id,
    },
  });
};

export const getEnableDetail = () => {
  return request({
    url: '/blade-develop/code-setting/enable-detail',
    method: 'get',
    params: {},
  });
};

export const getTableForm = tableName => {
  return request({
    url: '/blade-develop/code-setting/table-form',
    method: 'get',
    params: {
      tableName,
    },
  });
};

export const getTablePrototype = (tableName, datasourceId) => {
  return request({
    url: '/blade-develop/code-setting/table-prototype',
    method: 'get',
    params: {
      tableName,
      datasourceId,
    },
  });
};
