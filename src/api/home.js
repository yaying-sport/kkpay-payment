import request from '@/utils/request'

// 仪表盘查询商户信息
export function paymentDashBoardTotal(data) {
  return request({
    url: '/payment/center/paymentDashBoardTotal', 
    method: 'post',
    data: data,
    encrypt:true
  })
}

//旗下会员
export function searchPaymentMember(data) {
  return request({
    url: '/payment/center/searchPaymentMember',
    method: 'post',
    data: data,
    encrypt:true
  })
}
