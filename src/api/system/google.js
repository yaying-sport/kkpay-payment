import request from '@/utils/request'

// 重置谷歌验证码
export function resetGoogleCode(data) {
  return request({
    url: '/system/user/resetGoogleCode',
    method: 'post',
    encrypt:true,
    data: data
  })
}
// 下载谷歌验证码
export function getCurrentUserGoogleAuthKey(data) {
  return request({
    url: '/system/googleAuth/getCurrentUserGoogleAuthKey',
    method: 'get',
  })
}
// 刷新谷歌验证码
export function getGoogleAuthKey(data) {
  return request({
    url: '/system/googleAuth/getGoogleAuthKey',
    method: 'get',
  })
}
// 获取谷歌验证码code
export function getGoogleCode(query) {
  return request({
    url: '/system/googleAuth/getCode',
    method: 'get',
    params: query
  })
}
// 绑定谷歌验证码key
export function saveUserGoogleKey(data) {
  return request({
    url: '/system/googleAuth/saveUserGoogleKey',
    method: 'post',
    encrypt:true,
    data: data
  })
}
// 修改登录密码
export function editLoginPwd(data) {
  return request({
    url: '/payment/center/editLoginPwd',
    method: 'post',
    encrypt:true,
    data: data
  })
}
// 修改支付密码
export function editPayPwd(data) {
  return request({
    url: '/payment/center/editPayPwd',
    method: 'post',
    encrypt:true,
    data: data
  })
}
