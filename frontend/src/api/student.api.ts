import { axiosPrivate } from './axiosInstance';
import {
  StudentProfile,
  StudentDashboardResponse,
  UpdateProfileRequest,
  AllGradeStudent,
  StudentTimetableResponse,
  ProgramResponse,
} from '../types/student.types';
import { StudentGradeResponse } from '../types/teacher.types';
import { StudentTuitionResponse, StudentPaymentHistoryResponse } from '../types/tuition.types';
import { Enrollment } from '../types/enrollment.types';
import { PaymentStatus, EnrollmentStatus } from '../types/common.types';

export const studentApi = {
  getProfile: async (): Promise<StudentProfile> => {
    const res = await axiosPrivate.get('/api/v2/student/profile');
    return res.data.result;
  },

  updateProfile: async (data: UpdateProfileRequest): Promise<StudentProfile> => {
    const res = await axiosPrivate.put('/api/v2/student/profile', data);
    return res.data.result;
  },

  uploadAvatar: async (file: File): Promise<{ avatarUrl: string }> => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await axiosPrivate.post(
      '/api/v2/student/profile/upload-avatar',
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } },
    );
    return res.data.result;
  },

  getDashboard: async (): Promise<StudentDashboardResponse> => {
    const res = await axiosPrivate.get('/api/v2/student/dashboard');
    return res.data.result;
  },

  getGrades: async (): Promise<AllGradeStudent> => {
    const res = await axiosPrivate.get('/api/v2/student/grades');
    return res.data.result;
  },

  getGradeByClass: async (classId: number): Promise<StudentGradeResponse> => {
    const res = await axiosPrivate.get(`/api/v2/student/grades/${classId}`);
    return res.data.result;
  },

  getProgramProgress: async (): Promise<ProgramResponse> => {
    const res = await axiosPrivate.get('/api/v2/student/program-progress');
    return res.data.result;
  },

  getSchedules: async (params?: {
    semesterId?: number;
    fromDate?: string;
    toDate?: string;
  }): Promise<StudentTimetableResponse> => {
    const res = await axiosPrivate.get('/api/v2/student/schedules', { params });
    return res.data.result;
  },

  getTuition: async (params?: { semesterId?: number }): Promise<StudentTuitionResponse[]> => {
    const res = await axiosPrivate.get('/api/v2/student/tuition', { params });
    return res.data.result;
  },

  getPayments: async (params?: {
    status?: PaymentStatus;
  }): Promise<StudentPaymentHistoryResponse[]> => {
    const res = await axiosPrivate.get('/api/v2/student/payments', { params });
    return res.data.result;
  },

  getEnrollments: async (params?: {
    semesterId?: number;
    status?: EnrollmentStatus;
  }): Promise<Enrollment[]> => {
    const res = await axiosPrivate.get('/api/v2/enrollments/my', { params });
    return res.data.result;
  },
};
