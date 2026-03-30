import { useEffect, useState } from 'react'
import { dashboardApi } from '@/api/dashboard'
import StatCard from '@/components/common/StatCard'
import LoadingSpinner from '@/components/common/LoadingSpinner'
import dayjs from 'dayjs'

const statusColor = {
  ACTIVE: 'text-green-600 bg-green-100',
  INACTIVE: 'text-red-600 bg-red-100',
  GRADUATED: 'text-blue-600 bg-blue-100',
}

const tuitionColor = {
  PAID: 'text-green-600 bg-green-100',
  PENDING: 'text-yellow-600 bg-yellow-100',
  OVERDUE: 'text-red-600 bg-red-100',
}

export default function StudentDashboard() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    dashboardApi.getStudentDashboard()
      .then((res) => setData(res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <LoadingSpinner />
  if (!data) return <div className="text-center py-16 text-gray-400">Không thể tải dữ liệu</div>

  return (
    <div>
      {/* Header */}
      <div className="bg-gradient-to-r from-blue-600 to-purple-600 rounded-2xl p-6 text-white mb-6">
        <div className="flex items-start gap-4">
          <div className="w-16 h-16 rounded-full bg-white/20 flex items-center justify-center text-3xl">
            🎓
          </div>
          <div>
            <h2 className="text-2xl font-bold">{data.studentInfo?.fullName}</h2>
            <p className="text-blue-100 text-sm mt-1">{data.programName} — {data.departmentName}</p>
            <p className="text-blue-100 text-sm">Khoá {data.enrollmentYear}</p>
            <span className={`inline-block mt-2 px-3 py-0.5 rounded-full text-xs font-semibold ${statusColor[data.studentStatus] ?? 'bg-gray-100 text-gray-600'}`}>
              {data.studentStatus}
            </span>
          </div>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <StatCard label="GPA hiện tại" value={data.currentGPA?.toFixed(2)} icon="⭐" color="blue" />
        <StatCard label="Lớp đã đăng ký" value={data.totalEnrolledClasses} icon="🏫" color="purple" />
        <StatCard label="Tín chỉ hoàn thành" value={data.totalCompletedCredits} icon="📚" color="green" />
        <StatCard label="Điểm TB" value={data.averageScore?.toFixed(1)} icon="📊" color="orange" />
        <StatCard label="Môn qua" value={data.totalPassedSubjects} icon="✅" color="green" />
        <StatCard label="Môn trượt" value={data.totalFailedSubjects} icon="❌" color="red" />
        <StatCard label="Tỷ lệ điểm danh" value={data.attendanceRate ? `${data.attendanceRate}%` : '—'} icon="📅" color="blue" />
        <StatCard label="Ngày vắng" value={data.totalAbsentDays} icon="😓" color="orange"
          sub={`Muộn: ${data.totalLateArrivals ?? 0} lần`} />
      </div>

      {/* Tuition */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <div className="bg-white rounded-xl shadow-sm p-5">
          <p className="text-sm text-gray-500">Tổng học phí</p>
          <p className="text-2xl font-bold text-gray-800 mt-1">
            {data.totalTuitionFee?.toLocaleString('vi-VN')} đ
          </p>
        </div>
        <div className="bg-white rounded-xl shadow-sm p-5">
          <p className="text-sm text-gray-500">Đã thanh toán</p>
          <p className="text-2xl font-bold text-green-600 mt-1">
            {data.paidAmount?.toLocaleString('vi-VN')} đ
          </p>
        </div>
        <div className="bg-white rounded-xl shadow-sm p-5">
          <p className="text-sm text-gray-500">Còn lại</p>
          <p className="text-2xl font-bold text-orange-500 mt-1">
            {data.remainingAmount?.toLocaleString('vi-VN')} đ
          </p>
          <span className={`inline-block mt-2 px-2 py-0.5 rounded-full text-xs font-semibold ${tuitionColor[data.tuitionStatus] ?? 'bg-gray-100 text-gray-600'}`}>
            {data.tuitionStatus}
          </span>
        </div>
      </div>

      {/* Upcoming class */}
      {data.nextClassName && (
        <div className="bg-white rounded-xl shadow-sm p-5">
          <h3 className="text-base font-semibold text-gray-700 mb-3">Lớp học sắp tới</h3>
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center text-2xl">🏫</div>
            <div>
              <p className="font-semibold text-gray-800">{data.nextClassName}</p>
              <p className="text-sm text-gray-500">Phòng: {data.nextClassRoom ?? '—'}</p>
              <p className="text-sm text-gray-500">
                Thời gian: {data.nextClassTime ? dayjs(data.nextClassTime).format('HH:mm DD/MM/YYYY') : '—'}
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Recent grades */}
      {data.recentGrades?.length > 0 && (
        <div className="bg-white rounded-xl shadow-sm p-5 mt-4">
          <h3 className="text-base font-semibold text-gray-700 mb-3">Điểm số gần đây</h3>
          <div className="divide-y divide-gray-100">
            {data.recentGrades.map((g, i) => (
              <div key={i} className="flex items-center justify-between py-2.5">
                <div>
                  <p className="font-medium text-gray-800 text-sm">{g.subjectName ?? g.className}</p>
                  <p className="text-xs text-gray-400">{g.semester}</p>
                </div>
                <span className={`font-bold text-lg ${g.finalGrade >= 5 ? 'text-green-600' : 'text-red-500'}`}>
                  {g.finalGrade}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
