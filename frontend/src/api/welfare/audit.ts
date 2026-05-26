import request from '@/utils/request'

export function listAuditPhotos(query) {
  return request({
    url: '/welfare/audit/photos/list',
    method: 'get',
    params: query
  })
}

export function reviewAuditPhoto(id, data) {
  return request({
    url: `/welfare/audit/photos/${id}/review`,
    method: 'put',
    data
  })
}
