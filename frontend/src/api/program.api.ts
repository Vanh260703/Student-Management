import { axiosPublic, axiosPrivate } from './axiosInstance';
import { Subject } from './subject.api';

export interface Program {
  id: number;
  code: string;
  name: string;
  totalCredits: number;
  durationYears: number;
  description: string;
  departmentId: number;
}

export interface ProgramSubject {
  id: number;
  subject: Subject;
  semester: number;
  isRequired: boolean;
  prerequisiteSubject: Subject | null;
}

export interface CreateProgramRequest {
  departmentId: number;
  code: string;
  name: string;
  totalCredits: number;
  durationYear: number;
  description: string;
}

export interface UpdateProgramRequest {
  name: string;
  totalCredits: number;
  description: string;
  durationYear: number;
}

export interface AddProgramSubjectRequest {
  subjectId: number;
  semester: number;
  isRequired: boolean;
  prerequisiteSubjectId: number | null;
}

export const programApi = {
  getPrograms: async (params?: {
    departmentId?: number;
    search?: string;
  }): Promise<Program[]> => {
    const res = await axiosPublic.get('/api/v2/programs', { params });
    return res.data.result;
  },

  getProgram: async (id: number): Promise<Program> => {
    const res = await axiosPublic.get(`/api/v2/programs/${id}`);
    return res.data.result;
  },

  createProgram: async (data: CreateProgramRequest): Promise<Program> => {
    const res = await axiosPrivate.post('/api/v2/programs', data);
    return res.data.result;
  },

  updateProgram: async (id: number, data: UpdateProgramRequest): Promise<Program> => {
    const res = await axiosPrivate.put(`/api/v2/programs/${id}`, data);
    return res.data.result;
  },

  deleteProgram: async (id: number): Promise<void> => {
    await axiosPrivate.delete(`/api/v2/programs/${id}`);
  },

  getProgramSubjects: async (
    id: number,
    params?: { semester?: number; isRequired?: boolean },
  ): Promise<ProgramSubject[]> => {
    const res = await axiosPublic.get(`/api/v2/programs/${id}/subjects`, { params });
    return res.data.result;
  },

  addProgramSubject: async (
    id: number,
    data: AddProgramSubjectRequest,
  ): Promise<ProgramSubject> => {
    const res = await axiosPrivate.post(`/api/v2/programs/${id}/subjects`, data);
    return res.data.result;
  },

  deleteProgramSubject: async (programId: number, subjectId: number): Promise<void> => {
    await axiosPrivate.delete(`/api/v2/programs/${programId}/subjects/${subjectId}`);
  },
};
