import request from '@/utils/request'

// 钱包流水列表
export function feeStatisticList(data) {
  return request({
    url: '/promote/record/feeStatisticList',
    method: 'post',
    data: data,
  })
}
// 总计
export function feeStatisticTotal(data) {
  return request({
    url: '/promote/record/feeStatisticTotal',
    method: 'post',
    data: data,
    encrypt:true
  })
}
