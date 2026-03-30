import { ClassStatus, SemesterName } from './common.types';

export interface SemesterInfo {
  isActive: boolean;
  name: SemesterName;
  academicYear: string;
}

export interface SubjectInfo {
  code: string;
  name: string;
  credits: number;
  departmentName: string;
}

export interface TeacherInfo {
  id: number;
  fullName: string;
  teacherCode: string;
  email: string;
}

export interface ClassResponse {
  id: number;
  classCode: string;
  semesterResponse: SemesterInfo;
  status: ClassStatus;
  subjectResponse: SubjectInfo;
  teacherResponse: TeacherInfo;
  maxStudents: number;
  currentStudents: number;
  room: string;
}

export interface ScheduleItem {
  dayOfWeek: number;
  startPeriod: number;
  endPeriod: number;
  room: string;
  startWeek: string;
  endWeek: string;
}

export interface ClassScheduleResponse {
  schedules: ScheduleItem[];
}

export interface CreateClassRequest {
  semesterId: number;
  subjectId: number;
  teacherId: number;
  classCode: string;
  maxStudents: number;
  room: string;
}

export interface UpdateClassRequest {
  semesterId: number;
  subjectId: number;
  teacherId: number;
  classCode: string;
  maxStudents: number;
  room: string;
  status: ClassStatus;
}

export interface CreateScheduleRequest {
  schedules: Array<{
    dayOfWeek: number;
    startPeriod: number;
    endPeriod: number;
    room: string;
    startWeek: string;
    endWeek: string;
  }>;
}
