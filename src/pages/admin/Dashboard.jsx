import { useEffect, useState } from 'react'
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend,
} from 'recharts'
import { dashboardApi } from '@/api/dashboard'
import StatCard from '@/components/common/StatCard'
import LoadingSpinner from '@/components/common/LoadingSpinner'
import dayjs from 'dayjs'

const COLORS = ['#2563EB', '#7C3AED', '#10B981', '#F59E0B', '#EF4444']

export default function AdminDashboard() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    dashboardApi.getAdminDashboard()
      .then((res) => setData(res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <LoadingSpinner />

  if (!data) {
    return (
      <div className="text-center py-16 text-gray-400">
        Không thể tải dữ liệu dashboard
      </div>
    )
  }

  const userStats = [
    { name: 'Sinh viên', value: data.totalStudents ?? 0 },
    { name: 'Giảng viên', value: data.totalTeachers ?? 0 },
    { name: 'Quản trị', value: data.totalAdmins ?? 0 },
  ]

  const classStats = [
    { name: 'Đang mở', value: data.totalOpenClasses ?? 0 },
    { name: 'Đã đóng', value: data.totalClosedClasses ?? 0 },
  ]

  const paymentStats = [
    { name: 'Đã thanh toán', value: data.totalPaidPayments ?? 0 },
    { name: 'Chờ thanh toán', value: data.totalPendingPayments ?? 0 },
  ]

  return (
    <div>
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-gray-800">Admin Dashboard</h2>
        <p className="text-sm text-gray-500 mt-1">
          Cập nhật lúc: {data.lastUpdated ? dayjs(data.lastUpdated).format('HH:mm DD/MM/YYYY') : '—'}
          &nbsp;•&nbsp;
          Hệ thống: <span className="text-green-600 font-medium">{data.systemStatus ?? 'OK'}</span>
        </p>
      </div>

      {/* Stats grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <StatCard label="Tổng sinh viên" value={data.totalStudents} icon="🎓" color="blue" />
        <StatCard label="Tổng giảng viên" value={data.totalTeachers} icon="👨‍🏫" color="purple" />
        <StatCard label="Tổng lớp học" value={data.totalClasses} icon="🏫" color="green" />
        <StatCard label="Tổng đăng ký" value={data.totalEnrollments} icon="📋" color="orange" />
        <StatCard label="Môn học" value={data.totalSubjects} icon="📚" color="blue" />
        <StatCard label="Chương trình" value={data.totalPrograms} icon="🎯" color="purple" />
        <StatCard label="GPA trung bình" value={data.averageGPA?.toFixed(2)} icon="⭐" color="orange" />
        <StatCard label="Trượt môn" value={data.totalFailedGrades} icon="❌" color="red" />
      </div>

      {/* Tuition */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
        <StatCard
          label="Tổng học phí thu"
          value={data.totalTuitionCollected?.toLocaleString('vi-VN') + ' đ'}
          icon="💰"
          color="green"
        />
        <StatCard
          label="Học phí chờ thu"
          value={data.totalTuitionPending?.toLocaleString('vi-VN') + ' đ'}
          icon="⏳"
          color="orange"
        />
        <StatCard
          label="Thông báo chưa đọc"
          value={data.totalUnreadNotifications}
          icon="🔔"
          color="red"
        />
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* User distribution pie */}
        <div className="bg-white rounded-xl shadow-sm p-5">
          <h3 className="text-base font-semibold text-gray-700 mb-4">Phân bố người dùng</h3>
          <ResponsiveContainer width="100%" height={220}>
            <PieChart>
              <Pie data={userStats} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={80} label>
                {userStats.map((_, i) => (
                  <Cell key={i} fill={COLORS[i % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>

        {/* Class status bar */}
        <div className="bg-white rounded-xl shadow-sm p-5">
          <h3 className="text-base font-semibold text-gray-700 mb-4">Trạng thái lớp học</h3>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={classStats}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="value" fill="#2563EB" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Payment status pie */}
        <div className="bg-white rounded-xl shadow-sm p-5">
          <h3 className="text-base font-semibold text-gray-700 mb-4">Tình trạng thanh toán</h3>
          <ResponsiveContainer width="100%" height={220}>
            <PieChart>
              <Pie data={paymentStats} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={80} label>
                <Cell fill="#10B981" />
                <Cell fill="#F59E0B" />
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  )
}
