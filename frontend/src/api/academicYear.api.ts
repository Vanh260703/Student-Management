import { axiosPublic, axiosPrivate } from './axiosInstance';

export interface AcademicYear {
  id: number;
  name: string;
  startDate: string;
  endDate: string;
  isCurrent: boolean;
}

export interface CreateAcademicYearRequest {
  name: string;
  startDate: string;
  endDate: string;
  isCurrent: boolean;
}

export const academicYearApi = {
  getAcademicYears: async (params?: { isCurrent?: boolean }): Promise<AcademicYear[]> => {
    const res = await axiosPublic.get('/api/v2/academic-years', { params });
    return res.data.result;
  },

  createAcademicYear: async (data: CreateAcademicYearRequest): Promise<AcademicYear> => {
    const res = await axiosPrivate.post('/api/v2/academic-years', data);
    return res.data.result;
  },
};
