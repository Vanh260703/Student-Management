import { useEffect } from 'react'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/vi'
import { toast } from 'react-toastify'
import useNotificationStore from '@/store/notificationStore'
import LoadingSpinner from '@/components/common/LoadingSpinner'

dayjs.extend(relativeTime)
dayjs.locale('vi')

const typeBadge = {
  GRADE: 'bg-blue-100 text-blue-700',
  SCHEDULE: 'bg-purple-100 text-purple-700',
  PAYMENT: 'bg-green-100 text-green-700',
  SYSTEM: 'bg-gray-100 text-gray-700',
  ATTENDANCE: 'bg-orange-100 text-orange-700',
}

export default function NotificationsPage() {
  const { notifications, fetchNotifications, markAsRead, markAllAsRead, deleteNotification } =
    useNotificationStore()

  useEffect(() => {
    fetchNotifications()
  }, [fetchNotifications])

  const handleMarkAll = async () => {
    try {
      await markAllAsRead()
      toast.success('Đã đọc tất cả thông báo')
    } catch {
      toast.error('Có lỗi xảy ra')
    }
  }

  const handleDelete = async (id) => {
    try {
      await deleteNotification(id)
      toast.success('Đã xóa thông báo')
    } catch {
      toast.error('Xóa thất bại')
    }
  }

  const unreadCount = notifications.filter((n) => !n.isRead).length

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-800">Thông báo</h2>
          {unreadCount > 0 && (
            <p className="text-sm text-gray-500 mt-1">{unreadCount} chưa đọc</p>
          )}
        </div>
        {unreadCount > 0 && (
          <button
            onClick={handleMarkAll}
            className="px-4 py-2 text-sm bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100"
          >
            Đọc tất cả
          </button>
        )}
      </div>

      {notifications.length === 0 ? (
        <div className="text-center py-20 text-gray-400">
          <p className="text-4xl mb-3">🔔</p>
          <p>Chưa có thông báo nào</p>
        </div>
      ) : (
        <div className="space-y-2">
          {notifications.map((n) => (
            <div
              key={n.id}
              className={`bg-white rounded-xl shadow-sm p-4 flex items-start gap-4 transition ${!n.isRead ? 'border-l-4 border-blue-500' : ''}`}
            >
              {/* Type badge */}
              <span className={`mt-0.5 px-2 py-0.5 rounded-full text-xs font-semibold flex-shrink-0 ${typeBadge[n.type] ?? 'bg-gray-100 text-gray-600'}`}>
                {n.type}
              </span>

              {/* Content */}
              <div
                className="flex-1 cursor-pointer"
                onClick={() => !n.isRead && markAsRead(n.id)}
              >
                <p className={`text-sm font-medium text-gray-800 ${!n.isRead ? 'font-semibold' : ''}`}>
                  {n.title}
                </p>
                <p className="text-sm text-gray-600 mt-0.5">{n.message}</p>
                <p className="text-xs text-gray-400 mt-1">{dayjs(n.createdAt).fromNow()}</p>
              </div>

              {/* Actions */}
              <div className="flex flex-col gap-1 flex-shrink-0">
                {!n.isRead && (
                  <button
                    onClick={() => markAsRead(n.id)}
                    className="text-xs text-blue-600 hover:underline"
                  >
                    Đọc
                  </button>
                )}
                <button
                  onClick={() => handleDelete(n.id)}
                  className="text-xs text-red-400 hover:text-red-600"
                >
                  Xóa
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
