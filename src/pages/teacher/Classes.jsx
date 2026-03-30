import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import { profileApi } from '@/api/profile'
import LoadingSpinner from '@/components/common/LoadingSpinner'
import dayjs from 'dayjs'

const statusBadge = {
  OPEN: 'bg-green-100 text-green-700',
  CLOSED: 'bg-red-100 text-red-700',
  UPCOMING: 'bg-blue-100 text-blue-700',
}
const statusLabel = { OPEN: 'Đang mở', CLOSED: 'Đã đóng', UPCOMING: 'Sắp khai giảng' }

export default function TeacherClasses() {
  const [classes, setClasses] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    profileApi.getTeacherClasses()
      .then((res) => setClasses(res.data?.content ?? res.data ?? []))
      .catch(() => toast.error('Không thể tải danh sách lớp'))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <LoadingSpinner />

  return (
    <div>
      <h2 className="text-2xl font-bold text-gray-800 mb-6">Lớp học của tôi</h2>
      {classes.length === 0 ? (
        <div className="text-center py-16 text-gray-400">Chưa có lớp học nào</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {classes.map((c) => (
            <div key={c.classId ?? c.id} className="bg-white rounded-xl shadow-sm p-5">
              <div className="flex items-start justify-between mb-3">
                <h3 className="font-semibold text-gray-800">{c.className ?? c.name}</h3>
                <span className={`px-2 py-0.5 rounded-full text-xs font-semibold ${statusBadge[c.status] ?? 'bg-gray-100 text-gray-600'}`}>
                  {statusLabel[c.status] ?? c.status}
                </span>
              </div>
              <div className="space-y-1.5 text-sm text-gray-600">
                <p>📚 Môn: <span className="font-medium text-gray-800">{c.subjectName}</span></p>
                <p>👥 Sĩ số: <span className="font-medium text-gray-800">{c.currentStudents ?? 0}/{c.maxStudents ?? '∞'}</span></p>
                {c.room && <p>🚪 Phòng: <span className="font-medium text-gray-800">{c.room}</span></p>}
                {c.schedule && <p>📅 Lịch: <span className="font-medium text-gray-800">{c.schedule}</span></p>}
                {c.startDate && (
                  <p>🗓️ Bắt đầu: <span className="font-medium text-gray-800">{dayjs(c.startDate).format('DD/MM/YYYY')}</span></p>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
