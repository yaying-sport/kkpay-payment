import request from '@/utils/request'

// 列表
export function memberTradeList(data) {
  return request({
    url: '/payment/trade/memberTradeList',
    method: 'post',
    encrypt:true,
    data: data
  })
}
// 列表
export function punchMemberAmount(data) {
  return request({
    url: '/payment/trade/punchMemberAmount',
    method: 'post',
    encrypt:true,
    data: data
  })
}
// 总计
export function memberTradeListTotal(data) {
  return request({
    url: '/payment/trade/memberTradeListTotal',
    method: 'post',
    encrypt:true,
    data: data
  })
}
