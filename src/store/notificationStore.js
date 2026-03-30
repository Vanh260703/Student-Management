import { create } from 'zustand'
import { notificationsApi } from '@/api/notifications'

const useNotificationStore = create((set) => ({
  notifications: [],
  unreadCount: 0,

  fetchUnreadCount: async () => {
    try {
      const { data } = await notificationsApi.getUnreadCount()
      set({ unreadCount: data.count ?? data ?? 0 })
    } catch {
      // ignore
    }
  },

  fetchNotifications: async () => {
    try {
      const { data } = await notificationsApi.getAll()
      set({ notifications: data.content ?? data ?? [] })
    } catch {
      // ignore
    }
  },

  markAsRead: async (id) => {
    await notificationsApi.markAsRead(id)
    set((state) => ({
      notifications: state.notifications.map((n) =>
        n.id === id ? { ...n, isRead: true } : n
      ),
      unreadCount: Math.max(0, state.unreadCount - 1),
    }))
  },

  markAllAsRead: async () => {
    await notificationsApi.markAllAsRead()
    set((state) => ({
      notifications: state.notifications.map((n) => ({ ...n, isRead: true })),
      unreadCount: 0,
    }))
  },

  deleteNotification: async (id) => {
    await notificationsApi.delete(id)
    set((state) => ({
      notifications: state.notifications.filter((n) => n.id !== id),
    }))
  },
}))

export default useNotificationStore
