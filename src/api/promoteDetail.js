import request from '@/utils/request'

// 列表
export function agentList(data) {
  return request({
    url: '/promote/agent/list',
    method: 'post',
    encrypt:true,
    data: data
  })
}
// 详情
export function agentDetail(data) {
  return request({
    url: '/promote/agent/detail',
    method: 'post',
    encrypt:true,
    data: data
  })
}
