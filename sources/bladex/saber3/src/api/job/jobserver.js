import request from '@/axios';

export const getList = (current, size, params) => {
  return request({
    url: '/blade-job/job-server/list',
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
    url: '/blade-job/job-server/detail',
    method: 'get',
    params: {
      id,
    },
  });
};

export const remove = ids => {
  return request({
    url: '/blade-job/job-server/remove',
    method: 'post',
    params: {
      ids,
    },
  });
};

export const add = row => {
  return request({
    url: '/blade-job/job-server/submit',
    method: 'post',
    data: row,
  });
};

export const update = row => {
  return request({
    url: '/blade-job/job-server/submit',
    method: 'post',
    data: row,
  });
};

export const sync = row => {
  return request({
    url: '/blade-job/job-server/sync',
    method: 'post',
    data: row,
  });
};
