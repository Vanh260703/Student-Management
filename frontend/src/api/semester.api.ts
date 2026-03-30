import { axiosPublic, axiosPrivate } from './axiosInstance';

export interface AcademicYear {
  id: number;
  name: string;
  startDate: string;
  endDate: string;
  isCurrent: boolean;
}

export interface Semester {
  id: number;
  academicYear: AcademicYear;
  name: string;
  semesterNumber: number;
  startDate: string;
  endDate: string;
  registrationStart: string;
  registrationEnd: string;
  isActive: boolean;
}

export interface CreateSemesterRequest {
  academicYearId: number;
  semesterNumber: number;
  startDate: string;
  endDate: string;
  registrationStart: string;
  registrationEnd: string;
  isActive: boolean;
}

export interface UpdateSemesterRequest {
  startDate: string;
  endDate: string;
  registrationStart: string;
  registrationEnd: string;
}

export const semesterApi = {
  getSemesters: async (params?: {
    academicYearId?: number;
    isActive?: boolean;
    semesterNumber?: number;
  }): Promise<Semester[]> => {
    const res = await axiosPublic.get('/api/v2/semesters', { params });
    return res.data.result;
  },

  createSemester: async (data: CreateSemesterRequest): Promise<Semester> => {
    const res = await axiosPrivate.post('/api/v2/semesters', data);
    return res.data.result;
  },

  updateSemester: async (id: number, data: UpdateSemesterRequest): Promise<Semester> => {
    const res = await axiosPrivate.put(`/api/v2/semesters/${id}`, data);
    return res.data.result;
  },

  toggleSemesterActive: async (id: number): Promise<void> => {
    await axiosPrivate.patch(`/api/v2/semesters/${id}/toggle-active`);
  },
};
