import request from '@/axios';

// 获取数据审计表列表
export const getRecordList = (current, size, params = {}) => {
  return request({
    url: '/blade-system/record-data/list',
    method: 'get',
    params: {
      current,
      size,
      ...params,
    },
  });
};

// 获取数据审计表详情
export const getRecordDetail = id => {
  return request({
    url: '/blade-system/record-data/detail',
    method: 'get',
    params: {
      id,
    },
  });
};

// 新增数据审计表记录
export const addRecord = data => {
  return request({
    url: '/blade-system/record-data/save',
    method: 'post',
    data,
  });
};

// 修改数据审计表记录
export const updateRecord = data => {
  return request({
    url: '/blade-system/record-data/update',
    method: 'post',
    data,
  });
};

// 新增或修改数据审计表记录
export const submitRecord = data => {
  return request({
    url: '/blade-system/record-data/submit',
    method: 'post',
    data,
  });
};

// 删除数据审计表记录
export const removeRecord = ids => {
  return request({
    url: '/blade-system/record-data/remove',
    method: 'post',
    params: {
      ids,
    },
  });
}; 