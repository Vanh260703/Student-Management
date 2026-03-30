import axiosInstance from './axiosInstance'

export const attendanceApi = {
  getByDate: (classId, date) =>
    axiosInstance.get(`/teacher/classes/${classId}/attendance`, { params: { date } }),
  markAttendance: (classId, date, data) =>
    axiosInstance.post(`/teacher/classes/${classId}/attendance`, data, { params: { date } }),
  updateAttendance: (classId, attendanceId, date, data) =>
    axiosInstance.patch(
      `/teacher/classes/${classId}/attendance/${attendanceId}`,
      data,
      { params: { date } }
    ),
}
