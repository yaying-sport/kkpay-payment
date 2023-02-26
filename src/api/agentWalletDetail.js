import request from '@/utils/request'

// 列表
export function agentBillDetailList(data) {
  return request({
    url: '/promote/record/agentBillDetailList',
    method: 'post',
    // encrypt:true,
    data: data
  })
}
// 总计
export function agentBillProxyTotal(data) {
  return request({
    url: '/promote/record/agentBillProxyTotal',
    method: 'post',
    encrypt:true,
    data: data
  })
}
