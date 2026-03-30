import { axiosPublic, axiosPrivate } from './axiosInstance';
import { TeacherProfile } from '../types/teacher.types';
import { StudentProfile } from '../types/student.types';
import { StudentStatus } from '../types/common.types';

export interface Department {
  id: number;
  code: string;
  name: string;
  description: string;
  headTeacher: TeacherProfile | null;
}

export interface CreateDepartmentRequest {
  code: string;
  name: string;
  description: string;
  headTeacherId: number;
}

export interface UpdateDepartmentRequest {
  name: string;
  description: string;
  headTeacherId: number;
}

export const departmentApi = {
  getDepartments: async (): Promise<Department[]> => {
    const res = await axiosPublic.get('/api/v2/departments');
    return res.data.result;
  },

  getDepartment: async (id: number): Promise<Department> => {
    const res = await axiosPublic.get(`/api/v2/departments/${id}`);
    return res.data.result;
  },

  createDepartment: async (data: CreateDepartmentRequest): Promise<Department> => {
    const res = await axiosPrivate.post('/api/v2/departments', data);
    return res.data.result;
  },

  updateDepartment: async (
    id: number,
    data: UpdateDepartmentRequest,
  ): Promise<Department> => {
    const res = await axiosPrivate.put(`/api/v2/departments/${id}`, data);
    return res.data.result;
  },

  deleteDepartment: async (id: number): Promise<void> => {
    await axiosPrivate.delete(`/api/v2/departments/${id}`);
  },

  getDepartmentTeachers: async (
    id: number,
    params?: { search?: string; degree?: string },
  ): Promise<TeacherProfile[]> => {
    const res = await axiosPrivate.get(`/api/v2/departments/${id}/teachers`, { params });
    return res.data.result;
  },

  getDepartmentStudents: async (
    id: number,
    params?: {
      search?: string;
      programId?: number;
      enrollmentYear?: number;
      status?: StudentStatus;
    },
  ): Promise<StudentProfile[]> => {
    const res = await axiosPrivate.get(`/api/v2/departments/${id}/students`, { params });
    return res.data.result;
  },
};
