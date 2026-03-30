import axiosInstance from './axiosInstance'

export const classesApi = {
  getAll: (params) => axiosInstance.get('/classes', { params }),
  getById: (classId) => axiosInstance.get(`/classes/${classId}`),
  create: (data) => axiosInstance.post('/classes', data),
  update: (classId, data) => axiosInstance.put(`/classes/${classId}`, data),
  changeStatus: (classId, data) => axiosInstance.patch(`/classes/${classId}/change-status`, data),
  delete: (classId) => axiosInstance.delete(`/classes/${classId}`),
}

export const enrollmentApi = {
  getAvailable: (params) => axiosInstance.get('/enrollments/available-classes', { params }),
  enroll: (classId) => axiosInstance.post(`/enrollments/${classId}`),
  unenroll: (enrollmentId) => axiosInstance.delete(`/enrollments/${enrollmentId}`),
  getMy: (params) => axiosInstance.get('/enrollments/my', { params }),
}
