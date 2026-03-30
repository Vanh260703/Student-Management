import axiosInstance from './axiosInstance'

export const gradesApi = {
  // Teacher
  getClassGrades: (classId) => axiosInstance.get(`/teacher/classes/${classId}/grades`),
  createGradeComponents: (classId, data) =>
    axiosInstance.post(`/teacher/classes/${classId}/grade-components`, data),
  enterGrade: (enrollmentId, data) =>
    axiosInstance.post(`/teacher/grades/${enrollmentId}`, data),
  updateGrade: (gradeId, data) => axiosInstance.put(`/teacher/grades/${gradeId}`, data),
  publishGrade: (gradeId) => axiosInstance.put(`/teacher/grades/${gradeId}/publish`),

  // Student
  getMyGrades: () => axiosInstance.get('/student/grades'),
  getMyClassGrade: (classId) => axiosInstance.get(`/student/grades/${classId}`),
}
