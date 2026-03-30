import { axiosPublic, axiosPrivate } from './axiosInstance';
import { AuthResult, UserInfo } from '../types/auth.types';

export const authApi = {
  login: async (email: string, password: string): Promise<AuthResult> => {
    const res = await axiosPublic.post('/api/v2/auth/login', { email, password });
    return res.data.result;
  },

  logout: async (): Promise<void> => {
    await axiosPrivate.post('/api/v2/auth/logout');
  },

  refresh: async (): Promise<AuthResult> => {
    const res = await axiosPublic.post('/api/v2/auth/refresh');
    return res.data.result;
  },

  getMe: async (): Promise<UserInfo> => {
    const res = await axiosPrivate.get('/api/v2/auth/me');
    return res.data.result;
  },

  forgotPassword: async (email: string): Promise<void> => {
    await axiosPublic.post('/api/v2/auth/forgot-password', { email });
  },

  changePassword: async (data: {
    oldPassword: string;
    newPassword: string;
    confirmPassword: string;
  }): Promise<void> => {
    await axiosPrivate.patch('/api/v2/auth/change-password', data);
  },
};
