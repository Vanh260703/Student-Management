import { axiosPrivate, axiosPublic } from './axiosInstance';
import {
  ClassResponse,
  ClassScheduleResponse,
  CreateClassRequest,
  CreateScheduleRequest,
} from '../types/class.types';
import { StudentProfile } from '../types/student.types';
import { ClassStatus } from '../types/common.types';

export const classApi = {
  getClasses: async (params?: {
    semesterId?: number;
    subjectId?: number;
    teacherId?: number;
    status?: ClassStatus;
    search?: string;
    hasSlot?: boolean;
  }): Promise<ClassResponse[]> => {
    const res = await axiosPublic.get('/api/v2/classes', { params });
    return res.data.result;
  },

  getClass: async (classId: number): Promise<ClassResponse> => {
    const res = await axiosPublic.get(`/api/v2/classes/${classId}`);
    return res.data.result;
  },

  createClass: async (data: CreateClassRequest): Promise<ClassResponse> => {
    const res = await axiosPrivate.post('/api/v2/classes', data);
    return res.data.result;
  },

  changeClassStatus: async (classId: number, status: ClassStatus): Promise<void> => {
    await axiosPrivate.patch(`/api/v2/classes/${classId}/change-status`, { status });
  },

  deleteClass: async (classId: number): Promise<void> => {
    await axiosPrivate.delete(`/api/v2/classes/${classId}`);
  },

  getClassStudents: async (
    classId: number,
    search?: string,
  ): Promise<StudentProfile[]> => {
    const res = await axiosPrivate.get(`/api/v2/classes/${classId}/student`, {
      params: search ? { search } : undefined,
    });
    return res.data.result;
  },

  createSchedule: async (
    classId: number,
    data: CreateScheduleRequest,
  ): Promise<ClassScheduleResponse> => {
    const res = await axiosPrivate.post(`/api/v2/classes/${classId}/schedules`, data);
    return res.data.result;
  },

  getSchedules: async (classId: number): Promise<ClassScheduleResponse> => {
    const res = await axiosPublic.get(`/api/v2/classes/${classId}/schedules`);
    return res.data.result;
  },
};
