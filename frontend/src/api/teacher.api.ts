import { axiosPrivate } from './axiosInstance';
import {
  TeacherProfile,
  TeacherDashboardResponse,
  AttendanceResponse,
  GradeComponentResponse,
  ClassGradesResponse,
  StudentGradeResponse,
  UpdateTeacherProfileRequest,
} from '../types/teacher.types';
import { ClassResponse } from '../types/class.types';
import { AttendanceStatus, GradeComponentType } from '../types/common.types';

export const teacherApi = {
  getProfile: async (): Promise<TeacherProfile> => {
    const res = await axiosPrivate.get('/api/v2/teacher/profile');
    return res.data.result;
  },

  updateProfile: async (data: UpdateTeacherProfileRequest): Promise<TeacherProfile> => {
    const res = await axiosPrivate.put('/api/v2/teacher/profile', data);
    return res.data.result;
  },

  getDashboard: async (): Promise<TeacherDashboardResponse> => {
    const res = await axiosPrivate.get('/api/v2/teacher/dashboard');
    return res.data.result;
  },

  getClasses: async (): Promise<ClassResponse[]> => {
    const res = await axiosPrivate.get('/api/v2/teacher/classes');
    return res.data.result;
  },

  updateClassRoom: async (classId: number, room: string): Promise<ClassResponse> => {
    const res = await axiosPrivate.put(`/api/v2/teacher/${classId}`, { room });
    return res.data.result;
  },

  getAttendance: async (
    classId: number,
    date?: string,
  ): Promise<AttendanceResponse> => {
    const res = await axiosPrivate.get(
      `/api/v2/teacher/classes/${classId}/attendance`,
      { params: date ? { date } : undefined },
    );
    return res.data.result;
  },

  saveAttendance: async (
    classId: number,
    data: Array<{ enrollmentId: number; status: AttendanceStatus }>,
    date?: string,
  ): Promise<AttendanceResponse> => {
    const res = await axiosPrivate.post(
      `/api/v2/teacher/classes/${classId}/attendace`,
      data,
      { params: date ? { date } : undefined },
    );
    return res.data.result;
  },

  updateAttendance: async (
    classId: number,
    attendanceId: number,
    data: { enrollmentId: number; status: AttendanceStatus },
    date?: string,
  ): Promise<void> => {
    await axiosPrivate.patch(
      `/api/v2/teacher/classes/${classId}/attendance/${attendanceId}`,
      data,
      { params: date ? { date } : undefined },
    );
  },

  getGradeComponents: async (classId: number): Promise<GradeComponentResponse[]> => {
    const res = await axiosPrivate.get(
      `/api/v2/teacher/classes/${classId}/grade-components`,
    );
    return res.data.result;
  },

  createGradeComponent: async (
    classId: number,
    data: { weight: number; type: GradeComponentType; maxScore: number },
  ): Promise<GradeComponentResponse> => {
    const res = await axiosPrivate.post(
      `/api/v2/teacher/classes/${classId}/grade-components`,
      data,
    );
    return res.data.result;
  },

  updateGradeComponent: async (
    classId: number,
    componentId: number,
    data: { weight: number; maxScore: number },
  ): Promise<GradeComponentResponse> => {
    const res = await axiosPrivate.put(
      `/api/v2/teacher/classes/${classId}/grade-components/${componentId}`,
      data,
    );
    return res.data.result;
  },

  deleteGradeComponent: async (classId: number, componentId: number): Promise<void> => {
    await axiosPrivate.delete(
      `/api/v2/teacher/classes/${classId}/grade-components/${componentId}`,
    );
  },

  getGrades: async (
    classId: number,
    params?: { componentId?: number; isPublished?: boolean; search?: string },
  ): Promise<ClassGradesResponse> => {
    const res = await axiosPrivate.get(
      `/api/v2/teacher/classes/${classId}/grades`,
      { params },
    );
    return res.data.result;
  },

  saveGrade: async (
    classId: number,
    data: { enrollmentId: number; componentId: number; score: number },
  ): Promise<StudentGradeResponse> => {
    const res = await axiosPrivate.post(
      `/api/v2/teacher/classes/${classId}/grades`,
      data,
    );
    return res.data.result;
  },

  updateGrade: async (
    classId: number,
    gradeId: number,
    data: { componentId: number; score: number },
  ): Promise<StudentGradeResponse> => {
    const res = await axiosPrivate.put(
      `/api/v2/teacher/classes/${classId}/grades/${gradeId}`,
      data,
    );
    return res.data.result;
  },

  importGrades: async (classId: number, file: File): Promise<StudentGradeResponse[]> => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await axiosPrivate.post(
      `/api/v2/teacher/classes/${classId}/grades/import`,
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } },
    );
    return res.data.result;
  },

  publishGrades: async (classId: number): Promise<number> => {
    const res = await axiosPrivate.patch(
      `/api/v2/teacher/classes/${classId}/grades/publish`,
    );
    return res.data.result;
  },
};
