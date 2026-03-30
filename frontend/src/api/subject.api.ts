import { axiosPublic, axiosPrivate } from './axiosInstance';

export interface Subject {
  id: number;
  code: string;
  name: string;
  credits: number;
  description: string;
  isActive: boolean;
  departmentName: string;
}

export interface CreateSubjectRequest {
  departmentId: number;
  code: string;
  name: string;
  credits: number;
  description: string;
  isActive: boolean;
}

export interface UpdateSubjectRequest {
  name: string;
  description: string;
  isActive: boolean;
  credits: number;
}

export const subjectApi = {
  getSubjects: async (params?: {
    departmentId?: number;
    search?: string;
    isActive?: boolean;
    credits?: number;
  }): Promise<Subject[]> => {
    const res = await axiosPublic.get('/api/v2/subjects', { params });
    return res.data.result;
  },

  getSubject: async (id: number): Promise<Subject> => {
    const res = await axiosPublic.get(`/api/v2/subjects/${id}`);
    return res.data.result;
  },

  createSubject: async (data: CreateSubjectRequest): Promise<Subject> => {
    const res = await axiosPrivate.post('/api/v2/subjects', data);
    return res.data.result;
  },

  updateSubject: async (id: number, data: UpdateSubjectRequest): Promise<Subject> => {
    const res = await axiosPrivate.put(`/api/v2/subjects/${id}`, data);
    return res.data.result;
  },

  deleteSubject: async (id: number): Promise<void> => {
    await axiosPrivate.delete(`/api/v2/subjects/${id}`);
  },
};
