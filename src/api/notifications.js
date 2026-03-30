import axiosInstance from './axiosInstance'

export const notificationsApi = {
  getAll: () => axiosInstance.get('/notifications'),
  getUnread: () => axiosInstance.get('/notifications/unread'),
  getUnreadCount: () => axiosInstance.get('/notifications/unread/count'),
  getById: (id) => axiosInstance.get(`/notifications/${id}`),
  markAsRead: (id) => axiosInstance.put(`/notifications/${id}/mark-as-read`),
  markAllAsRead: () => axiosInstance.put('/notifications/mark-all-as-read'),
  delete: (id) => axiosInstance.delete(`/notifications/${id}`),
}
