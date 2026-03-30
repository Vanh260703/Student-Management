import axiosInstance from './axiosInstance'

export const authApi = {
  login: (data) => axiosInstance.post('/auth/login', data),
  refreshToken: (refreshToken) => axiosInstance.post('/auth/refresh-token', { refreshToken }),
  forgotPassword: (email) => axiosInstance.post('/auth/forgot-password', { email }),
  resetPassword: (data) => axiosInstance.post('/auth/reset-password', data),
  changePassword: (data) => axiosInstance.post('/auth/change-password', data),
}
