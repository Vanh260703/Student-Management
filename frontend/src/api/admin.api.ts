import { axiosPrivate } from './axiosInstance';
import { UserInfo } from '../types/auth.types';
import { StudentProfile } from '../types/student.types';
import { TeacherProfile, CreateTeacherRequest } from '../types/teacher.types';
import { ClassResponse, UpdateClassRequest } from '../types/class.types';
import {
  TuitionResponse,
  UpdateTuitionRequest,
  GenerateTuitionResponse,
  AdminPaymentResponse,
} from '../types/tuition.types';
import {
  BroadcastNotificationRequest,
  SendNotificationRequest,
} from '../types/notification.types';
import { EnrollmentStatus, PaymentStatus, StudentStatus } from '../types/common.types';

export interface AdminDashboardResponse {
  totalStudents: number;
  totalTeachers: number;
  totalAdmins: number;
  totalActiveUsers: number;
  totalInactiveUsers: number;
  totalClasses: number;
  totalOpenClasses: number;
  totalClosedClasses: number;
  totalEnrollments: number;
  totalSubjects: number;
  totalPrograms: number;
  totalDepartments: number;
  totalSemesters: number;
  totalTuitionCollected: number;
  totalTuitionPending: number;
  totalPaymentTransactions: number;
  totalPaidPayments: number;
  totalPendingPayments: number;
  totalNotificationsSent: number;
  totalUnreadNotifications: number;
  averageStudentsPerClass: number;
  averageGPA: number;
  totalFailedGrades: number;
  lastUpdated: string;
  systemStatus: string;
}

export interface CreateStudentRequest {
  fullName: string;
  personalEmail: string;
  phone: string;
  avatarUrl?: string;
  departmentId: number;
  programId: number;
  dayOfBirth: string;
  address: string;
  gender: string;
  className: string;
}

export interface UpdateStudentRequest {
  fullName: string;
  phone: string;
  personalEmail: string;
  dayOfBirth: string;
  address: string;
  gender: string;
  isActive: boolean;
  enrollmentYear: number;
  className: string;
  status: StudentStatus;
}

export interface UpdateStatusEnrollment {
  status: EnrollmentStatus;
}

export const adminApi = {
  getDashboard: async (): Promise<AdminDashboardResponse> => {
    const res = await axiosPrivate.get('/api/v2/admin/dashboard');
    return res.data.result;
  },

  // Students
  getStudents: async (params?: {
    search?: string;
    departmentId?: number;
    programId?: number;
    enrollmentYear?: number;
    status?: StudentStatus;
    gpaMin?: number;
    gpaMax?: number;
  }): Promise<StudentProfile[]> => {
    const res = await axiosPrivate.get('/api/v2/admin/students', { params });
    return res.data.result;
  },

  getStudent: async (id: number): Promise<StudentProfile> => {
    const res = await axiosPrivate.get(`/api/v2/admin/students/${id}`);
    return res.data.result;
  },

  createStudent: async (data: CreateStudentRequest): Promise<StudentProfile> => {
    const res = await axiosPrivate.post('/api/v2/admin/students', data);
    return res.data.result;
  },

  updateStudent: async (id: number, data: UpdateStudentRequest): Promise<StudentProfile> => {
    const res = await axiosPrivate.put(`/api/v2/admin/students/${id}`, data);
    return res.data.result;
  },

  bulkImportStudents: async (file: File): Promise<void> => {
    const formData = new FormData();
    formData.append('file', file);
    await axiosPrivate.post('/api/v2/admin/students/bulk-import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  // Teachers
  getTeachers: async (params?: {
    search?: string;
    departmentId?: number;
    degree?: string;
  }): Promise<TeacherProfile[]> => {
    const res = await axiosPrivate.get('/api/v2/admin/teachers', { params });
    return res.data.result;
  },

  getTeacher: async (id: number): Promise<TeacherProfile> => {
    const res = await axiosPrivate.get(`/api/v2/admin/teachers/${id}`);
    return res.data.result;
  },

  createTeacher: async (data: CreateTeacherRequest): Promise<TeacherProfile> => {
    const res = await axiosPrivate.post('/api/v2/admin/teachers', data);
    return res.data.result;
  },

  bulkImportTeachers: async (file: File): Promise<void> => {
    const formData = new FormData();
    formData.append('file', file);
    await axiosPrivate.post('/api/v2/admin/teachers/bulk-import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  // Users
  getUsers: async (params?: { search?: string; role?: string }): Promise<UserInfo[]> => {
    const res = await axiosPrivate.get('/api/v2/admin/users', { params });
    return res.data.result;
  },

  getUser: async (id: number): Promise<UserInfo> => {
    const res = await axiosPrivate.get(`/api/v2/admin/users/${id}`);
    return res.data.result;
  },

  updateUser: async (
    id: number,
    data: { fullName: string; phone: string; avatarUrl?: string },
  ): Promise<UserInfo> => {
    const res = await axiosPrivate.put(`/api/v2/admin/users/${id}`, data);
    return res.data.result;
  },

  toggleUserActive: async (id: number): Promise<void> => {
    await axiosPrivate.patch(`/api/v2/admin/users/${id}`);
  },

  deleteUser: async (id: number): Promise<void> => {
    await axiosPrivate.delete(`/api/v2/admin/users/${id}`);
  },

  // Payments
  getPayments: async (params?: {
    status?: PaymentStatus;
    semesterId?: number;
    studentId?: number;
    fromDate?: string;
    endDate?: string;
  }): Promise<AdminPaymentResponse[]> => {
    const res = await axiosPrivate.get('/api/v2/admin/payments', { params });
    return res.data.result;
  },

  // Tuition
  generateTuition: async (): Promise<GenerateTuitionResponse> => {
    const res = await axiosPrivate.post('/api/v2/admin/tuition/generate');
    return res.data.result;
  },

  getTuitions: async (): Promise<TuitionResponse[]> => {
    const res = await axiosPrivate.get('/api/v2/admin/tuition');
    return res.data.result;
  },

  updateTuition: async (id: number, data: UpdateTuitionRequest): Promise<TuitionResponse> => {
    const res = await axiosPrivate.put(`/api/v2/admin/tuition/${id}`, data);
    return res.data.result;
  },

  // Classes
  updateClass: async (classId: number, data: UpdateClassRequest): Promise<ClassResponse> => {
    const res = await axiosPrivate.put(`/api/v2/admin/classes/${classId}`, data);
    return res.data.result;
  },

  // Enrollments
  updateEnrollmentStatus: async (
    enrollmentId: number,
    data: UpdateStatusEnrollment,
  ): Promise<void> => {
    await axiosPrivate.get(`/api/v2/admin/enrollments/${enrollmentId}`, {
      params: data,
    });
  },

  // Notifications
  broadcastNotification: async (
    data: BroadcastNotificationRequest,
  ): Promise<number> => {
    const res = await axiosPrivate.post('/api/v2/admin/notifications/broadcast', data);
    return res.data.result;
  },

  sendNotification: async (data: SendNotificationRequest): Promise<number> => {
    const res = await axiosPrivate.post('/api/v2/admin/notifications/send', data);
    return res.data.result;
  },
};
