import axiosInstance from './axiosInstance'

export const dashboardApi = {
  getAdminDashboard: () => axiosInstance.get('/admin/dashboard'),
  getTeacherDashboard: () => axiosInstance.get('/teacher/dashboard'),
  getStudentDashboard: () => axiosInstance.get('/student/dashboard'),
}
