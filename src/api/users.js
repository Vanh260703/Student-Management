import axiosInstance from './axiosInstance'

export const usersApi = {
  getAll: (params) => axiosInstance.get('/admin/users', { params }),
  getById: (id) => axiosInstance.get(`/admin/users/${id}`),
  create: (data) => axiosInstance.post('/admin/users', data),
  update: (id, data) => axiosInstance.put(`/admin/users/${id}`, data),
  delete: (id) => axiosInstance.delete(`/admin/users/${id}`),
  block: (id) => axiosInstance.post(`/admin/users/${id}/block`),
  unblock: (id) => axiosInstance.post(`/admin/users/${id}/unblock`),
}
