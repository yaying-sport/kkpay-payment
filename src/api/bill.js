import request from '@/utils/request'

// 列表
export function billList(data) {
  return request({
    url: '/payment/bill/list',
    method: 'post',
    encrypt:true,
    data: data
  })
}
// 总计
export function billTotal(data) {
  return request({
    url: '/payment/bill/billTotal',
    method: 'post',
    encrypt:true,
    data: data
  })
}
