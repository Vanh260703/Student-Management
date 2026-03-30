# 📱 Frontend Specification - Quản Lý Sinh Viên V2

## 📌 Tổng Quan Dự Án

**Quản Lý Sinh Viên V2** là hệ thống quản lý sinh viên toàn diện với frontend hiện đại, responsive, và dễ sử dụng. Frontend được xây dựng với React/Vue, TypeScript, và Tailwind CSS.

---

## 🎨 Technology Stack Khuyến Nghị

### Frontend Framework
- **Framework**: React 18+ hoặc Vue 3
- **Language**: TypeScript
- **State Management**: Zustand / Redux Toolkit / Pinia (Vue)
- **HTTP Client**: Axios
- **UI Components**: Material-UI / Ant Design / shadcn/ui
- **Styling**: Tailwind CSS
- **Form Handling**: React Hook Form / VeeValidate (Vue)
- **Validation**: Zod / Yup
- **Date**: Day.js
- **Charts**: Recharts / Chart.js
- **Notifications**: react-toastify / vue-toast
- **Build Tool**: Vite
- **Testing**: Jest + React Testing Library
- **Env**: Node.js 18+

---

## 🔐 Authentication & Authorization

### Login Flow
```
1. User nhập email & password
2. Frontend: POST /api/v2/auth/login
3. Backend: Return { accessToken, refreshToken, user }
4. Frontend: Lưu tokens (localStorage)
5. Redux: Cập nhật auth state
6. Redirect: Dựa trên role (admin/teacher/student)
```

### Protected Routes
- Check authentication before rendering
- Redirect to login if not authenticated
- Redirect to error if insufficient role

---

## 📊 Dashboard Pages

### Admin Dashboard
**Route**: `/admin/dashboard`
**Required**: ROLE_ADMIN
**API**: `GET /api/v2/admin/dashboard`

**Response Data**:
- totalStudents, totalTeachers, totalAdmins
- totalActiveUsers, totalInactiveUsers
- totalClasses, totalOpenClasses, totalClosedClasses
- totalEnrollments, totalSubjects, totalPrograms
- totalDepartments, totalSemesters
- totalTuitionCollected, totalTuitionPending
- totalPaymentTransactions, totalPaidPayments, totalPendingPayments
- totalNotificationsSent, totalUnreadNotifications
- averageStudentsPerClass, averageGPA, totalFailedGrades
- lastUpdated, systemStatus

### Teacher Dashboard
**Route**: `/teacher/dashboard`
**Required**: ROLE_TEACHER
**API**: `GET /api/v2/teacher/dashboard`

**Response Data**:
- teacherInfo, departmentName
- totalClasses, classes[], totalStudents
- totalGradesPosted, totalGradesPending, totalEnrollments
- averageClassGPA, totalFailedStudents, totalExcellentStudents
- largestClassName, largestClassSize
- smallestClassName, smallestClassSize
- lastUpdated

### Student Dashboard
**Route**: `/student/dashboard`
**Required**: ROLE_STUDENT
**API**: `GET /api/v2/student/dashboard`

**Response Data**:
- studentInfo, programName, departmentName, enrollmentYear
- totalEnrolledClasses, totalCompletedCredits, currentGPA
- studentStatus, averageScore, totalPassedSubjects, totalFailedSubjects
- recentGrades, attendanceRate, totalAbsentDays, totalLateArrivals
- totalTuitionFee, paidAmount, remainingAmount, tuitionStatus
- upcomingClasses, nextClassName, nextClassRoom, nextClassTime
- lastUpdated

---

## 📬 Notification System

### Get All Notifications
```
GET /api/v2/notifications
```

### Get Unread Notifications
```
GET /api/v2/notifications/unread
GET /api/v2/notifications/unread/count
```

### Mark as Read
```
PUT /api/v2/notifications/:id/mark-as-read
PUT /api/v2/notifications/mark-all-as-read
```

### Delete Notification
```
DELETE /api/v2/notifications/:id
```

---

## 🎓 Main Features

### Authentication
- Login, Logout, Forgot Password, Reset Password, Change Password
- Token-based auth with automatic refresh
- Role-based routing

### User Management (Admin)
- View/Edit/Delete users
- Block/Unblock users
- Create new users
- Filter and search

### Classes
- View all classes with filters
- Create/Edit/Delete classes (Admin)
- Enroll in classes (Student)
- Manage class schedule
- View class details

### Grades
- Teacher: Enter grades, set grade components, publish grades
- Student: View grades, GPA, transcript
- Auto-calculate final grades

### Attendance
- Teacher: Mark attendance by date
- Student: View attendance rate and history

### Payments
- Student: View tuition fees, payment history
- Integration with MoMo payment gateway
- Payment confirmation and receipt

### Notifications
- Bell icon with unread count
- Notification center with all notifications
- Types: GRADE, SCHEDULE, PAYMENT, SYSTEM, ATTENDANCE

---

## 🎨 UI/UX Design

### Color Scheme
- Primary: #2563EB (Blue)
- Secondary: #7C3AED (Purple)
- Success: #10B981 (Green)
- Warning: #F59E0B (Orange)
- Error: #EF4444 (Red)

### Responsive Design
- Mobile: < 640px
- Tablet: 640px - 1024px
- Desktop: > 1024px

### Component Library
- Button, Input, Select, Modal, Table
- Form components with validation
- Loading skeletons
- Error toasts

---

## 🌐 API Endpoints Summary

```
AUTH:
  POST   /auth/login
  POST   /auth/refresh-token
  POST   /auth/forgot-password
  POST   /auth/reset-password
  POST   /auth/change-password

DASHBOARD:
  GET    /admin/dashboard
  GET    /teacher/dashboard
  GET    /student/dashboard

USERS (Admin):
  GET    /admin/users?page=0&size=10
  POST   /admin/users
  GET    /admin/users/:id
  PUT    /admin/users/:id
  DELETE /admin/users/:id
  POST   /admin/users/:id/block
  POST   /admin/users/:id/unblock

STUDENT:
  GET    /student/profile
  PUT    /student/profile
  GET    /student/grades
  GET    /student/grades/:classId
  GET    /student/tuitions
  GET    /student/payment-history
  POST   /student/profile/upload-avatar

TEACHER:
  GET    /teacher/profile
  PUT    /teacher/profile
  GET    /teacher/classes
  GET    /teacher/classes/:classId/grades
  POST   /teacher/classes/:classId/grade-components
  POST   /teacher/grades/:enrollmentId
  PUT    /teacher/grades/:gradeId
  PUT    /teacher/grades/:gradeId/publish
  GET    /teacher/classes/:classId/attendance?date=
  POST   /teacher/classes/:classId/attendace?date=
  PATCH  /teacher/classes/:classId/attendance/:attendanceId?date=

CLASSES:
  GET    /classes?semesterId=&subjectId=&teacherId=&status=&search=&hasSlot=
  POST   /classes
  GET    /classes/:classId
  PUT    /classes/:classId
  PATCH  /classes/:classId/change-status
  DELETE /classes/:classId

ENROLLMENT:
  GET    /enrollments/available-classes?semesterId=&subjectId=&departmentId=
  POST   /enrollments/:classId
  DELETE /enrollments/:enrollmentId
  GET    /enrollments/my?semesterId=&status=

NOTIFICATIONS:
  GET    /notifications
  GET    /notifications/unread
  GET    /notifications/unread/count
  GET    /notifications/:id
  PUT    /notifications/:id/mark-as-read
  PUT    /notifications/mark-all-as-read
  DELETE /notifications/:id

PAYMENTS:
  POST   /payments/momo/create/:tuitionId
  GET    /payments/momo/return
  POST   /payments/momo/ipn
```

---

## 📝 Development Checklist

- [ ] Project setup with Vite + React/Vue
- [ ] Axios client with interceptors
- [ ] State management (Redux/Zustand/Pinia)
- [ ] Authentication & token management
- [ ] Route structure with guards
- [ ] Login page
- [ ] Admin Dashboard
- [ ] Teacher Dashboard
- [ ] Student Dashboard
- [ ] User Management (Admin)
- [ ] Class Management
- [ ] Grade Management
- [ ] Attendance Tracking
- [ ] Payment Integration
- [ ] Notification System
- [ ] Profile pages
- [ ] Responsive design
- [ ] Form validation
- [ ] Error handling
- [ ] Tests
- [ ] Deployment

---

## 📚 Type Definitions

### User Type
```typescript
interface User {
  id: number;
  email: string;
  fullName: string;
  role: 'ROLE_ADMIN' | 'ROLE_TEACHER' | 'ROLE_STUDENT';
  avatarUrl: string;
  isActive: boolean;
  createdAt: string;
}
```

### Dashboard Response Types
```typescript
interface AdminDashboardResponse {
  totalStudents: number;
  totalTeachers: number;
  totalAdmins: number;
  totalClasses: number;
  totalEnrollments: number;
  averageGPA: number;
  systemStatus: string;
  lastUpdated: string;
}

interface TeacherDashboardResponse {
  teacherInfo: User;
  totalClasses: number;
  totalStudents: number;
  averageClassGPA: number;
  totalGradesPosted: number;
  totalGradesPending: number;
}

interface StudentDashboardResponse {
  studentInfo: User;
  currentGPA: number;
  totalEnrolledClasses: number;
  averageScore: number;
  totalTuitionFee: number;
  paidAmount: number;
  remainingAmount: number;
  tuitionStatus: string;
}
```

---

## 🚢 Environment Variables

```
VITE_API_URL=http://localhost:8080/api/v2
VITE_APP_NAME=Student Management System
VITE_ENVIRONMENT=development
```

---

## 🎯 Implementation Checklist

- [ ] Project setup with Vite + React/Vue
- [ ] Axios client with interceptors
- [ ] State management setup
- [ ] Authentication implementation
- [ ] Dashboard pages
- [ ] Notification system
- [ ] Form validation
- [ ] Error handling
- [ ] Responsive design
- [ ] Tests
- [ ] Deployment

---

**Status**: ✅ Ready for Frontend Development
**Version**: 1.0.0
**Last Updated**: March 30, 2026

**Happy Coding! 🚀**

