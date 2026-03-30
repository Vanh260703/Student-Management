import { Gender, GradeComponentType, LetterGrade, StudentStatus, TuitionStatus } from './common.types';
import { SemesterInfo } from './class.types';

export interface StudentProfile {
  id: number;
  studentCode: string;
  departmentId: number;
  programId: number;
  className: string;
  fullName: string;
  schoolEmail: string;
  personalEmail: string;
  dayOfBirth: string;
  gender: Gender;
  address: string;
  avatarUrl: string;
  gpa: number;
  accumulatedCredits: number;
  enrollmentYear: number;
  status: StudentStatus;
  phone: string;
}

export interface StudentTuitionInfo {
  id: number;
  semester: SemesterInfo;
  amount: number;
  discount: number;
  finalAmount: number;
  dueDate: string;
  status: TuitionStatus;
  createdAt: string;
}

export interface RecentGrade {
  subjectName: string;
  score: number;
  letterGrade: LetterGrade;
}

export interface UpcomingClass {
  nextClassName: string;
  nextClassRoom: string;
  nextClassTime: string;
}

export interface StudentDashboardResponse {
  studentInfo: StudentProfile;
  totalEnrolledClasses: number;
  totalCompletedCredits: number;
  currentGPA: number;
  studentStatus: StudentStatus;
  averageScore: number;
  totalPassedSubjects: number;
  totalFailedSubjects: number;
  recentGrades: RecentGrade[];
  attendanceRate: number;
  totalAbsentDays: number;
  totalLateArrivals: number;
  tuitionInfo: StudentTuitionInfo | null;
  totalTuitionFee: number;
  paidAmount: number;
  remainingAmount: number;
  tuitionStatus: TuitionStatus | null;
  upcomingClasses: UpcomingClass[];
  programName: string;
  departmentName: string;
  enrollmentYear: number;
  lastUpdated: string;
}

export interface UpdateProfileRequest {
  fullName: string;
  phone: string;
  personalEmail: string;
  dayOfBirth: string;
  address: string;
  gender: Gender;
}

export interface GradeCourse {
  classId: number;
  classCode: string;
  subjectName: string;
  credits: number;
  grades: Record<GradeComponentType, number>;
  finalScore: number | null;
  finalLetterGrade: LetterGrade | null;
  isPassed: boolean | null;
  isPublished: boolean;
}

export interface GradeSemester {
  semesterId: number;
  semesterName: string;
  courses: GradeCourse[];
}

export interface GradeSummary {
  gpa: number;
  totalCredits: number;
  passedCredits: number;
}

export interface AllGradeStudent {
  studentInfo: {
    studentCode: string;
    name: string;
  };
  semesters: GradeSemester[];
  summary: GradeSummary;
}

export interface ScheduleClass {
  classId: number;
  classCode: string;
  subjectName: string;
  room: string;
  teacherName: string;
  startPeriod: number;
  endPeriod: number;
}

export interface ScheduleDay {
  dayOfWeek: number;
  classes: ScheduleClass[];
}

export interface StudentTimetableResponse {
  schedule: ScheduleDay[];
}

export interface ProgramSubjectItem {
  id: number;
  code: string;
  name: string;
  credits: number;
  isRequired: boolean;
  prerequisiteSubject: string | null;
}

export interface ProgramSemesterGroup {
  semester: number;
  subjects: ProgramSubjectItem[];
}

export interface ProgramResponse {
  id: number;
  code: string;
  name: string;
  totalCredits: number;
  durationYears: number;
  subjectBySemester: ProgramSemesterGroup[];
}
