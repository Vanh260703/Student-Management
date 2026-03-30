import { AttendanceStatus, Gender, GradeComponentType } from './common.types';
import { ClassResponse } from './class.types';

export interface TeacherProfile {
  id: number;
  teacherCode: string;
  fullName: string;
  email: string;
  personalEmail: string;
  phone: string;
  avatarUrl: string;
  gender: Gender;
  dateOfBirth: string;
  department: string;
  degree: string;
  joinedDate: string;
  isActive: boolean;
}

export interface TeacherDashboardResponse {
  teacherInfo: TeacherProfile;
  totalClasses: number;
  totalStudents: number;
  classes: ClassResponse[];
  totalGradesPosted: number;
  totalGradesPending: number;
  totalEnrollments: number;
  totalAttendanceRecords: number;
  averageAttendanceRate: number;
  averageClassGPA: number;
  totalFailedStudents: number;
  totalExcellentStudents: number;
  largestClassName: string;
  largestClassSize: number;
  smallestClassName: string;
  smallestClassSize: number;
  lastUpdated: string;
  departmentName: string;
}

export interface AttendanceStudentRecord {
  enrollmentId: number;
  studentCode: string;
  name: string;
  status: AttendanceStatus;
}

export interface AttendanceResponse {
  classId: number;
  date: string;
  students: AttendanceStudentRecord[];
}

export interface GradeComponentResponse {
  classId: number;
  id: number;
  type: GradeComponentType;
  weight: number;
  name: string;
  maxScore: number;
}

export interface StudentGradeRecord {
  enrollmentId: number;
  studentCode: string;
  name: string;
  grades: Record<GradeComponentType, number>;
}

export interface ClassGradesResponse {
  classId: number;
  students: StudentGradeRecord[];
}

export interface StudentGradeResponse {
  enrollmentId: number;
  studentCode: string;
  name: string;
  grades: Record<GradeComponentType, number>;
}

export interface CreateTeacherRequest {
  fullName: string;
  personalEmail: string;
  phone: string;
  avatarUrl?: string;
  teacherCode: string;
  departmentId: number;
  degree: string;
  address: string;
}

export interface UpdateTeacherProfileRequest {
  fullName: string;
  phone: string;
  personalEmail: string;
  dayOfBirth: string;
  address: string;
  gender: Gender;
}
