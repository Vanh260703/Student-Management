import axiosInstance from './axiosInstance'

export const profileApi = {
  // Student
  getStudentProfile: () => axiosInstance.get('/student/profile'),
  updateStudentProfile: (data) => axiosInstance.put('/student/profile', data),
  uploadStudentAvatar: (formData) =>
    axiosInstance.post('/student/profile/upload-avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    }),

  // Teacher
  getTeacherProfile: () => axiosInstance.get('/teacher/profile'),
  updateTeacherProfile: (data) => axiosInstance.put('/teacher/profile', data),

  // Teacher classes list
  getTeacherClasses: () => axiosInstance.get('/teacher/classes'),
}
