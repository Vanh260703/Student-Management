import { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/vi'
import useNotificationStore from '@/store/notificationStore'
import useAuthStore from '@/store/authStore'

dayjs.extend(relativeTime)
dayjs.locale('vi')

const roleBase = {
  ROLE_ADMIN: '/admin',
  ROLE_TEACHER: '/teacher',
  ROLE_STUDENT: '/student',
}

export default function NotificationBell() {
  const [open, setOpen] = useState(false)
  const ref = useRef(null)
  const navigate = useNavigate()
  const { user } = useAuthStore()
  const { unreadCount, notifications, fetchUnreadCount, fetchNotifications, markAsRead, markAllAsRead } =
    useNotificationStore()

  useEffect(() => {
    fetchUnreadCount()
    const interval = setInterval(fetchUnreadCount, 30000)
    return () => clearInterval(interval)
  }, [fetchUnreadCount])

  useEffect(() => {
    if (open) fetchNotifications()
  }, [open, fetchNotifications])

  // Close on outside click
  useEffect(() => {
    const handler = (e) => {
      if (ref.current && !ref.current.contains(e.target)) setOpen(false)
    }
    document.addEventListener('mousedown', handler)
    return () => document.removeEventListener('mousedown', handler)
  }, [])

  const handleViewAll = () => {
    setOpen(false)
    navigate(`${roleBase[user?.role]}/notifications`)
  }

  return (
    <div className="relative" ref={ref}>
      <button
        onClick={() => setOpen((o) => !o)}
        className="relative p-2 rounded-lg hover:bg-gray-100 transition"
      >
        <span className="text-xl">🔔</span>
        {unreadCount > 0 && (
          <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full min-w-[18px] h-[18px] flex items-center justify-center px-1">
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        )}
      </button>

      {open && (
        <div className="absolute right-0 top-12 w-80 bg-white rounded-xl shadow-xl border border-gray-200 z-50">
          <div className="flex items-center justify-between px-4 py-3 border-b">
            <h3 className="font-semibold text-gray-800">Thông báo</h3>
            {unreadCount > 0 && (
              <button
                onClick={markAllAsRead}
                className="text-xs text-blue-600 hover:underline"
              >
                Đọc tất cả
              </button>
            )}
          </div>

          <div className="max-h-72 overflow-y-auto divide-y divide-gray-100">
            {notifications.length === 0 ? (
              <p className="text-center text-gray-400 py-6 text-sm">Không có thông báo</p>
            ) : (
              notifications.slice(0, 5).map((n) => (
                <div
                  key={n.id}
                  onClick={() => markAsRead(n.id)}
                  className={`px-4 py-3 cursor-pointer hover:bg-gray-50 transition ${
                    !n.isRead ? 'bg-blue-50' : ''
                  }`}
                >
                  <p className="text-sm text-gray-800 font-medium line-clamp-1">{n.title}</p>
                  <p className="text-xs text-gray-500 mt-0.5 line-clamp-2">{n.message}</p>
                  <p className="text-xs text-gray-400 mt-1">{dayjs(n.createdAt).fromNow()}</p>
                </div>
              ))
            )}
          </div>

          <div className="px-4 py-3 border-t">
            <button
              onClick={handleViewAll}
              className="w-full text-sm text-blue-600 hover:underline text-center"
            >
              Xem tất cả thông báo
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
