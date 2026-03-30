import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import { enrollmentApi } from '@/api/classes'
import Modal from '@/components/common/Modal'
import LoadingSpinner from '@/components/common/LoadingSpinner'
import dayjs from 'dayjs'

const statusBadge = {
  OPEN: 'bg-green-100 text-green-700',
  CLOSED: 'bg-red-100 text-red-700',
  UPCOMING: 'bg-blue-100 text-blue-700',
}

export default function StudentClasses() {
  const [tab, setTab] = useState('my') // 'my' | 'available'
  const [myClasses, setMyClasses] = useState([])
  const [availableClasses, setAvailableClasses] = useState([])
  const [loading, setLoading] = useState(true)
  const [enrolling, setEnrolling] = useState(null)

  const fetchMyClasses = () =>
    enrollmentApi.getMy({}).then((res) => setMyClasses(res.data?.content ?? res.data ?? []))

  const fetchAvailable = () =>
    enrollmentApi.getAvailable({}).then((res) => setAvailableClasses(res.data?.content ?? res.data ?? []))

  useEffect(() => {
    setLoading(true)
    Promise.all([fetchMyClasses(), fetchAvailable()])
      .catch(() => toast.error('Không thể tải dữ liệu lớp học'))
      .finally(() => setLoading(false))
  }, [])

  const handleEnroll = async (classId) => {
    try {
      setEnrolling(classId)
      await enrollmentApi.enroll(classId)
      toast.success('Đăng ký lớp thành công!')
      await Promise.all([fetchMyClasses(), fetchAvailable()])
    } catch (err) {
      toast.error(err.response?.data?.message || 'Đăng ký thất bại')
    } finally {
      setEnrolling(null)
    }
  }

  const handleUnenroll = async (enrollmentId) => {
    if (!window.confirm('Bạn chắc chắn muốn hủy đăng ký lớp này?')) return
    try {
      await enrollmentApi.unenroll(enrollmentId)
      toast.success('Đã hủy đăng ký')
      fetchMyClasses()
    } catch {
      toast.error('Hủy đăng ký thất bại')
    }
  }

  if (loading) return <LoadingSpinner />

  return (
    <div>
      <h2 className="text-2xl font-bold text-gray-800 mb-6">Lớp học</h2>

      {/* Tabs */}
      <div className="flex gap-2 mb-6">
        {[
          { key: 'my', label: `Lớp của tôi (${myClasses.length})` },
          { key: 'available', label: `Lớp có thể đăng ký (${availableClasses.length})` },
        ].map((t) => (
          <button
            key={t.key}
            onClick={() => setTab(t.key)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition ${tab === t.key ? 'bg-blue-600 text-white' : 'bg-white text-gray-600 border border-gray-300 hover:bg-gray-50'}`}
          >
            {t.label}
          </button>
        ))}
      </div>

      {tab === 'my' && (
        <div className="bg-white rounded-xl shadow-sm overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-600 uppercase text-xs">
              <tr>
                <th className="px-4 py-3 text-left">Lớp học</th>
                <th className="px-4 py-3 text-left">Môn học</th>
                <th className="px-4 py-3 text-left">Giảng viên</th>
                <th className="px-4 py-3 text-left">Trạng thái</th>
                <th className="px-4 py-3 text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {myClasses.length === 0 ? (
                <tr><td colSpan={5} className="text-center py-10 text-gray-400">Chưa đăng ký lớp nào</td></tr>
              ) : (
                myClasses.map((e) => (
                  <tr key={e.enrollmentId ?? e.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3 font-medium text-gray-800">{e.className}</td>
                    <td className="px-4 py-3 text-gray-600">{e.subjectName}</td>
                    <td className="px-4 py-3 text-gray-600">{e.teacherName}</td>
                    <td className="px-4 py-3">
                      <span className={`px-2 py-0.5 rounded-full text-xs font-semibold ${statusBadge[e.status] ?? 'bg-gray-100 text-gray-600'}`}>
                        {e.status}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-right">
                      <button
                        onClick={() => handleUnenroll(e.enrollmentId ?? e.id)}
                        className="px-3 py-1 text-xs bg-red-50 text-red-600 rounded-lg hover:bg-red-100"
                      >
                        Hủy đăng ký
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {tab === 'available' && (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {availableClasses.length === 0 ? (
            <div className="col-span-3 text-center py-16 text-gray-400">Không có lớp nào có thể đăng ký</div>
          ) : (
            availableClasses.map((c) => (
              <div key={c.classId ?? c.id} className="bg-white rounded-xl shadow-sm p-5">
                <div className="flex items-start justify-between mb-3">
                  <h3 className="font-semibold text-gray-800">{c.className ?? c.name}</h3>
                  <span className="px-2 py-0.5 rounded-full text-xs font-semibold bg-green-100 text-green-700">
                    Còn {c.availableSlots ?? (c.maxStudents - c.currentStudents)} chỗ
                  </span>
                </div>
                <div className="space-y-1 text-sm text-gray-600 mb-4">
                  <p>📚 {c.subjectName}</p>
                  <p>👨‍🏫 {c.teacherName}</p>
                  {c.schedule && <p>📅 {c.schedule}</p>}
                  {c.startDate && <p>🗓️ {dayjs(c.startDate).format('DD/MM/YYYY')}</p>}
                </div>
                <button
                  onClick={() => handleEnroll(c.classId ?? c.id)}
                  disabled={enrolling === (c.classId ?? c.id)}
                  className="w-full py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-60"
                >
                  {enrolling === (c.classId ?? c.id) ? 'Đang đăng ký...' : 'Đăng ký'}
                </button>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  )
}
