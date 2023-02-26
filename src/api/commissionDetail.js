import request from '@/utils/request'

// 列表
export function recordList(data) {
  return request({
    url: '/promote/record/list',
    method: 'post',
    encrypt:true,
    data: data
  })
}
// 总计
export function commissionTotal(data) {
  return request({
    url: '/promote/record/commissionTotal',
    method: 'post',
    encrypt:true,
    data: data
  })
}
