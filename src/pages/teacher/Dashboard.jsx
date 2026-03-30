import { useEffect, useState } from 'react'
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
} from 'recharts'
import { dashboardApi } from '@/api/dashboard'
import StatCard from '@/components/common/StatCard'
import LoadingSpinner from '@/components/common/LoadingSpinner'
import dayjs from 'dayjs'

export default function TeacherDashboard() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    dashboardApi.getTeacherDashboard()
      .then((res) => setData(res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <LoadingSpinner />
  if (!data) return <div className="text-center py-16 text-gray-400">Không thể tải dữ liệu</div>

  const classChartData = (data.classes ?? []).map((c) => ({
    name: c.className ?? c.name,
    'Sinh viên': c.totalStudents ?? c.studentCount ?? 0,
  }))

  return (
    <div>
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-gray-800">
          Xin chào, {data.teacherInfo?.fullName ?? 'Giảng viên'}!
        </h2>
        <p className="text-sm text-gray-500 mt-1">
          {data.departmentName} &nbsp;•&nbsp;
          Cập nhật: {data.lastUpdated ? dayjs(data.lastUpdated).format('HH:mm DD/MM/YYYY') : '—'}
        </p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <StatCard label="Lớp đang dạy" value={data.totalClasses} icon="🏫" color="blue" />
        <StatCard label="Tổng sinh viên" value={data.totalStudents} icon="🎓" color="green" />
        <StatCard label="Đã nhập điểm" value={data.totalGradesPosted} icon="✅" color="purple" />
        <StatCard label="Chờ nhập điểm" value={data.totalGradesPending} icon="⏳" color="orange" />
        <StatCard label="GPA trung bình" value={data.averageClassGPA?.toFixed(2)} icon="⭐" color="blue" />
        <StatCard label="Trượt môn" value={data.totalFailedStudents} icon="❌" color="red" />
        <StatCard label="Xuất sắc" value={data.totalExcellentStudents} icon="🏆" color="green" />
        <StatCard label="Đăng ký" value={data.totalEnrollments} icon="📋" color="gray" />
      </div>

      {/* Class size info */}
      {(data.largestClassName || data.smallestClassName) && (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-6">
          <div className="bg-white rounded-xl shadow-sm p-5">
            <p className="text-sm text-gray-500">Lớp đông nhất</p>
            <p className="font-semibold text-gray-800 mt-1">{data.largestClassName}</p>
            <p className="text-2xl font-bold text-blue-600">{data.largestClassSize} SV</p>
          </div>
          <div className="bg-white rounded-xl shadow-sm p-5">
            <p className="text-sm text-gray-500">Lớp ít nhất</p>
            <p className="font-semibold text-gray-800 mt-1">{data.smallestClassName}</p>
            <p className="text-2xl font-bold text-purple-600">{data.smallestClassSize} SV</p>
          </div>
        </div>
      )}

      {/* Classes chart */}
      {classChartData.length > 0 && (
        <div className="bg-white rounded-xl shadow-sm p-5">
          <h3 className="text-base font-semibold text-gray-700 mb-4">Sĩ số các lớp</h3>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={classChartData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" tick={{ fontSize: 12 }} />
              <YAxis />
              <Tooltip />
              <Bar dataKey="Sinh viên" fill="#2563EB" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      )}
    </div>
  )
}
