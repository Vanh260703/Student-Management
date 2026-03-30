import useAuthStore from '@/store/authStore'
import NotificationBell from './NotificationBell'

export default function Header({ onMenuClick }) {
  const { user } = useAuthStore()

  const roleLabel = {
    ROLE_ADMIN: 'Quản trị viên',
    ROLE_TEACHER: 'Giảng viên',
    ROLE_STUDENT: 'Sinh viên',
  }

  return (
    <header className="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-4 md:px-6 sticky top-0 z-40">
      {/* Mobile menu button */}
      <button
        onClick={onMenuClick}
        className="md:hidden p-2 rounded-lg hover:bg-gray-100"
        aria-label="Toggle menu"
      >
        <span className="text-xl">☰</span>
      </button>

      <div className="hidden md:block" />

      {/* Right side */}
      <div className="flex items-center gap-3">
        <NotificationBell />

        <div className="flex items-center gap-2">
          <div className="w-9 h-9 rounded-full bg-blue-600 flex items-center justify-center overflow-hidden">
            {user?.avatarUrl ? (
              <img src={user.avatarUrl} alt={user.fullName} className="w-full h-full object-cover" />
            ) : (
              <span className="text-white text-sm font-semibold">
                {user?.fullName?.[0]?.toUpperCase() ?? 'U'}
              </span>
            )}
          </div>
          <div className="hidden sm:block">
            <p className="text-sm font-medium text-gray-800 leading-tight">{user?.fullName}</p>
            <p className="text-xs text-gray-400">{roleLabel[user?.role]}</p>
          </div>
        </div>
      </div>
    </header>
  )
}
