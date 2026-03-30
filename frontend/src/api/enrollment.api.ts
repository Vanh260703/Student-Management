import { axiosPrivate } from './axiosInstance';
import { ClassResponse } from '../types/class.types';
import { Enrollment } from '../types/enrollment.types';
import { EnrollmentStatus } from '../types/common.types';

export const enrollmentApi = {
  getAvailableClasses: async (params?: {
    semesterId?: number;
    subjectId?: number;
    departmentId?: number;
    hasSlot?: boolean;
    notEnrolled?: boolean;
    search?: string;
  }): Promise<ClassResponse[]> => {
    const res = await axiosPrivate.get('/api/v2/enrollments/available-classes', { params });
    return res.data.result;
  },

  enrollClass: async (classId: number): Promise<void> => {
    await axiosPrivate.post(`/api/v2/enrollments/${classId}`);
  },

  dropEnrollment: async (enrollmentId: number): Promise<void> => {
    await axiosPrivate.delete(`/api/v2/enrollments/${enrollmentId}`);
  },

  getMyEnrollments: async (params?: {
    semesterId?: number;
    status?: EnrollmentStatus;
  }): Promise<Enrollment[]> => {
    const res = await axiosPrivate.get('/api/v2/enrollments/my', { params });
    return res.data.result;
  },

  getEnrollment: async (enrollmentId: number): Promise<Enrollment> => {
    const res = await axiosPrivate.get(`/api/v2/enrollments/${enrollmentId}`);
    return res.data.result;
  },
};
