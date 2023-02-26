import request from '@/utils/request'

// 钱包流水列表
export function findFeeRatio() {
  return request({
    url: '/promote/record/findFeeRatio',
    method: 'get',
  })
}
