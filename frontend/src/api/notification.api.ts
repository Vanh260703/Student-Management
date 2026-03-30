import { axiosPrivate } from './axiosInstance';
import { NotificationResponse } from '../types/notification.types';

export const notificationApi = {
  getAll: async (): Promise<NotificationResponse[]> => {
    const res = await axiosPrivate.get('/api/v2/notifications');
    return res.data.result;
  },

  getUnread: async (): Promise<NotificationResponse[]> => {
    const res = await axiosPrivate.get('/api/v2/notifications/unread');
    return res.data.result;
  },

  getUnreadCount: async (): Promise<number> => {
    const res = await axiosPrivate.get('/api/v2/notifications/unread/count');
    return res.data.result;
  },

  getById: async (id: number): Promise<NotificationResponse> => {
    const res = await axiosPrivate.get(`/api/v2/notifications/${id}`);
    return res.data.result;
  },

  markAsRead: async (id: number): Promise<NotificationResponse> => {
    const res = await axiosPrivate.put(`/api/v2/notifications/${id}/mark-as-read`);
    return res.data.result;
  },

  markAllAsRead: async (): Promise<void> => {
    await axiosPrivate.put('/api/v2/notifications/mark-all-as-read');
  },

  deleteNotification: async (id: number): Promise<void> => {
    await axiosPrivate.delete(`/api/v2/notifications/${id}`);
  },
};
