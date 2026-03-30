import React, { Suspense } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Spin } from 'antd';
import { useAuthStore } from '../stores/authStore';
import { Role } from '../types/auth.types';
import ProtectedRoute from '../components/common/ProtectedRoute';
import AppLayout from '../components/layout/AppLayout';

// Auth pages
import LoginPage from '../pages/auth/LoginPage';
import ForgotPasswordPage from '../pages/auth/ForgotPasswordPage';

// MoMo return
import MomoReturnPage from '../pages/MomoReturnPage';

// Lazy-loaded pages — Student
const StudentDashboard = React.lazy(() => import('../pages/student/DashboardPage'));
const StudentProfilePage = React.lazy(() => import('../pages/student/ProfilePage'));
const StudentGradesPage = React.lazy(() => import('../pages/student/GradesPage'));
const StudentGradeDetailPage = React.lazy(() => import('../pages/student/GradeDetailPage'));
const StudentSchedulePage = React.lazy(() => import('../pages/student/SchedulePage'));
const EnrollmentPage = React.lazy(() => import('../pages/student/EnrollmentPage'));
const StudentTuitionPage = React.lazy(() => import('../pages/student/TuitionPage'));
const StudentPaymentsPage = React.lazy(() => import('../pages/student/PaymentsPage'));
const ProgramProgressPage = React.lazy(() => import('../pages/student/ProgramProgressPage'));
const StudentNotificationsPage = React.lazy(() => import('../pages/student/NotificationsPage'));

// Lazy-loaded pages — Teacher
const TeacherDashboard = React.lazy(() => import('../pages/teacher/DashboardPage'));
const TeacherProfilePage = React.lazy(() => import('../pages/teacher/ProfilePage'));
const TeacherClassesPage = React.lazy(() => import('../pages/teacher/ClassesPage'));
const TeacherClassDetailPage = React.lazy(() => import('../pages/teacher/ClassDetailPage'));
const TeacherNotificationsPage = React.lazy(() => import('../pages/teacher/NotificationsPage'));

// Lazy-loaded pages — Admin
const AdminDashboard = React.lazy(() => import('../pages/admin/DashboardPage'));
const AdminStudentsPage = React.lazy(() => import('../pages/admin/StudentsPage'));
const AdminStudentDetailPage = React.lazy(() => import('../pages/admin/StudentDetailPage'));
const AdminTeachersPage = React.lazy(() => import('../pages/admin/TeachersPage'));
const AdminTeacherDetailPage = React.lazy(() => import('../pages/admin/TeacherDetailPage'));
const AdminUsersPage = React.lazy(() => import('../pages/admin/UsersPage'));
const AdminClassesPage = React.lazy(() => import('../pages/admin/ClassesPage'));
const AdminClassDetailPage = React.lazy(() => import('../pages/admin/ClassDetailPage'));
const AdminSubjectsPage = React.lazy(() => import('../pages/admin/SubjectsPage'));
const AdminDepartmentsPage = React.lazy(() => import('../pages/admin/DepartmentsPage'));
const AdminProgramsPage = React.lazy(() => import('../pages/admin/ProgramsPage'));
const AdminSemestersPage = React.lazy(() => import('../pages/admin/SemestersPage'));
const AdminAcademicYearsPage = React.lazy(() => import('../pages/admin/AcademicYearsPage'));
const AdminTuitionPage = React.lazy(() => import('../pages/admin/TuitionPage'));
const AdminPaymentsPage = React.lazy(() => import('../pages/admin/PaymentsPage'));
const AdminNotificationsPage = React.lazy(() => import('../pages/admin/NotificationsPage'));

const PageFallback = () => (
  <div className="flex justify-center items-center" style={{ minHeight: 300 }}>
    <Spin size="large" />
  </div>
);

export default function AppRouter() {
  const { isAuthenticated, role } = useAuthStore();

  const rootRedirect = !isAuthenticated
    ? '/login'
    : role === Role.ADMIN
    ? '/admin/dashboard'
    : role === Role.TEACHER
    ? '/teacher/dashboard'
    : '/student/dashboard';

  return (
    <Suspense fallback={<PageFallback />}>
      <Routes>
        {/* Public routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />
        <Route path="/payment/momo/return" element={<MomoReturnPage />} />

        {/* Root redirect */}
        <Route path="/" element={<Navigate to={rootRedirect} replace />} />

        {/* Student routes */}
        <Route
          path="/student/*"
          element={
            <ProtectedRoute allowedRole={Role.STUDENT}>
              <AppLayout role={Role.STUDENT} />
            </ProtectedRoute>
          }
        >
          <Route path="dashboard" element={<StudentDashboard />} />
          <Route path="profile" element={<StudentProfilePage />} />
          <Route path="grades" element={<StudentGradesPage />} />
          <Route path="grades/:classId" element={<StudentGradeDetailPage />} />
          <Route path="schedule" element={<StudentSchedulePage />} />
          <Route path="enrollment" element={<EnrollmentPage />} />
          <Route path="tuition" element={<StudentTuitionPage />} />
          <Route path="payments" element={<StudentPaymentsPage />} />
          <Route path="program-progress" element={<ProgramProgressPage />} />
          <Route path="notifications" element={<StudentNotificationsPage />} />
          <Route path="*" element={<Navigate to="dashboard" replace />} />
        </Route>

        {/* Teacher routes */}
        <Route
          path="/teacher/*"
          element={
            <ProtectedRoute allowedRole={Role.TEACHER}>
              <AppLayout role={Role.TEACHER} />
            </ProtectedRoute>
          }
        >
          <Route path="dashboard" element={<TeacherDashboard />} />
          <Route path="profile" element={<TeacherProfilePage />} />
          <Route path="classes" element={<TeacherClassesPage />} />
          <Route path="classes/:classId" element={<TeacherClassDetailPage />} />
          <Route path="notifications" element={<TeacherNotificationsPage />} />
          <Route path="*" element={<Navigate to="dashboard" replace />} />
        </Route>

        {/* Admin routes */}
        <Route
          path="/admin/*"
          element={
            <ProtectedRoute allowedRole={Role.ADMIN}>
              <AppLayout role={Role.ADMIN} />
            </ProtectedRoute>
          }
        >
          <Route path="dashboard" element={<AdminDashboard />} />
          <Route path="students" element={<AdminStudentsPage />} />
          <Route path="students/:studentId" element={<AdminStudentDetailPage />} />
          <Route path="teachers" element={<AdminTeachersPage />} />
          <Route path="teachers/:teacherId" element={<AdminTeacherDetailPage />} />
          <Route path="users" element={<AdminUsersPage />} />
          <Route path="classes" element={<AdminClassesPage />} />
          <Route path="classes/:classId" element={<AdminClassDetailPage />} />
          <Route path="subjects" element={<AdminSubjectsPage />} />
          <Route path="departments" element={<AdminDepartmentsPage />} />
          <Route path="programs" element={<AdminProgramsPage />} />
          <Route path="semesters" element={<AdminSemestersPage />} />
          <Route path="academic-years" element={<AdminAcademicYearsPage />} />
          <Route path="tuition" element={<AdminTuitionPage />} />
          <Route path="payments" element={<AdminPaymentsPage />} />
          <Route path="notifications" element={<AdminNotificationsPage />} />
          <Route path="*" element={<Navigate to="dashboard" replace />} />
        </Route>

        {/* Catch-all fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Suspense>
  );
}
