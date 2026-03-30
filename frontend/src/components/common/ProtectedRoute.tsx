import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../../stores/authStore';
import { Role } from '../../types/auth.types';

interface Props {
  children: React.ReactNode;
  allowedRole: Role;
}

export default function ProtectedRoute({ children, allowedRole }: Props) {
  const { isAuthenticated, role } = useAuthStore();

  if (!isAuthenticated) return <Navigate to="/login" replace />;

  if (role !== allowedRole) {
    if (role === Role.ADMIN) return <Navigate to="/admin/dashboard" replace />;
    if (role === Role.TEACHER) return <Navigate to="/teacher/dashboard" replace />;
    if (role === Role.STUDENT) return <Navigate to="/student/dashboard" replace />;
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}
