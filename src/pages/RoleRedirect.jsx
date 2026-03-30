import { Navigate } from 'react-router-dom'
import useAuthStore from '@/store/authStore'

const roleHome = {
  ROLE_ADMIN: '/admin/dashboard',
  ROLE_TEACHER: '/teacher/dashboard',
  ROLE_STUDENT: '/student/dashboard',
}

export default function RoleRedirect() {
  const { isAuthenticated, user } = useAuthStore()
  if (!isAuthenticated) return <Navigate to="/login" replace />
  return <Navigate to={roleHome[user?.role] ?? '/login'} replace />
}
