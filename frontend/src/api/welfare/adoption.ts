import request from '@/utils/request'

export function listAdoptionApplications(query) {
  return request({
    url: '/welfare/adoption/list',
    method: 'get',
    params: query
  })
}

export function getAdoptionApplication(id) {
  return request({
    url: `/welfare/adoption/${id}`,
    method: 'get'
  })
}

export function getAdoptionStats() {
  return request({
    url: '/welfare/adoption/stats',
    method: 'get'
  })
}

export function reviewAdoptionApplication(id, data) {
  return request({
    url: `/welfare/adoption/${id}/review`,
    method: 'put',
    data
  })
}
