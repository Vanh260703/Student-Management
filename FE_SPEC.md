# FE_SPEC — Hệ Thống Quản Lý Sinh Viên v2

> Tài liệu này mô tả đầy đủ toàn bộ specification của Frontend dựa trên Backend API.
> Backend chạy tại: `http://localhost:8080`
> Frontend chạy tại: `http://localhost:5173`

---

## MỤC LỤC

1. [Tech Stack & Cấu trúc dự án](#1-tech-stack--cấu-trúc-dự-án)
2. [Authentication & Authorization Flow](#2-authentication--authorization-flow)
3. [HTTP Client Setup](#3-http-client-setup)
4. [Data Types & Enums](#4-data-types--enums)
5. [API Endpoints chi tiết](#5-api-endpoints-chi-tiết)
6. [Pages & Routes](#6-pages--routes)
7. [Error Handling](#7-error-handling)
8. [Khởi tạo dự án](#8-khởi-tạo-dự-án)
9. [Design System & UI](#9-design-system--ui)
10. [Layout & Navigation](#10-layout--navigation)
11. [Patterns & Conventions](#11-patterns--conventions)
12. [Docker — Chạy toàn bộ dự án](#12-docker--chạy-toàn-bộ-dự-án)

---

## 1. Tech Stack & Cấu trúc dự án

### Tech Stack (cố định, không được thay đổi)

| Mục | Công nghệ |
|-----|-----------|
| Framework | React 18 + TypeScript |
| Build tool | Vite |
| Routing | React Router v6 |
| State management | Zustand |
| HTTP client | Axios |
| UI library | Ant Design (antd) v5 |
| Styling | Tailwind CSS |
| Form | React Hook Form + Zod |
| Icons | Ant Design Icons (`@ant-design/icons`) |
| Notification/Toast | Ant Design `message` và `notification` |
| Date | Day.js (đã có sẵn trong antd) |
| Language | Tiếng Việt toàn bộ UI |

### Cấu trúc thư mục

```
src/
├── api/
│   ├── axiosInstance.ts       # 2 axios instances: axiosPublic, axiosPrivate
│   ├── auth.api.ts
│   ├── student.api.ts
│   ├── teacher.api.ts
│   ├── admin.api.ts
│   ├── class.api.ts
│   ├── enrollment.api.ts
│   ├── notification.api.ts
│   ├── payment.api.ts
│   ├── subject.api.ts
│   ├── department.api.ts
│   ├── program.api.ts
│   ├── semester.api.ts
│   └── academicYear.api.ts
├── types/
│   ├── auth.types.ts
│   ├── student.types.ts
│   ├── teacher.types.ts
│   ├── class.types.ts
│   ├── enrollment.types.ts
│   ├── grade.types.ts
│   ├── tuition.types.ts
│   ├── notification.types.ts
│   └── common.types.ts
├── stores/
│   └── authStore.ts           # Zustand store cho auth
├── hooks/
│   ├── useAuth.ts             # Hook lấy state từ authStore
│   └── useNotification.ts     # Hook lấy unread count
├── pages/
│   ├── auth/
│   │   ├── LoginPage.tsx
│   │   └── ForgotPasswordPage.tsx
│   ├── student/
│   │   ├── DashboardPage.tsx
│   │   ├── ProfilePage.tsx
│   │   ├── GradesPage.tsx
│   │   ├── SchedulePage.tsx
│   │   ├── EnrollmentPage.tsx
│   │   ├── TuitionPage.tsx
│   │   ├── PaymentsPage.tsx
│   │   ├── ProgramProgressPage.tsx
│   │   └── NotificationsPage.tsx
│   ├── teacher/
│   │   ├── DashboardPage.tsx
│   │   ├── ProfilePage.tsx
│   │   ├── ClassesPage.tsx
│   │   ├── ClassDetailPage.tsx  # Tab: Điểm danh | Bảng điểm
│   │   └── NotificationsPage.tsx
│   └── admin/
│       ├── DashboardPage.tsx
│       ├── StudentsPage.tsx
│       ├── StudentDetailPage.tsx
│       ├── TeachersPage.tsx
│       ├── TeacherDetailPage.tsx
│       ├── UsersPage.tsx
│       ├── ClassesPage.tsx
│       ├── ClassDetailPage.tsx
│       ├── SubjectsPage.tsx
│       ├── DepartmentsPage.tsx
│       ├── ProgramsPage.tsx
│       ├── SemestersPage.tsx
│       ├── AcademicYearsPage.tsx
│       ├── TuitionPage.tsx
│       ├── PaymentsPage.tsx
│       └── NotificationsPage.tsx
├── components/
│   ├── layout/
│   │   ├── AppLayout.tsx        # Layout chung có sidebar + header
│   │   ├── Sidebar.tsx
│   │   ├── Header.tsx
│   │   └── NotificationBell.tsx
│   └── common/
│       ├── ProtectedRoute.tsx
│       ├── LoadingScreen.tsx    # Full-screen loading khi App Init
│       ├── PageHeader.tsx
│       └── ConfirmModal.tsx
├── router/
│   └── index.tsx
├── App.tsx
└── main.tsx
```

---

## 2. Authentication & Authorization Flow

### 2.1 Tổng quan

- Backend dùng **JWT stateless**.
- **accessToken**: lưu trong memory (biến trong store, KHÔNG lưu localStorage/sessionStorage).
- **refreshToken**: lưu trong **HttpOnly cookie** — browser tự gửi kèm, FE không đọc được và không cần xử lý thủ công.
- Mỗi request cần auth phải kèm header: `Authorization: Bearer <accessToken>`

### 2.2 Flow Login

```
1. User nhập email + password → POST /api/v2/auth/login
2. Response trả về JSON:
   {
     "code": 200,
     "message": "Login success!",
     "result": {
       "email": "...",
       "role": "ROLE_ADMIN | ROLE_TEACHER | ROLE_STUDENT",
       "accessToken": "eyJ...",
       "refreshToken": "eyJ..."   ← cũng có trong JSON nhưng KHÔNG cần dùng
     }
   }
   + Set-Cookie: refreshToken=...; HttpOnly; Path=/api/v2/; SameSite=Lax (do browser tự lưu)

3. FE lưu vào store:
   - accessToken (memory only)
   - role
   - email

4. Redirect theo role:
   - ROLE_ADMIN   → /admin/dashboard
   - ROLE_TEACHER → /teacher/dashboard
   - ROLE_STUDENT → /student/dashboard
```

### 2.3 Flow Refresh Token (Silent Refresh)

```
Khi request API bị lỗi 401:
1. Tự động gọi POST /api/v2/auth/refresh
   - Browser tự gửi cookie refreshToken (HttpOnly)
   - KHÔNG cần gửi body gì cả
   - PHẢI dùng withCredentials: true

2. Response trả về accessToken mới:
   {
     "code": 200,
     "message": "Refresh token success!",
     "result": {
       "email": "...",
       "role": "...",
       "accessToken": "eyJ...",  ← lưu cái này vào store
       "refreshToken": "..."     ← bỏ qua
     }
   }

3. Retry request gốc với accessToken mới.

4. Nếu /refresh cũng lỗi (401) → clear store → redirect /login
```

### 2.4 Flow Logout

```
1. Gọi POST /api/v2/auth/logout
   - Gửi kèm cookie (withCredentials: true) để BE revoke refreshToken
2. Clear accessToken trong store
3. Redirect → /login
```

### 2.5 Khởi động ứng dụng (App Init)

```
Khi user mở lại tab/F5:
1. accessToken trong memory đã mất (reload)
2. Thử gọi POST /api/v2/auth/refresh (cookie vẫn còn)
3. Nếu thành công → lưu accessToken mới → tiếp tục
4. Nếu thất bại → redirect /login
```

> **QUAN TRỌNG**: Bước App Init phải chạy TRƯỚC khi render bất kỳ route protected nào.

### 2.6 Auth Store (Zustand example)

```typescript
interface AuthState {
  accessToken: string | null;
  role: Role | null;
  email: string | null;
  isAuthenticated: boolean;
  isInitialized: boolean;  // đã chạy App Init chưa

  setAuth: (token: string, role: Role, email: string) => void;
  clearAuth: () => void;
  setInitialized: () => void;
}
```

### 2.7 Role và quyền truy cập

| Role | Giá trị từ API | Trang được phép |
|------|---------------|-----------------|
| Admin | `ROLE_ADMIN` | `/admin/*` |
| Giáo viên | `ROLE_TEACHER` | `/teacher/*` |
| Sinh viên | `ROLE_STUDENT` | `/student/*` |

---

## 3. HTTP Client Setup

### 3.1 Axios Instance

```typescript
// api/axiosInstance.ts
import axios from 'axios';

const BASE_URL = 'http://localhost:8080';

export const axiosPublic = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,  // luôn gửi cookie
});

export const axiosPrivate = axios.create({
  baseURL: BASE_URL,
  withCredentials: true,  // luôn gửi cookie (cần cho refresh)
});
```

### 3.2 Request Interceptor (axiosPrivate)

```typescript
axiosPrivate.interceptors.request.use((config) => {
  const { accessToken } = useAuthStore.getState();
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});
```

### 3.3 Response Interceptor — Auto Refresh Token

```typescript
let isRefreshing = false;
let failedQueue: Array<{ resolve: Function; reject: Function }> = [];

axiosPrivate.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Chỉ xử lý lỗi 401 và chưa retry
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // Có request khác đang refresh → đợi
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then((token) => {
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return axiosPrivate(originalRequest);
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        // Gọi refresh — cookie tự động gửi kèm
        const res = await axiosPublic.post('/api/v2/auth/refresh');
        const newToken = res.data.result.accessToken;

        useAuthStore.getState().setAuth(
          newToken,
          res.data.result.role,
          res.data.result.email
        );

        // Retry các request đang đợi
        failedQueue.forEach(({ resolve }) => resolve(newToken));
        failedQueue = [];

        originalRequest.headers.Authorization = `Bearer ${newToken}`;
        return axiosPrivate(originalRequest);
      } catch (refreshError) {
        failedQueue.forEach(({ reject }) => reject(refreshError));
        failedQueue = [];
        useAuthStore.getState().clearAuth();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);
```

### 3.4 Cấu trúc Response từ API

Tất cả response thành công đều có dạng:

```typescript
interface APIResponse<T> {
  code: number;      // HTTP status code (200, 201, ...)
  message: string;   // Thông báo
  result: T;         // Dữ liệu thực sự (đây là phần FE cần dùng)
}
```

Lấy dữ liệu: `response.data.result` (KHÔNG phải `response.data.data`)

Tất cả response lỗi đều có dạng:

```typescript
interface ErrorResponse {
  code: number;     // HTTP status code
  message: string;  // Mô tả lỗi
}
```

Lấy lỗi: `error.response.data.message`

---

## 4. Data Types & Enums

### 4.1 Enums

```typescript
enum Role {
  ADMIN = 'ROLE_ADMIN',
  TEACHER = 'ROLE_TEACHER',
  STUDENT = 'ROLE_STUDENT',
}

enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
  OTHER = 'OTHER',
}

enum StudentStatus {
  ACTIVE = 'ACTIVE',
  SUSPENDED = 'SUSPENDED',
  GRADUATED = 'GRADUATED',
}

enum ClassStatus {
  OPEN = 'OPEN',
  CLOSE = 'CLOSE',
  CANCELLED = 'CANCELLED',
}

enum EnrollmentStatus {
  ENROLLED = 'ENROLLED',
  DROPPED = 'DROPPED',
  COMPLETED = 'COMPLETED',
}

enum PaymentStatus {
  PENDING = 'PENDING',
  SUCCESS = 'SUCCESS',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED',
}

enum PaymentMethod {
  BANK_TRANSFER = 'BANK_TRANSFER',
  MOMO = 'MOMO',
  CASH = 'CASH',
}

enum GradeComponentType {
  ATTENDANCE = 'ATTENDANCE',
  MIDTERM = 'MIDTERM',
  FINAL = 'FINAL',
  ASSIGNMENT = 'ASSIGNMENT',
}

enum LetterGrade {
  A = 'A',
  B = 'B',
  C = 'C',
  D = 'D',
  F = 'F',
}

enum NotificationType {
  GRADE = 'GRADE',
  SCHEDULE = 'SCHEDULE',
  PAYMENT = 'PAYMENT',
  SYSTEM = 'SYSTEM',
  ATTENDANCE = 'ATTENDANCE',
}

enum AttendanceStatus {
  PRESENT = 'PRESENT',
  ABSENT = 'ABSENT',
  LATE = 'LATE',
  EXCUSED = 'EXCUSED',
}

enum SemesterName {
  HK1 = 'HK1',
  HK2 = 'HK2',
  HK3 = 'HK3',
}

enum TuitionStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  OVERDUE = 'OVERDUE',
  WAIVED = 'WAIVED',
}
```

### 4.2 Interfaces

```typescript
// Auth
interface AuthResult {
  email: string;
  role: Role;
  accessToken: string;
  refreshToken: string;  // có trong JSON nhưng không cần dùng
}

interface UserInfo {
  id: number;
  email: string;
  fullName: string;
  phone: string;
  avatarUrl: string;
  role: Role;
}

// Student
interface StudentProfile {
  id: number;
  studentCode: string;
  departmentId: number;
  programId: number;
  className: string;
  fullName: string;
  schoolEmail: string;
  personalEmail: string;
  dayOfBirth: string;   // ISO date string
  gender: Gender;
  address: string;
  avatarUrl: string;
  gpa: number;
  accumulatedCredits: number;
  enrollmentYear: number;
  status: StudentStatus;
}

// Teacher
interface TeacherProfile {
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

// Class
interface ClassResponse {
  classCode: string;
  semesterResponse: SemesterInfo;
  status: ClassStatus;
  subjectResponse: SubjectInfo;
  teacherResponse: TeacherInfo;
}

interface SemesterInfo {
  isActive: boolean;
  name: SemesterName;
  academicYear: string;
}

interface SubjectInfo {
  code: string;
  name: string;
  credits: number;
  departmentName: string;
}

// Grade
interface StudentGradeResponse {
  enrollmentId: number;
  studentCode: string;
  name: string;
  grades: Record<GradeComponentType, number>;  // Map<type, score>
}

interface GradeComponentResponse {
  classId: number;
  type: GradeComponentType;
  weight: number;
  name: string;
}

// Tuition
interface StudentTuitionResponse {
  id: number;
  semester: SemesterInfo;
  amount: number;
  discount: number;
  finalAmount: number;
  dueDate: string;
  status: TuitionStatus;
  createdAt: string;
}

// Notification
interface NotificationResponse {
  id: number;
  title: string;
  content: string;
  type: NotificationType;
  isRead: boolean;
  referenceId: number | null;
  referenceType: string | null;
  createdAt: string;
}

// Payment
interface StudentPaymentHistoryResponse {
  paymentId: number;
  tuitionId: number;
  transactionCode: string;
  amount: number;
  method: PaymentMethod;
  paymentStatus: PaymentStatus;
  paidAt: string | null;
  createdAt: string;
  tuition: StudentTuitionResponse;
}

interface MomoPaymentResponse {
  paymentId: number;
  orderId: string;
  requestId: string;
  amount: number;
  status: string;
  payUrl: string;    // URL redirect sang Momo
  deeplink: string;
  qrCodeUrl: string;
}
```

---

## 5. API Endpoints chi tiết

> Convention: Tất cả request cần auth phải dùng `axiosPrivate`. Request public dùng `axiosPublic`.

---

### 5.1 Auth API

**Base URL:** `/api/v2/auth`

#### POST `/login` — Public

```typescript
// Request
{ email: string; password: string }

// Response result
AuthResult

// Ví dụ call
const login = async (email: string, password: string) => {
  const res = await axiosPublic.post('/api/v2/auth/login', { email, password });
  return res.data.result; // AuthResult
};
```

#### POST `/logout` — Auth required

```typescript
// Request: không có body, browser tự gửi cookie
// Response: không có data

const logout = async () => {
  await axiosPrivate.post('/api/v2/auth/logout');
};
```

#### POST `/refresh` — Public (dùng cookie)

```typescript
// Request: không có body, browser tự gửi cookie refreshToken
// Response result: AuthResult

const refresh = async () => {
  const res = await axiosPublic.post('/api/v2/auth/refresh');
  return res.data.result; // AuthResult
};
```

#### GET `/me` — Auth required

```typescript
// Response result: UserInfo

const getMe = async () => {
  const res = await axiosPrivate.get('/api/v2/auth/me');
  return res.data.result; // UserInfo
};
```

#### POST `/forgot-password` — Public

```typescript
// Request
{ email: string }

// Response: không có data (gửi mail)
```

#### PATCH `/change-password` — Auth required

```typescript
// Request
{
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}

// Response: không có data
```

---

### 5.2 Student API

**Base URL:** `/api/v2/student`
**Role required:** `ROLE_STUDENT`

#### GET `/profile`

```typescript
// Response result
interface StudentProfileResponse {
  id: number;
  studentCode: string;
  programId: number;
  departmentId: number;
  enrollmentYear: number;
  className: string;
  gpa: number;
  accumulatedCredits: number;
  status: StudentStatus;
  fullName: string;
  schoolEmail: string;
  personalEmail: string;
  dayOfBirth: string;
  gender: Gender;
  address: string;
  avatarUrl: string;
  phone: string;
}
```

#### PUT `/profile`

```typescript
// Request
{
  fullName: string;
  phone: string;
  personalEmail: string;
  dayOfBirth: string;   // format: YYYY-MM-DD
  address: string;
  gender: Gender;
}

// Response result: StudentProfileResponse
```

#### POST `/profile/upload-avatar`

```typescript
// Request: multipart/form-data
// field name: "file", type: image/*

const uploadAvatar = async (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  const res = await axiosPrivate.post('/api/v2/student/profile/upload-avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return res.data.result; // { avatarUrl: string }
};
```

#### GET `/grades`

```typescript
// Response result
interface AllGradeStudent {
  studentInfo: { studentCode: string; name: string };
  semesters: Array<{
    semesterId: number;
    semesterName: string;
    courses: Array<{
      classId: number;
      classCode: string;
      subjectName: string;
      credits: number;
      grades: Record<GradeComponentType, number>;
      finalScore: number | null;
      finalLetterGrade: LetterGrade | null;
      isPassed: boolean | null;
      isPublished: boolean;
    }>;
  }>;
  summary: {
    gpa: number;
    totalCredits: number;
    passedCredits: number;
  };
}
```

#### GET `/grades/{classId}`

```typescript
// Response result: StudentGradeResponse
interface StudentGradeResponse {
  enrollmentId: number;
  studentCode: string;
  name: string;
  grades: Record<GradeComponentType, number>;
}
```

#### GET `/program-progress`

```typescript
// Response result
interface ProgramResponse {
  id: number;
  code: string;
  name: string;
  totalCredits: number;
  durationYears: number;
  subjectBySemester: Array<{
    semester: number;
    subjects: Array<{
      id: number;
      code: string;
      name: string;
      credits: number;
      isRequired: boolean;
      prerequisiteSubject: string | null;
    }>;
  }>;
}
```

#### GET `/schedules`

```typescript
// Query params (tất cả optional)
interface ScheduleQuery {
  semesterId?: number;
  fromDate?: string;   // YYYY-MM-DD
  toDate?: string;     // YYYY-MM-DD
}

// Response result
interface StudentTimetableResponse {
  schedule: Array<{
    dayOfWeek: number;   // 1=Monday ... 7=Sunday
    classes: Array<{
      classId: number;
      classCode: string;
      subjectName: string;
      room: string;
      teacherName: string;
      startPeriod: number;
      endPeriod: number;
    }>;
  }>;
}
```

#### GET `/tuition`

```typescript
// Query params (optional)
{ semesterId?: number }

// Response result: StudentTuitionResponse[]
```

#### GET `/payments`

```typescript
// Query params (optional)
{ status?: PaymentStatus }

// Response result: StudentPaymentHistoryResponse[]
```

#### GET `/dashboard`

**URL:** `/api/v2/student/dashboard`

```typescript
// Response result
interface StudentDashboardResponse {
  studentInfo: StudentProfileResponse;
  totalEnrolledClasses: number;
  totalCompletedCredits: number;
  currentGPA: number;
  studentStatus: StudentStatus;
  averageScore: number;
  totalPassedSubjects: number;
  totalFailedSubjects: number;
  recentGrades: Array<{
    subjectName: string;
    score: number;
    letterGrade: LetterGrade;
  }>;
  attendanceRate: number;
  totalAbsentDays: number;
  totalLateArrivals: number;
  tuitionInfo: StudentTuitionResponse | null;
  totalTuitionFee: number;
  paidAmount: number;
  remainingAmount: number;
  tuitionStatus: TuitionStatus | null;
  upcomingClasses: Array<{
    nextClassName: string;
    nextClassRoom: string;
    nextClassTime: string;
  }>;
  programName: string;
  departmentName: string;
  enrollmentYear: number;
  lastUpdated: string;
}
```

---

### 5.3 Teacher API

**Base URL:** `/api/v2/teacher`
**Role required:** `ROLE_TEACHER`

#### GET `/profile`

```typescript
// Response result: TeacherProfileResponse
```

#### PUT `/profile`

```typescript
// Request
{
  fullName: string;
  phone: string;
  personalEmail: string;
  dayOfBirth: string;
  address: string;
  gender: Gender;
}

// Response result: TeacherProfileResponse
```

#### GET `/classes`

```typescript
// Response result: ClassResponse[]
```

#### PUT `/{classId}`

```typescript
// Request (chỉ teacher được sửa room)
{ room: string }

// Response result: ClassResponse
```

#### GET `/classes/{classId}/attendance`

```typescript
// Query params (optional)
{ date?: string }  // YYYY-MM-DD, default: today

// Response result
interface AttendanceResponse {
  classId: number;
  date: string;
  students: Array<{
    enrollmentId: number;
    studentCode: string;
    name: string;
    status: AttendanceStatus;
  }>;
}
```

#### POST `/classes/{classId}/attendace`

> **Lưu ý**: endpoint có lỗi chính tả "attendace" (thiếu chữ 'n') — phải gọi đúng như vậy.

```typescript
// Query params (optional)
{ date?: string }  // YYYY-MM-DD

// Request body: array
Array<{
  enrollmentId: number;
  status: AttendanceStatus;
}>

// Response result: AttendanceResponse
```

#### PATCH `/classes/{classId}/attendance/{attendanceId}`

```typescript
// Query params (optional)
{ date?: string }

// Request body
{ enrollmentId: number; status: AttendanceStatus }

// Response: không có data
```

#### GET `/classes/{classId}/grade-components`

```typescript
// Response result: GradeComponentResponse[]
```

#### POST `/classes/{classId}/grade-components`

```typescript
// Request
{
  weight: number;          // phần trăm (0-100)
  type: GradeComponentType;
  maxScore: number;
}

// Response result: GradeComponentResponse
```

#### PUT `/classes/{classId}/grade-components/{gradeComponentId}`

```typescript
// Request
{
  weight: number;
  maxScore: number;
}

// Response result: GradeComponentResponse
```

#### DELETE `/classes/{classId}/grade-components/{gradeComponentId}`

```typescript
// Response: không có data
```

#### GET `/classes/{classId}/grades`

```typescript
// Query params (optional)
{
  componentId?: number;
  isPublished?: boolean;
  search?: string;
}

// Response result
interface ClassGradesResponse {
  classId: number;
  students: StudentGradeResponse[];
}
```

#### POST `/classes/{classId}/grades`

```typescript
// Request
{
  enrollmentId: number;
  componentId: number;
  score: number;
}

// Response result: StudentGradeResponse
```

#### PUT `/classes/{classId}/grades/{gradeId}`

```typescript
// Request
{
  componentId: number;
  score: number;
}

// Response result: StudentGradeResponse
```

#### POST `/classes/{classId}/grades/import` — Multipart

```typescript
// Request: multipart/form-data
// field name: "file", type: .xlsx

// Response result: StudentGradeResponse[]
```

#### PATCH `/classes/{classId}/grades/publish`

```typescript
// Response result: number (số grades được publish)
```

#### GET `/dashboard`

**URL:** `/api/v2/teacher/dashboard`

```typescript
// Response result
interface TeacherDashboardResponse {
  teacherInfo: TeacherProfileResponse;
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
```

---

### 5.4 Class API

**Base URL:** `/api/v2/classes`

#### GET `/` — Public

```typescript
// Query params (tất cả optional)
{
  semesterId?: number;
  subjectId?: number;
  teacherId?: number;
  status?: ClassStatus;
  search?: string;
  hasSlot?: boolean;
}

// Response result: ClassResponse[]
```

#### GET `/{classId}` — Public

```typescript
// Response result: ClassResponse
```

#### POST `/` — ADMIN only

```typescript
// Request
{
  semesterId: number;
  subjectId: number;
  teacherId: number;
  classCode: string;
  maxStudents: number;
  room: string;
}

// Response result: ClassResponse
```

#### PATCH `/{classId}/change-status` — ADMIN only

```typescript
// Request
{ status: ClassStatus }

// Response: không có data
```

#### DELETE `/{classId}` — ADMIN only

```typescript
// Response: không có data
```

#### GET `/{classId}/student` — ADMIN/TEACHER

```typescript
// Query params (optional)
{ search?: string }

// Response result: StudentProfile[]
```

#### POST `/{classId}/schedules` — ADMIN only

```typescript
// Request
{
  schedules: Array<{
    dayOfWeek: number;     // 1=Monday...7=Sunday
    startPeriod: number;   // tiết bắt đầu
    endPeriod: number;     // tiết kết thúc
    room: string;
    startWeek: string;     // YYYY-MM-DD
    endWeek: string;       // YYYY-MM-DD
  }>;
}

// Response result
interface ClassScheduleResponse {
  schedules: Array<{
    dayOfWeek: number;
    startPeriod: number;
    endPeriod: number;
    room: string;
    startWeek: string;
    endWeek: string;
  }>;
}
```

#### GET `/{classId}/schedules` — Public

```typescript
// Response result: ClassScheduleResponse
```

---

### 5.5 Enrollment API

**Base URL:** `/api/v2/enrollments`
**Role required:** `ROLE_STUDENT`

#### GET `/available-classes`

```typescript
// Query params (tất cả optional)
{
  semesterId?: number;
  subjectId?: number;
  departmentId?: number;
  hasSlot?: boolean;      // chỉ lấy lớp còn chỗ
  notEnrolled?: boolean;  // chỉ lấy lớp chưa đăng ký
  search?: string;
}

// Response result: ClassResponse[]
```

#### POST `/{classId}` — Đăng ký lớp

```typescript
// Request: không có body

// Response: không có data
// Lỗi thường gặp:
// 400 - Đã đăng ký lớp này
// 400 - Lớp đã đầy
// 400 - Ngoài thời gian đăng ký
```

#### DELETE `/{enrollmentId}` — Huỷ đăng ký

```typescript
// Response: không có data
```

#### GET `/my`

```typescript
// Query params (optional)
{
  semesterId?: number;
  status?: EnrollmentStatus;
}

// Response result: Enrollment[]
interface Enrollment {
  id: number;
  classEntity: ClassResponse;
  enrolledAt: string;
  status: EnrollmentStatus;
  finalScore: number | null;
  finalLetterGrade: LetterGrade | null;
  isPassed: boolean | null;
}
```

#### GET `/{enrollmentId}`

```typescript
// Response result: Enrollment
```

---

### 5.6 Payment API

**Base URL:** `/api/v2/payments`

#### POST `/momo/create/{tuitionId}` — STUDENT only

```typescript
// Request: không có body

// Response result: MomoPaymentResponse
// Sau đó redirect user đến res.data.result.payUrl
```

#### GET `/momo/return` — Public (Momo callback)

```typescript
// Query params: do Momo tự gửi khi redirect về
// FE chỉ cần hiển thị trang kết quả, không cần gọi API này
// BE xử lý và response về MomoCallbackResponse
interface MomoCallbackResponse {
  orderId: string;
  requestId: string;
  transId: string;
  paymentStatus: PaymentStatus;
  resultCode: number;
  message: string;
  signatureValid: boolean;
  success: boolean;
}
```

---

### 5.7 Notification API

**Base URL:** `/api/v2/notifications`
**Role required:** Authenticated (tất cả roles)

#### GET `/`

```typescript
// Response result: NotificationResponse[]
```

#### GET `/unread`

```typescript
// Response result: NotificationResponse[]
```

#### GET `/unread/count`

```typescript
// Response result: number
```

#### GET `/{id}`

```typescript
// Response result: NotificationResponse
```

#### PUT `/{id}/mark-as-read`

```typescript
// Response result: NotificationResponse
```

#### PUT `/mark-all-as-read`

```typescript
// Response: không có data
```

#### DELETE `/{id}`

```typescript
// Response: không có data
```

---

### 5.8 Subject API

**Base URL:** `/api/v2/subjects`

#### GET `/` — Public

```typescript
// Query params (optional)
{
  departmentId?: number;
  search?: string;
  isActive?: boolean;
  credits?: number;
}

// Response result: Subject[]
interface Subject {
  id: number;
  code: string;
  name: string;
  credits: number;
  description: string;
  isActive: boolean;
  departmentName: string;
}
```

#### GET `/{subjectId}` — Public

```typescript
// Response result: Subject
```

#### POST `/` — ADMIN only

```typescript
// Request
{
  departmentId: number;
  code: string;
  name: string;
  credits: number;
  description: string;
  isActive: boolean;
}
```

#### PUT `/{subjectId}` — ADMIN only

```typescript
// Request
{
  name: string;
  description: string;
  isActive: boolean;
  credits: number;
}
```

#### DELETE `/{subjectId}` — ADMIN only

---

### 5.9 Department API

**Base URL:** `/api/v2/departments`

#### POST `/` — ADMIN only

```typescript
// Request
{
  code: string;
  name: string;
  description: string;
  headTeacherId: number;
}
```

#### GET `/{departmentId}` — Public

```typescript
// Response result: Department
interface Department {
  id: number;
  code: string;
  name: string;
  description: string;
  headTeacher: TeacherProfile | null;
}
```

#### PUT `/{departmentId}` — ADMIN only

```typescript
// Request
{
  name: string;
  description: string;
  headTeacherId: number;
}
```

#### DELETE `/{departmentId}` — ADMIN only

#### GET `/{departmentId}/teachers` — ADMIN only

```typescript
// Query params (optional)
{ search?: string; degree?: string }

// Response result: TeacherProfile[]
```

#### GET `/{departmentId}/students` — ADMIN only

```typescript
// Query params (optional)
{
  search?: string;
  programId?: number;
  enrollmentYear?: number;
  status?: StudentStatus;
}

// Response result: StudentProfile[]
```

---

### 5.10 Program API

**Base URL:** `/api/v2/programs`

#### GET `/` — Public

```typescript
// Query params (optional)
{ departmentId?: number; search?: string }

// Response result: Program[]
interface Program {
  id: number;
  code: string;
  name: string;
  totalCredits: number;
  durationYears: number;
  description: string;
  departmentId: number;
}
```

#### GET `/{programId}` — Public

#### POST `/` — ADMIN only

```typescript
// Request
{
  departmentId: number;
  code: string;
  name: string;
  totalCredits: number;
  durationYear: number;
  description: string;
}
```

#### PUT `/{programId}` — ADMIN only

```typescript
// Request
{
  name: string;
  totalCredits: number;
  description: string;
  durationYear: number;
}
```

#### DELETE `/{programId}` — ADMIN only

#### GET `/{programId}/subjects` — Public

```typescript
// Query params (optional)
{ semester?: number; isRequired?: boolean }

// Response result: ProgramSubject[]
interface ProgramSubject {
  id: number;
  subject: Subject;
  semester: number;
  isRequired: boolean;
  prerequisiteSubject: Subject | null;
}
```

#### POST `/{programId}/subjects` — ADMIN only

```typescript
// Request
{
  subjectId: number;
  semester: number;
  isRequired: boolean;
  prerequisiteSubjectId: number | null;
}
```

#### DELETE `/{programId}/subjects/{subjectId}` — ADMIN only

---

### 5.11 Semester API

**Base URL:** `/api/v2/semesters`

#### GET `/` — Public

```typescript
// Query params (optional)
{
  academicYearId?: number;
  isActive?: boolean;
  semesterNumber?: number;
}

// Response result: Semester[]
interface Semester {
  id: number;
  academicYear: AcademicYear;
  name: SemesterName;
  semesterNumber: number;
  startDate: string;
  endDate: string;
  registrationStart: string;
  registrationEnd: string;
  isActive: boolean;
}
```

#### POST `/` — ADMIN only

```typescript
// Request
{
  academicYearId: number;
  semesterNumber: number;
  startDate: string;
  endDate: string;
  registrationStart: string;
  registrationEnd: string;
  isActive: boolean;
}
```

#### PUT `/{semesterId}` — ADMIN only

```typescript
// Request
{
  startDate: string;
  endDate: string;
  registrationStart: string;
  registrationEnd: string;
}
```

#### PATCH `/{semesterId}/toggle-active` — ADMIN only

```typescript
// Response: không có data
```

---

### 5.12 Academic Year API

**Base URL:** `/api/v2/academic-years`

#### GET `/` — Public

```typescript
// Query params (optional)
{ isCurrent?: boolean }

// Response result: AcademicYear[]
interface AcademicYear {
  id: number;
  name: string;
  startDate: string;
  endDate: string;
  isCurrent: boolean;
}
```

#### POST `/` — ADMIN only

```typescript
// Request
{
  name: string;
  startDate: string;
  endDate: string;
  isCurrent: boolean;
}
```

---

### 5.13 Admin API

**Base URL:** `/api/v2/admin`
**Role required:** `ROLE_ADMIN` cho tất cả endpoints dưới đây

#### Admin Dashboard — GET `/dashboard`

```typescript
// Response result
interface AdminDashboardResponse {
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
```

#### Admin Students — `/admin/students`

| Method | Path | Request | Response |
|--------|------|---------|----------|
| GET | `/` | `?search, ?departmentId, ?programId, ?enrollmentYear, ?status, ?gpaMin, ?gpaMax` | `StudentProfile[]` |
| GET | `/{studentId}` | — | `StudentProfile` |
| POST | `/` | `CreateStudentRequest` | `StudentProfile` |
| POST | `/bulk-import` | `multipart: file (.xlsx)` | — |
| PUT | `/{studentId}` | `UpdateStudentRequest` | `StudentProfile` |

```typescript
interface CreateStudentRequest {
  fullName: string;
  personalEmail: string;
  phone: string;
  avatarUrl?: string;
  departmentId: number;
  programId: number;
  dayOfBirth: string;
  address: string;
  gender: Gender;
  className: string;
}

interface UpdateStudentRequest {
  fullName: string;
  phone: string;
  personalEmail: string;
  dayOfBirth: string;
  address: string;
  gender: Gender;
  isActive: boolean;
  enrollmentYear: number;
  className: string;
  status: StudentStatus;
}
```

#### Admin Teachers — `/admin/teachers`

| Method | Path | Request | Response |
|--------|------|---------|----------|
| GET | `/` | `?search, ?departmentId, ?degree` | `TeacherProfile[]` |
| GET | `/{teacherId}` | — | `TeacherProfile` |
| POST | `/` | `CreateTeacherRequest` | `TeacherProfile` |
| POST | `/bulk-import` | `multipart: file (.xlsx)` | — |

```typescript
interface CreateTeacherRequest {
  fullName: string;
  personalEmail: string;
  phone: string;
  avatarUrl?: string;
  teacherCode: string;
  departmentId: number;
  degree: string;
  address: string;
}
```

#### Admin Users — `/admin/users`

| Method | Path | Request | Response |
|--------|------|---------|----------|
| GET | `/` | `?search, ?role` | `UserInfo[]` |
| GET | `/{userId}` | — | `UserInfo` |
| PUT | `/{userId}` | `{ fullName, phone, avatarUrl }` | `UserInfo` |
| PATCH | `/{userId}` | — | — (toggle active/inactive) |
| DELETE | `/{userId}` | — | — |

#### Admin Payments — `/admin/payments`

| Method | Path | Request | Response |
|--------|------|---------|----------|
| GET | `/` | `?status, ?semesterId, ?studentId, ?fromDate, ?endDate` | `AdminPaymentResponse[]` |

```typescript
interface AdminPaymentResponse {
  paymentId: number;
  transactionCode: string;
  amount: number;
  method: PaymentMethod;
  status: PaymentStatus;
  paidAt: string | null;
  createdAt: string;
  student: StudentProfile;
  tuition: StudentTuitionResponse;
}
```

#### Admin Tuition — `/admin/tuition`

| Method | Path | Request | Response |
|--------|------|---------|----------|
| POST | `/generate` | — | `GenerateTuitionResponse` |
| GET | `/` | — | `TuitionResponse[]` |
| PUT | `/{tuitionId}` | `UpdateTuitionRequest` | `TuitionResponse` |

```typescript
interface UpdateTuitionRequest {
  amount: number;
  discount: number;
  dueDate: string;
  status: TuitionStatus;
}

interface GenerateTuitionResponse {
  semesterId: number;
  semesterName: string;
  creditPrice: number;
  totalEnrollments: number;
  totalStudents: number;
  generatedCount: number;
  skippedCount: number;
}

interface TuitionResponse {
  id: number;
  student: StudentProfile;
  semester: SemesterInfo;
  amount: number;
  discount: number;
  finalAmount: number;
  dueDate: string;
  status: TuitionStatus;
  createdAt: string;
}
```

#### Admin Classes — `/admin/classes`

| Method | Path | Request | Response |
|--------|------|---------|----------|
| PUT | `/{classId}` | `UpdateClassRequest` | `ClassResponse` |

```typescript
interface UpdateClassRequest {
  semesterId: number;
  subjectId: number;
  teacherId: number;
  classCode: string;
  maxStudents: number;
  room: string;
  status: ClassStatus;
}
```

#### Admin Enrollments — `/admin/enrollments`

| Method | Path | Request | Response |
|--------|------|---------|----------|
| GET | `/{enrollmentId}` | `UpdateStatusEnrollment` | — |

```typescript
interface UpdateStatusEnrollment {
  status: EnrollmentStatus;
}
```

#### Admin Notifications — `/admin/notifications`

| Method | Path | Request | Response |
|--------|------|---------|----------|
| POST | `/broadcast` | `BroadcastNotificationRequest` | `number` (số người nhận) |
| POST | `/send` | `SendNotificationRequest` | `number` (số người nhận) |

```typescript
interface BroadcastNotificationRequest {
  title: string;
  content: string;
  type: NotificationType;
  referenceId?: number;
  referenceType?: string;
  targetRoles: Role[];
}

interface SendNotificationRequest {
  title: string;
  content: string;
  type: NotificationType;
  referenceId?: number;
  referenceType?: string;
  targetUserIds: number[];
}
```

---

## 6. Pages & Routes

### 6.1 Route Structure

```
/login                          → Public
/forgot-password                → Public

/student/dashboard              → ROLE_STUDENT
/student/profile                → ROLE_STUDENT
/student/grades                 → ROLE_STUDENT
/student/grades/:classId        → ROLE_STUDENT
/student/schedule               → ROLE_STUDENT
/student/enrollment             → ROLE_STUDENT
/student/tuition                → ROLE_STUDENT
/student/payments               → ROLE_STUDENT
/student/notifications          → ROLE_STUDENT
/student/program-progress       → ROLE_STUDENT

/teacher/dashboard              → ROLE_TEACHER
/teacher/profile                → ROLE_TEACHER
/teacher/classes                → ROLE_TEACHER
/teacher/classes/:classId       → ROLE_TEACHER (detail: grades, attendance)
/teacher/notifications          → ROLE_TEACHER

/admin/dashboard                → ROLE_ADMIN
/admin/students                 → ROLE_ADMIN
/admin/students/:studentId      → ROLE_ADMIN
/admin/teachers                 → ROLE_ADMIN
/admin/teachers/:teacherId      → ROLE_ADMIN
/admin/users                    → ROLE_ADMIN
/admin/classes                  → ROLE_ADMIN
/admin/classes/:classId         → ROLE_ADMIN
/admin/subjects                 → ROLE_ADMIN
/admin/departments              → ROLE_ADMIN
/admin/programs                 → ROLE_ADMIN
/admin/semesters                → ROLE_ADMIN
/admin/academic-years           → ROLE_ADMIN
/admin/tuition                  → ROLE_ADMIN
/admin/payments                 → ROLE_ADMIN
/admin/notifications            → ROLE_ADMIN

/payment/momo/return            → Public (callback từ Momo)
```

### 6.2 Route Guard Logic

```typescript
// Tại root "/" hoặc app init:
// 1. Gọi POST /api/v2/auth/refresh
// 2. Nếu thành công → lưu token → redirect tới trang theo role
// 3. Nếu thất bại → redirect /login

// Protected Route wrapper:
// - Kiểm tra isAuthenticated && isInitialized
// - Kiểm tra role có match với route không
// - Nếu không → redirect về trang phù hợp
```

---

## 7. Error Handling

### 7.1 HTTP Status Codes

| Code | Ý nghĩa | Xử lý |
|------|---------|-------|
| 200 | Thành công | Lấy `response.data.result` |
| 400 | Dữ liệu không hợp lệ | Hiển thị `error.response.data.message` |
| 401 | Chưa đăng nhập / token hết hạn | Tự động refresh hoặc redirect /login |
| 403 | Không có quyền | Hiển thị thông báo "Không có quyền truy cập" |
| 404 | Không tìm thấy | Hiển thị thông báo tương ứng |
| 413 | File quá lớn | Hiển thị "File không được vượt quá 10MB" |
| 500 | Lỗi server | Hiển thị "Lỗi hệ thống, vui lòng thử lại" |

### 7.2 Global Error Handler

```typescript
// Trong response interceptor của axiosPrivate
// Sau khi xử lý 401 (refresh token):

switch (error.response?.status) {
  case 400:
    toast.error(error.response.data.message);
    break;
  case 403:
    toast.error('Bạn không có quyền thực hiện thao tác này');
    break;
  case 404:
    toast.error(error.response.data.message || 'Không tìm thấy dữ liệu');
    break;
  case 413:
    toast.error('File quá lớn. Tối đa 10MB');
    break;
  case 500:
    toast.error('Lỗi hệ thống, vui lòng thử lại sau');
    break;
}
```

### 7.3 Lưu ý quan trọng

1. **Luôn dùng `withCredentials: true`** cho mọi request (để cookie hoạt động với refresh token).
2. **accessToken chỉ lưu trong memory** (store), không localStorage/sessionStorage — tránh XSS.
3. **Khi app khởi động**, phải chạy `/auth/refresh` trước khi render route protected.
4. **refreshToken không đọc được** bởi JS (HttpOnly cookie) — không cần xử lý gì thêm.
5. **Endpoint attendance** có lỗi chính tả: `/attendace` (thiếu chữ 'n') — phải gọi đúng.
6. **Momo payment**: sau khi tạo payment, redirect user sang `payUrl`. Khi Momo callback về `/payment/momo/return`, chỉ cần hiển thị trang kết quả.
7. **Dữ liệu thực sự** luôn nằm trong `response.data.result`, không phải `response.data.data`.

---

## 8. Khởi tạo dự án

### 8.1 Tạo project

```bash
npm create vite@latest quan-ly-sinh-vien-fe -- --template react-ts
cd quan-ly-sinh-vien-fe
npm install
```

### 8.2 Cài dependencies

```bash
# Core
npm install axios zustand react-router-dom

# UI
npm install antd @ant-design/icons

# Form & Validation
npm install react-hook-form zod @hookform/resolvers

# Styling
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init -p

# Date (day.js đã có trong antd, không cần cài thêm)
```

### 8.3 Cấu hình Tailwind (`tailwind.config.js`)

```js
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: { extend: {} },
  plugins: [],
  // Tắt preflight để không xung đột với Ant Design
  corePlugins: {
    preflight: false,
  },
};
```

### 8.4 Cấu hình `vite.config.ts`

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // Proxy để tránh CORS khi dev (tuỳ chọn — có thể bỏ vì BE đã config CORS)
    // proxy: {
    //   '/api': 'http://localhost:8080',
    // },
  },
});
```

### 8.5 File `src/main.tsx`

```tsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import { ConfigProvider } from 'antd';
import viVN from 'antd/locale/vi_VN';
import App from './App';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ConfigProvider
      locale={viVN}
      theme={{
        token: {
          colorPrimary: '#1677ff',
          borderRadius: 8,
        },
      }}
    >
      <App />
    </ConfigProvider>
  </React.StrictMode>
);
```

### 8.6 File `src/index.css`

```css
@tailwind base;
@tailwind components;
@tailwind utilities;

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background-color: #f5f5f5;
}
```

### 8.7 App Init trong `src/App.tsx`

```tsx
import { useEffect, useState } from 'react';
import { BrowserRouter } from 'react-router-dom';
import { axiosPublic } from './api/axiosInstance';
import { useAuthStore } from './stores/authStore';
import AppRouter from './router';
import LoadingScreen from './components/common/LoadingScreen';

export default function App() {
  const [isInitializing, setIsInitializing] = useState(true);
  const { setAuth, setInitialized } = useAuthStore();

  useEffect(() => {
    // App Init: thử refresh token khi mở app
    axiosPublic
      .post('/api/v2/auth/refresh')
      .then((res) => {
        const { accessToken, role, email } = res.data.result;
        setAuth(accessToken, role, email);
      })
      .catch(() => {
        // Không có session hợp lệ — để router tự redirect /login
      })
      .finally(() => {
        setInitialized();
        setIsInitializing(false);
      });
  }, []);

  if (isInitializing) return <LoadingScreen />;

  return (
    <BrowserRouter>
      <AppRouter />
    </BrowserRouter>
  );
}
```

---

## 9. Design System & UI

### 9.1 Màu sắc

| Vai trò | Primary color | Accent |
|---------|--------------|--------|
| Admin | `#1677ff` (Ant Design blue) | `#f0f2f5` nền |
| Teacher | `#1677ff` | `#f0f2f5` nền |
| Student | `#1677ff` | `#f0f2f5` nền |

> Dùng chung một màu primary. Phân biệt role qua sidebar menu items và badge.

### 9.2 Ant Design Token

```typescript
// Dùng trong ConfigProvider ở main.tsx
theme: {
  token: {
    colorPrimary: '#1677ff',
    colorSuccess: '#52c41a',
    colorWarning: '#faad14',
    colorError: '#ff4d4f',
    colorInfo: '#1677ff',
    borderRadius: 8,
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
  },
}
```

### 9.3 Hiển thị Enum ra tiếng Việt

Luôn dùng mapping để hiển thị, không hiển thị raw enum value:

```typescript
export const GENDER_LABEL: Record<Gender, string> = {
  MALE: 'Nam',
  FEMALE: 'Nữ',
  OTHER: 'Khác',
};

export const STUDENT_STATUS_LABEL: Record<StudentStatus, string> = {
  ACTIVE: 'Đang học',
  SUSPENDED: 'Đình chỉ',
  GRADUATED: 'Đã tốt nghiệp',
};

export const STUDENT_STATUS_COLOR: Record<StudentStatus, string> = {
  ACTIVE: 'green',
  SUSPENDED: 'red',
  GRADUATED: 'blue',
};

export const CLASS_STATUS_LABEL: Record<ClassStatus, string> = {
  OPEN: 'Đang mở',
  CLOSE: 'Đã đóng',
  CANCELLED: 'Đã huỷ',
};

export const CLASS_STATUS_COLOR: Record<ClassStatus, string> = {
  OPEN: 'green',
  CLOSE: 'default',
  CANCELLED: 'red',
};

export const ENROLLMENT_STATUS_LABEL: Record<EnrollmentStatus, string> = {
  ENROLLED: 'Đang học',
  DROPPED: 'Đã rút',
  COMPLETED: 'Hoàn thành',
};

export const PAYMENT_STATUS_LABEL: Record<PaymentStatus, string> = {
  PENDING: 'Chờ thanh toán',
  SUCCESS: 'Thành công',
  FAILED: 'Thất bại',
  REFUNDED: 'Đã hoàn tiền',
};

export const PAYMENT_STATUS_COLOR: Record<PaymentStatus, string> = {
  PENDING: 'orange',
  SUCCESS: 'green',
  FAILED: 'red',
  REFUNDED: 'purple',
};

export const PAYMENT_METHOD_LABEL: Record<PaymentMethod, string> = {
  BANK_TRANSFER: 'Chuyển khoản',
  MOMO: 'Ví MoMo',
  CASH: 'Tiền mặt',
};

export const TUITION_STATUS_LABEL: Record<TuitionStatus, string> = {
  PENDING: 'Chưa đóng',
  PAID: 'Đã đóng',
  OVERDUE: 'Quá hạn',
  WAIVED: 'Được miễn',
};

export const TUITION_STATUS_COLOR: Record<TuitionStatus, string> = {
  PENDING: 'orange',
  PAID: 'green',
  OVERDUE: 'red',
  WAIVED: 'blue',
};

export const GRADE_COMPONENT_LABEL: Record<GradeComponentType, string> = {
  ATTENDANCE: 'Chuyên cần',
  MIDTERM: 'Giữa kỳ',
  FINAL: 'Cuối kỳ',
  ASSIGNMENT: 'Bài tập',
};

export const ATTENDANCE_STATUS_LABEL: Record<AttendanceStatus, string> = {
  PRESENT: 'Có mặt',
  ABSENT: 'Vắng',
  LATE: 'Đi trễ',
  EXCUSED: 'Có phép',
};

export const ATTENDANCE_STATUS_COLOR: Record<AttendanceStatus, string> = {
  PRESENT: 'green',
  ABSENT: 'red',
  LATE: 'orange',
  EXCUSED: 'blue',
};

export const NOTIFICATION_TYPE_LABEL: Record<NotificationType, string> = {
  GRADE: 'Điểm số',
  SCHEDULE: 'Lịch học',
  PAYMENT: 'Học phí',
  SYSTEM: 'Hệ thống',
  ATTENDANCE: 'Điểm danh',
};

export const DAY_OF_WEEK_LABEL: Record<number, string> = {
  1: 'Thứ Hai',
  2: 'Thứ Ba',
  3: 'Thứ Tư',
  4: 'Thứ Năm',
  5: 'Thứ Sáu',
  6: 'Thứ Bảy',
  7: 'Chủ Nhật',
};

export const ROLE_LABEL: Record<string, string> = {
  ROLE_ADMIN: 'Quản trị viên',
  ROLE_TEACHER: 'Giáo viên',
  ROLE_STUDENT: 'Sinh viên',
};

export const LETTER_GRADE_COLOR: Record<LetterGrade, string> = {
  A: 'green',
  B: 'blue',
  C: 'orange',
  D: 'gold',
  F: 'red',
};
```

### 9.4 Format tiền tệ và ngày tháng

```typescript
// Format tiền VNĐ
export const formatCurrency = (amount: number) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);

// Format ngày
export const formatDate = (dateStr: string) =>
  dayjs(dateStr).format('DD/MM/YYYY');

// Format ngày giờ
export const formatDateTime = (dateStr: string) =>
  dayjs(dateStr).format('DD/MM/YYYY HH:mm');

// Format điểm GPA
export const formatGPA = (gpa: number) => gpa.toFixed(2);
```

---

## 10. Layout & Navigation

### 10.1 AppLayout

Tất cả trang (trừ `/login`, `/forgot-password`, `/payment/momo/return`) đều dùng layout chung:

```
┌─────────────────────────────────────────────────┐
│  HEADER (64px)                                   │
│  [Logo]  [Breadcrumb]    [Bell] [Avatar dropdown]│
├──────────┬──────────────────────────────────────┤
│          │                                       │
│ SIDEBAR  │  PAGE CONTENT                        │
│ (220px)  │  (padding: 24px)                     │
│          │                                       │
│          │                                       │
└──────────┴──────────────────────────────────────┘
```

- Sidebar: có thể collapse xuống 64px (chỉ icon)
- Header: fixed top
- Content area: scroll độc lập

### 10.2 Sidebar theo Role

#### ROLE_STUDENT

```
📊 Dashboard
👤 Hồ sơ cá nhân
📝 Đăng ký học phần
📅 Thời khóa biểu
🎓 Bảng điểm
📈 Tiến trình học tập
💰 Học phí
💳 Lịch sử thanh toán
🔔 Thông báo  [badge: unread count]
```

#### ROLE_TEACHER

```
📊 Dashboard
👤 Hồ sơ cá nhân
📚 Danh sách lớp học
🔔 Thông báo  [badge: unread count]
```

> Từ "Danh sách lớp học" → click vào từng lớp → ClassDetailPage với 2 tab: Điểm danh | Bảng điểm

#### ROLE_ADMIN

```
📊 Dashboard
── QUẢN LÝ NGƯỜI DÙNG ──
👥 Sinh viên
👨‍🏫 Giáo viên
🔑 Tài khoản hệ thống
── HỌC VỤ ──
📚 Lớp học
📖 Môn học
🏛️ Khoa / Bộ môn
🎓 Chương trình đào tạo
── HỌC KỲ ──
📅 Năm học
🗓️ Học kỳ
── TÀI CHÍNH ──
💰 Học phí
💳 Lịch sử thanh toán
── HỆ THỐNG ──
🔔 Gửi thông báo
```

### 10.3 Header

```
[☰ Toggle sidebar]  [Tên trang hiện tại]     [🔔 Badge]  [Avatar + Tên + ▼]
                                                            ├─ Hồ sơ cá nhân
                                                            ├─ Đổi mật khẩu
                                                            └─ Đăng xuất
```

- Bell icon: hiển thị badge với số thông báo chưa đọc (gọi `GET /api/v2/notifications/unread/count`)
- Click bell: mở Drawer hiển thị danh sách thông báo chưa đọc
- Avatar dropdown: click "Đăng xuất" → gọi logout API → redirect `/login`

### 10.4 ProtectedRoute

```tsx
// components/common/ProtectedRoute.tsx
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
    // Redirect về đúng trang theo role
    if (role === Role.ADMIN) return <Navigate to="/admin/dashboard" replace />;
    if (role === Role.TEACHER) return <Navigate to="/teacher/dashboard" replace />;
    if (role === Role.STUDENT) return <Navigate to="/student/dashboard" replace />;
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}
```

### 10.5 Router (`src/router/index.tsx`)

```tsx
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';
import ProtectedRoute from '../components/common/ProtectedRoute';
import AppLayout from '../components/layout/AppLayout';
import { Role } from '../types/auth.types';

// Auth pages
import LoginPage from '../pages/auth/LoginPage';
import ForgotPasswordPage from '../pages/auth/ForgotPasswordPage';

// Student pages
import StudentDashboard from '../pages/student/DashboardPage';
// ... import tất cả pages

export default function AppRouter() {
  const { isAuthenticated, role } = useAuthStore();

  return (
    <Routes>
      {/* Public */}
      <Route path="/login" element={<LoginPage />} />
      <Route path="/forgot-password" element={<ForgotPasswordPage />} />
      <Route path="/payment/momo/return" element={<MomoReturnPage />} />

      {/* Root redirect */}
      <Route
        path="/"
        element={
          !isAuthenticated ? <Navigate to="/login" replace /> :
          role === Role.ADMIN ? <Navigate to="/admin/dashboard" replace /> :
          role === Role.TEACHER ? <Navigate to="/teacher/dashboard" replace /> :
          <Navigate to="/student/dashboard" replace />
        }
      />

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
        <Route path="notifications" element={<NotificationsPage />} />
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
        <Route path="notifications" element={<NotificationsPage />} />
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
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
```

---

## 11. Patterns & Conventions

### 11.1 Cách gọi API (pattern chuẩn)

```typescript
// Mỗi file trong api/ export các function thuần
// Ví dụ: api/student.api.ts

import { axiosPrivate } from './axiosInstance';
import { StudentProfileResponse, UpdateProfileRequest } from '../types/student.types';

export const studentApi = {
  getProfile: async (): Promise<StudentProfileResponse> => {
    const res = await axiosPrivate.get('/api/v2/student/profile');
    return res.data.result;
  },

  updateProfile: async (data: UpdateProfileRequest): Promise<StudentProfileResponse> => {
    const res = await axiosPrivate.put('/api/v2/student/profile', data);
    return res.data.result;
  },

  uploadAvatar: async (file: File): Promise<{ avatarUrl: string }> => {
    const formData = new FormData();
    formData.append('file', file);
    const res = await axiosPrivate.post('/api/v2/student/profile/upload-avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data.result;
  },
};
```

### 11.2 Loading & Error state trong component

```tsx
// Pattern chuẩn cho mọi page fetch data
const [data, setData] = useState<T | null>(null);
const [loading, setLoading] = useState(true);

useEffect(() => {
  studentApi.getProfile()
    .then(setData)
    .catch((err) => {
      // axiosPrivate interceptor đã toast lỗi rồi
      // chỉ cần xử lý state nếu cần
    })
    .finally(() => setLoading(false));
}, []);

if (loading) return <Spin size="large" className="flex justify-center mt-20" />;
if (!data) return null;
```

### 11.3 Form pattern (React Hook Form + Zod)

```tsx
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Form, Input, Button, message } from 'antd';

const schema = z.object({
  email: z.string().email('Email không hợp lệ'),
  password: z.string().min(6, 'Mật khẩu ít nhất 6 ký tự'),
});

type FormValues = z.infer<typeof schema>;

export default function LoginForm() {
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm<FormValues>({
    resolver: zodResolver(schema),
  });

  const onSubmit = async (values: FormValues) => {
    try {
      await authApi.login(values.email, values.password);
      message.success('Đăng nhập thành công');
    } catch {
      // Lỗi đã được xử lý bởi interceptor
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <Input {...register('email')} status={errors.email ? 'error' : ''} />
      {errors.email && <span className="text-red-500">{errors.email.message}</span>}
      <Button htmlType="submit" loading={isSubmitting}>Đăng nhập</Button>
    </form>
  );
}
```

### 11.4 Table pattern (Ant Design)

```tsx
// Dùng antd Table cho tất cả danh sách
import { Table, Tag, Button, Space } from 'antd';
import type { ColumnsType } from 'antd/es/table';

const columns: ColumnsType<StudentProfile> = [
  { title: 'Mã SV', dataIndex: 'studentCode', key: 'studentCode' },
  { title: 'Họ tên', dataIndex: 'fullName', key: 'fullName' },
  {
    title: 'Trạng thái',
    dataIndex: 'status',
    key: 'status',
    render: (status: StudentStatus) => (
      <Tag color={STUDENT_STATUS_COLOR[status]}>{STUDENT_STATUS_LABEL[status]}</Tag>
    ),
  },
  {
    title: 'Thao tác',
    key: 'action',
    render: (_, record) => (
      <Space>
        <Button size="small" onClick={() => navigate(`/admin/students/${record.id}`)}>Chi tiết</Button>
      </Space>
    ),
  },
];

<Table
  columns={columns}
  dataSource={data}
  rowKey="id"
  loading={loading}
  pagination={{ pageSize: 10, showTotal: (total) => `Tổng ${total} bản ghi` }}
/>
```

### 11.5 Modal CRUD pattern

```tsx
// Dùng antd Modal + Form cho create/update
const [modalOpen, setModalOpen] = useState(false);
const [editingItem, setEditingItem] = useState<T | null>(null);

const handleCreate = () => { setEditingItem(null); setModalOpen(true); };
const handleEdit = (item: T) => { setEditingItem(item); setModalOpen(true); };
const handleClose = () => { setModalOpen(false); setEditingItem(null); };

const handleSubmit = async (values: FormValues) => {
  if (editingItem) {
    await api.update(editingItem.id, values);
    message.success('Cập nhật thành công');
  } else {
    await api.create(values);
    message.success('Tạo mới thành công');
  }
  handleClose();
  fetchData(); // reload list
};
```

### 11.6 Thông báo (Toast)

```typescript
// Dùng antd message (ngắn, không cần action)
import { message } from 'antd';
message.success('Thao tác thành công');
message.error('Có lỗi xảy ra');
message.warning('Chú ý: ...');

// Dùng antd notification (dài hơn, có title)
import { notification } from 'antd';
notification.success({ message: 'Thành công', description: 'Chi tiết...' });
```

### 11.7 Confirm xoá

```typescript
// Dùng antd Modal.confirm cho mọi thao tác xoá
import { Modal } from 'antd';

const handleDelete = (id: number) => {
  Modal.confirm({
    title: 'Xác nhận xoá',
    content: 'Bạn có chắc chắn muốn xoá không? Thao tác này không thể hoàn tác.',
    okText: 'Xoá',
    okType: 'danger',
    cancelText: 'Huỷ',
    onOk: async () => {
      await api.delete(id);
      message.success('Đã xoá thành công');
      fetchData();
    },
  });
};
```

### 11.8 Upload file (bulk import)

```tsx
// Dùng antd Upload cho bulk import Excel
import { Upload, Button } from 'antd';
import { UploadOutlined } from '@ant-design/icons';

<Upload
  accept=".xlsx,.xls"
  showUploadList={false}
  customRequest={async ({ file }) => {
    const formData = new FormData();
    formData.append('file', file as File);
    try {
      await axiosPrivate.post('/api/v2/admin/students/bulk-import', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      message.success('Import thành công');
      fetchData();
    } catch {
      // interceptor đã xử lý
    }
  }}
>
  <Button icon={<UploadOutlined />}>Import Excel</Button>
</Upload>
```

### 11.9 Chạy song song BE + FE (dev local)

```bash
# Terminal 1 — Backend (Spring Boot)
./mvnw spring-boot:run
# BE chạy tại http://localhost:8080

# Terminal 2 — Frontend (Vite)
npm run dev
# FE chạy tại http://localhost:5173
```

> BE phải chạy TRƯỚC khi mở FE. CORS đã được cấu hình sẵn cho `http://localhost:5173`.

---

## 12. Docker — Chạy toàn bộ dự án

> Dự án được đóng gói toàn bộ bằng Docker Compose gồm 4 services:
> `mysql` · `minio` · `backend` · `frontend`

### 12.1 Cấu trúc thư mục khi dùng Docker

```
quan-ly-sinh-vien-v2/        ← thư mục BE (repo này)
├── Dockerfile                ← build BE image
├── docker-compose.yml        ← orchestrate tất cả
├── .env                      ← biến môi trường (tự điền)
├── .dockerignore
├── frontend/                 ← thư mục FE (tạo project Vite vào đây)
│   ├── Dockerfile            ← build FE image (multi-stage → nginx)
│   ├── nginx.conf            ← config nginx cho React SPA
│   ├── .dockerignore
│   ├── package.json
│   ├── vite.config.ts
│   └── src/
├── src/                      ← source BE
└── pom.xml
```

### 12.2 Tạo FE project vào đúng thư mục

```bash
# Từ thư mục gốc của BE
cd frontend
npm create vite@latest . -- --template react-ts
# Chọn "." để tạo vào thư mục hiện tại
npm install
```

### 12.3 Cấu hình `frontend/vite.config.ts` (bắt buộc cho Docker)

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    host: true,  // cần thiết để Vite bind 0.0.0.0 trong container (dev mode)
  },
  // API_BASE_URL hardcode http://localhost:8080 vì browser gọi ra ngoài container
  // KHÔNG dùng tên service "backend" — browser không biết Docker network
});
```

### 12.4 Cấu hình `frontend/src/api/axiosInstance.ts` (bắt buộc)

```typescript
// BASE_URL phải là http://localhost:8080
// Khi chạy Docker, BE expose port 8080 ra host
// Browser gọi localhost:8080 — hoạt động bình thường
const BASE_URL = 'http://localhost:8080';
```

> **Không được** dùng `http://backend:8080` — `backend` là tên service Docker, browser không resolve được.

### 12.5 Điền biến môi trường trong `.env`

```bash
# Mở file .env và điền thông tin thực tế
DB_PASSWORD=root123
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin123
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password   # App Password Gmail, không phải password thường
```

### 12.6 Khởi động toàn bộ

```bash
# Build và chạy tất cả services
docker compose up --build

# Lần sau (không cần build lại nếu code không đổi)
docker compose up

# Chạy nền (background)
docker compose up -d

# Xem logs
docker compose logs -f
docker compose logs -f backend   # chỉ xem log BE
docker compose logs -f frontend  # chỉ xem log FE
```

### 12.7 Các URL sau khi chạy

| Service | URL | Mô tả |
|---------|-----|-------|
| Frontend | http://localhost:5173 | Giao diện người dùng |
| Backend API | http://localhost:8080 | REST API |
| MinIO Console | http://localhost:9001 | Quản lý file/ảnh |
| MinIO API | http://localhost:9000 | S3-compatible endpoint |
| MySQL | localhost:3306 | Database (dùng client như DBeaver) |

### 12.8 Lần đầu chạy — Tạo MinIO bucket

MinIO bucket chưa tự động tạo. Sau khi `docker compose up` xong:

```
1. Truy cập http://localhost:9001
2. Login: user = MINIO_ACCESS_KEY, password = MINIO_SECRET_KEY (từ file .env)
3. Vào Buckets → Create Bucket
4. Tên bucket: avatar-image (phải khớp với MINIO_BUCKET trong .env)
5. Tạo xong → vào bucket → Access Policy → set Public (để FE hiển thị ảnh)
```

### 12.9 Các lệnh quản lý thường dùng

```bash
# Dừng tất cả
docker compose down

# Dừng và xóa toàn bộ data (reset database, minio)
docker compose down -v

# Build lại image khi có thay đổi code
docker compose up --build backend    # chỉ build lại BE
docker compose up --build frontend   # chỉ build lại FE

# Restart một service
docker compose restart backend

# Xem trạng thái
docker compose ps

# Vào trong container
docker compose exec backend sh
docker compose exec mysql mysql -u root -p
```

### 12.10 Lưu ý quan trọng khi dùng Docker

1. **Database tự động tạo**: `createDatabaseIfNotExist=true` đã có trong connection string, không cần tạo DB thủ công.
2. **Schema tự migrate**: `ddl-auto: update` — Hibernate tự tạo/cập nhật bảng khi BE khởi động.
3. **Data persistent**: MySQL và MinIO data lưu trong Docker volumes (`mysql_data`, `minio_data`), không mất khi `docker compose down` (chỉ mất khi `down -v`).
4. **Thứ tự khởi động**: `docker-compose.yml` đã cấu hình `depends_on` + `healthcheck`, BE chờ MySQL sẵn sàng mới start, FE chờ BE.
5. **Hot reload không hoạt động** trong Docker production build — phải `docker compose up --build` mỗi khi đổi code.
6. **MinIO public URL**: phải là `http://localhost:9000` (không phải `http://minio:9000`) để browser load được ảnh.
