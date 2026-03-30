import { createBrowserRouter, Navigate } from 'react-router-dom'
import ProtectedRoute from './ProtectedRoute'

// Auth pages
import LoginPage from '@/pages/auth/LoginPage'
import ForgotPasswordPage from '@/pages/auth/ForgotPasswordPage'
import ResetPasswordPage from '@/pages/auth/ResetPasswordPage'

// Layouts
import AdminLayout from '@/components/layout/AdminLayout'
import TeacherLayout from '@/components/layout/TeacherLayout'
import StudentLayout from '@/components/layout/StudentLayout'

// Admin pages
import AdminDashboard from '@/pages/admin/Dashboard'
import UserManagement from '@/pages/admin/UserManagement'

// Teacher pages
import TeacherDashboard from '@/pages/teacher/Dashboard'
import TeacherClasses from '@/pages/teacher/Classes'
import TeacherGrades from '@/pages/teacher/Grades'
import TeacherAttendance from '@/pages/teacher/Attendance'
import TeacherProfile from '@/pages/teacher/Profile'

// Student pages
import StudentDashboard from '@/pages/student/Dashboard'
import StudentClasses from '@/pages/student/Classes'
import StudentGrades from '@/pages/student/Grades'
import StudentPayments from '@/pages/student/Payments'
import StudentProfile from '@/pages/student/Profile'

// Shared
import NotificationsPage from '@/pages/NotificationsPage'
import UnauthorizedPage from '@/pages/UnauthorizedPage'
import RoleRedirect from '@/pages/RoleRedirect'

const router = createBrowserRouter([
  { path: '/', element: <RoleRedirect /> },
  { path: '/login', element: <LoginPage /> },
  { path: '/forgot-password', element: <ForgotPasswordPage /> },
  { path: '/reset-password', element: <ResetPasswordPage /> },
  { path: '/unauthorized', element: <UnauthorizedPage /> },

  // Admin routes
  {
    element: <ProtectedRoute allowedRoles={['ROLE_ADMIN']} />,
    children: [
      {
        element: <AdminLayout />,
        children: [
          { path: '/admin/dashboard', element: <AdminDashboard /> },
          { path: '/admin/users', element: <UserManagement /> },
          { path: '/admin/notifications', element: <NotificationsPage /> },
        ],
      },
    ],
  },

  // Teacher routes
  {
    element: <ProtectedRoute allowedRoles={['ROLE_TEACHER']} />,
    children: [
      {
        element: <TeacherLayout />,
        children: [
          { path: '/teacher/dashboard', element: <TeacherDashboard /> },
          { path: '/teacher/classes', element: <TeacherClasses /> },
          { path: '/teacher/grades', element: <TeacherGrades /> },
          { path: '/teacher/attendance', element: <TeacherAttendance /> },
          { path: '/teacher/profile', element: <TeacherProfile /> },
          { path: '/teacher/notifications', element: <NotificationsPage /> },
        ],
      },
    ],
  },

  // Student routes
  {
    element: <ProtectedRoute allowedRoles={['ROLE_STUDENT']} />,
    children: [
      {
        element: <StudentLayout />,
        children: [
          { path: '/student/dashboard', element: <StudentDashboard /> },
          { path: '/student/classes', element: <StudentClasses /> },
          { path: '/student/grades', element: <StudentGrades /> },
          { path: '/student/payments', element: <StudentPayments /> },
          { path: '/student/profile', element: <StudentProfile /> },
          { path: '/student/notifications', element: <NotificationsPage /> },
        ],
      },
    ],
  },
])

export default router
