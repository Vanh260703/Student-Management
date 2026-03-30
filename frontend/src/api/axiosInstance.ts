import axios from 'axios';
import { message } from 'antd';
import { useAuthStore } from '../stores/authStore';

const BASE_URL = 'http://localhost:8080';

export const axiosPublic = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
});

export const axiosPrivate = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,
});

// Request interceptor: attach Bearer token
axiosPrivate.interceptors.request.use((config) => {
  const { accessToken } = useAuthStore.getState();
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});

// Silent refresh state
let isRefreshing = false;
let failedQueue: Array<{ resolve: (token: string) => void; reject: (err: unknown) => void }> = [];

const processQueue = (error: unknown, token: string | null) => {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error);
    } else if (token) {
      resolve(token);
    }
  });
  failedQueue = [];
};

// Response interceptor: silent refresh + global error handling
axiosPrivate.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise<string>((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then((token) => {
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return axiosPrivate(originalRequest);
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const res = await axiosPublic.post('/api/v2/auth/refresh');
        const newToken: string = res.data.result.accessToken;

        useAuthStore.getState().setAuth(
          newToken,
          res.data.result.role,
          res.data.result.email,
        );

        processQueue(null, newToken);
        originalRequest.headers.Authorization = `Bearer ${newToken}`;
        return axiosPrivate(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        useAuthStore.getState().clearAuth();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    // Global error handler
    const status = error.response?.status;
    const errorMessage: string = error.response?.data?.message ?? '';

    switch (status) {
      case 400:
        message.error(errorMessage || 'Dữ liệu không hợp lệ');
        break;
      case 403:
        message.error('Bạn không có quyền thực hiện thao tác này');
        break;
      case 404:
        message.error(errorMessage || 'Không tìm thấy dữ liệu');
        break;
      case 413:
        message.error('File quá lớn. Tối đa 10MB');
        break;
      case 500:
        message.error('Lỗi hệ thống, vui lòng thử lại sau');
        break;
      default:
        break;
    }

    return Promise.reject(error);
  },
);
