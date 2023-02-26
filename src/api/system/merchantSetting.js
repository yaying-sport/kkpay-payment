import request from '@/utils/request'

// 用户头像上传
export function uploadAvatar(data) {
  return request({
    url: '/agent/center/uploadAvatar',
    method: 'post',
    data: data
  })
}
// 保存头像
export function saveLogo(data) {
  return request({
    url: '/agent/center/saveLogo',
    method: 'post',
    encrypt:true,
    data: data
  })
}
// 保存网址
export function saveWebsite(data) {
  return request({
    url: '/agent/center/saveWebsite',
    method: 'post',
    encrypt:true,
    data: data
  })
}
// 查询参数列表
export function getValue(query) {
  return request({
    url: '/system/config/getValue',
    method: 'get',
    encrypt:true,
    params: query
  })
}
// 设置开关
export function setValue(data) {
  return request({
    url: '/system/config/setValue',
    method: 'post',
    encrypt:true,
    data: data
  })
}