import { NavLink, useNavigate } from 'react-router-dom'
import { toast } from 'react-toastify'
import useAuthStore from '@/store/authStore'

const adminNav = [
  { label: 'Dashboard', icon: '📊', to: '/admin/dashboard' },
  { label: 'Người dùng', icon: '👥', to: '/admin/users' },
  { label: 'Thông báo', icon: '🔔', to: '/admin/notifications' },
]

const teacherNav = [
  { label: 'Dashboard', icon: '📊', to: '/teacher/dashboard' },
  { label: 'Lớp học', icon: '🏫', to: '/teacher/classes' },
  { label: 'Điểm số', icon: '📝', to: '/teacher/grades' },
  { label: 'Điểm danh', icon: '✅', to: '/teacher/attendance' },
  { label: 'Hồ sơ', icon: '👤', to: '/teacher/profile' },
  { label: 'Thông báo', icon: '🔔', to: '/teacher/notifications' },
]

const studentNav = [
  { label: 'Dashboard', icon: '📊', to: '/student/dashboard' },
  { label: 'Lớp học', icon: '🏫', to: '/student/classes' },
  { label: 'Điểm số', icon: '📝', to: '/student/grades' },
  { label: 'Học phí', icon: '💳', to: '/student/payments' },
  { label: 'Hồ sơ', icon: '👤', to: '/student/profile' },
  { label: 'Thông báo', icon: '🔔', to: '/student/notifications' },
]

const navByRole = {
  ROLE_ADMIN: adminNav,
  ROLE_TEACHER: teacherNav,
  ROLE_STUDENT: studentNav,
}

export default function Sidebar({ onClose }) {
  const { user, logout } = useAuthStore()
  const navigate = useNavigate()
  const navItems = navByRole[user?.role] ?? []

  const handleLogout = () => {
    logout()
    toast.info('Đã đăng xuất')
    navigate('/login')
  }

  return (
    <div className="flex flex-col h-full bg-gray-900 text-white w-64">
      {/* Logo */}
      <div className="px-6 py-5 border-b border-gray-700">
        <h1 className="text-lg font-bold text-white">🎓 Quản Lý Sinh Viên</h1>
        <p className="text-xs text-gray-400 mt-1 truncate">{user?.fullName}</p>
      </div>

      {/* Nav items */}
      <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            onClick={onClose}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-2.5 rounded-lg text-sm font-medium transition ${
                isActive
                  ? 'bg-blue-600 text-white'
                  : 'text-gray-300 hover:bg-gray-700 hover:text-white'
              }`
            }
          >
            <span>{item.icon}</span>
            {item.label}
          </NavLink>
        ))}
      </nav>

      {/* Logout */}
      <div className="px-3 py-4 border-t border-gray-700">
        <button
          onClick={handleLogout}
          className="flex items-center gap-3 px-4 py-2.5 w-full rounded-lg text-sm text-gray-300 hover:bg-red-600 hover:text-white transition"
        >
          <span>🚪</span>
          Đăng xuất
        </button>
      </div>
    </div>
  )
}
