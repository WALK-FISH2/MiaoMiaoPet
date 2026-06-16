import request from '@/utils/request'

export function listPets(query) {
  return request({
    url: '/welfare/pet/list',
    method: 'get',
    params: query
  })
}

export function getPet(id) {
  return request({
    url: `/welfare/pet/${id}`,
    method: 'get'
  })
}

export function addPet(data) {
  return request({
    url: '/welfare/pet',
    method: 'post',
    data
  })
}

export function updatePet(data) {
  return request({
    url: '/welfare/pet',
    method: 'put',
    data
  })
}

export function deletePet(id) {
  return request({
    url: `/welfare/pet/${id}`,
    method: 'delete'
  })
}

export function listPetLocations() {
  return request({
    url: '/welfare/pet/locations',
    method: 'get'
  })
}

export function uploadPetImage(file) {
  const data = new FormData()
  data.append('file', file)
  return request({
    url: '/common/upload',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data', repeatSubmit: false },
    data
  })
}
