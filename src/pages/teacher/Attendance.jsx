import { useEffect, useState } from 'react'
import { toast } from 'react-toastify'
import dayjs from 'dayjs'
import { profileApi } from '@/api/profile'
import { attendanceApi } from '@/api/attendance'
import LoadingSpinner from '@/components/common/LoadingSpinner'

const STATUS_OPTIONS = ['PRESENT', 'ABSENT', 'LATE']
const statusBadge = {
  PRESENT: 'bg-green-100 text-green-700',
  ABSENT: 'bg-red-100 text-red-700',
  LATE: 'bg-yellow-100 text-yellow-700',
}
const statusLabel = { PRESENT: 'Có mặt', ABSENT: 'Vắng', LATE: 'Muộn' }

export default function TeacherAttendance() {
  const [classes, setClasses] = useState([])
  const [selectedClass, setSelectedClass] = useState(null)
  const [date, setDate] = useState(dayjs().format('YYYY-MM-DD'))
  const [attendance, setAttendance] = useState([])
  const [loadingClasses, setLoadingClasses] = useState(true)
  const [loadingAttendance, setLoadingAttendance] = useState(false)
  const [saving, setSaving] = useState(false)
  const [localStatus, setLocalStatus] = useState({}) // { enrollmentId: status }

  useEffect(() => {
    profileApi.getTeacherClasses()
      .then((res) => setClasses(res.data?.content ?? res.data ?? []))
      .catch(() => toast.error('Không thể tải lớp học'))
      .finally(() => setLoadingClasses(false))
  }, [])

  const loadAttendance = async (cls, d) => {
    setLoadingAttendance(true)
    try {
      const { data } = await attendanceApi.getByDate(cls.classId ?? cls.id, d)
      const list = data?.students ?? data ?? []
      setAttendance(list)
      const init = {}
      list.forEach((s) => { init[s.enrollmentId ?? s.id] = s.status ?? 'PRESENT' })
      setLocalStatus(init)
    } catch {
      setAttendance([])
      setLocalStatus({})
    } finally {
      setLoadingAttendance(false)
    }
  }

  const selectClass = (cls) => {
    setSelectedClass(cls)
    loadAttendance(cls, date)
  }

  const handleDateChange = (d) => {
    setDate(d)
    if (selectedClass) loadAttendance(selectedClass, d)
  }

  const handleSave = async () => {
    try {
      setSaving(true)
      const payload = attendance.map((s) => ({
        enrollmentId: s.enrollmentId ?? s.id,
        status: localStatus[s.enrollmentId ?? s.id] ?? 'PRESENT',
      }))
      await attendanceApi.markAttendance(selectedClass.classId ?? selectedClass.id, date, payload)
      toast.success('Lưu điểm danh thành công!')
    } catch (err) {
      toast.error(err.response?.data?.message || 'Lưu thất bại')
    } finally {
      setSaving(false)
    }
  }

  if (loadingClasses) return <LoadingSpinner />

  return (
    <div>
      <h2 className="text-2xl font-bold text-gray-800 mb-6">Điểm danh</h2>

      {!selectedClass ? (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
          {classes.length === 0 ? (
            <div className="col-span-3 text-center py-16 text-gray-400">Không có lớp học</div>
          ) : (
            classes.map((c) => (
              <button
                key={c.classId ?? c.id}
                onClick={() => selectClass(c)}
                className="bg-white rounded-xl shadow-sm p-5 text-left hover:shadow-md transition border-2 border-transparent hover:border-blue-300"
              >
                <h3 className="font-semibold text-gray-800">{c.className ?? c.name}</h3>
                <p className="text-sm text-gray-500 mt-1">{c.subjectName}</p>
              </button>
            ))
          )}
        </div>
      ) : (
        <div>
          <button onClick={() => setSelectedClass(null)} className="mb-4 text-sm text-blue-600 hover:underline">
            ← Quay lại
          </button>

          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-4">
            <h3 className="text-lg font-semibold text-gray-800">{selectedClass.className}</h3>
            <div className="flex items-center gap-3">
              <input
                type="date"
                value={date}
                onChange={(e) => handleDateChange(e.target.value)}
                className="px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <button
                onClick={handleSave}
                disabled={saving || attendance.length === 0}
                className="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 disabled:opacity-60"
              >
                {saving ? 'Đang lưu...' : 'Lưu điểm danh'}
              </button>
            </div>
          </div>

          {loadingAttendance ? (
            <LoadingSpinner />
          ) : (
            <div className="bg-white rounded-xl shadow-sm overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="bg-gray-50 text-gray-600 uppercase text-xs">
                  <tr>
                    <th className="px-4 py-3 text-left">Sinh viên</th>
                    <th className="px-4 py-3 text-center">Trạng thái</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {attendance.length === 0 ? (
                    <tr><td colSpan={2} className="text-center py-10 text-gray-400">Không có dữ liệu cho ngày này</td></tr>
                  ) : (
                    attendance.map((s) => {
                      const sid = s.enrollmentId ?? s.id
                      const status = localStatus[sid] ?? 'PRESENT'
                      return (
                        <tr key={sid} className="hover:bg-gray-50">
                          <td className="px-4 py-3 font-medium text-gray-800">{s.studentName}</td>
                          <td className="px-4 py-3 text-center">
                            <div className="flex items-center justify-center gap-2">
                              {STATUS_OPTIONS.map((opt) => (
                                <button
                                  key={opt}
                                  onClick={() => setLocalStatus((prev) => ({ ...prev, [sid]: opt }))}
                                  className={`px-3 py-1 rounded-full text-xs font-semibold transition ${
                                    status === opt ? statusBadge[opt] + ' ring-2 ring-offset-1 ring-current' : 'bg-gray-100 text-gray-500 hover:bg-gray-200'
                                  }`}
                                >
                                  {statusLabel[opt]}
                                </button>
                              ))}
                            </div>
                          </td>
                        </tr>
                      )
                    })
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
