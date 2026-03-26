# FE Specification for `quan-ly-sinh-vien-v2`

## 1. Muc tieu tai lieu

Tai lieu nay tong hop toan bo backend hien co de dung lam dau vao cho frontend:

- Xac dinh domain va module nghiep vu
- Liet ke endpoint, quyen truy cap, query param, request body, response shape
- Chot enum, state, object model de dung cho table, form, detail page
- Chi ra cac diem bat nhat hoac canh bao trong backend de FE xu ly som

Pham vi tai lieu duoc suy ra tu code trong repo, khong phai tai lieu mong muon ly tuong. Nghia la uu tien "backend dang code gi" hon la "comment mong muon gi".

## 2. Tong quan he thong

Day la he thong quan ly sinh vien viet bang Spring Boot 4, Java 17, MySQL, JWT auth, upload avatar qua MinIO, thanh toan hoc phi qua MoMo.

### 2.1 Vai tro nguoi dung

- `ROLE_ADMIN`
- `ROLE_TEACHER`
- `ROLE_STUDENT`

### 2.2 Domain chinh

- Xac thuc va nguoi dung
- Khoa (`Department`)
- Chuong trinh dao tao (`Program`)
- Mon hoc (`Subject`)
- Nam hoc (`AcademicYear`)
- Hoc ky (`Semester`)
- Lop hoc phan (`ClassEntity`)
- Dang ky hoc (`Enrollment`)
- Diem thanh phan va bang diem (`GradeComponent`, `Grade`)
- Diem danh (`Attendance`)
- Ho so sinh vien (`StudentProfile`)
- Ho so giang vien (`TeacherProfile`)
- Hoc phi (`TuitionFee`)
- Thanh toan (`Payment`)
- Thong bao (`Notification`) da co entity nhung chua expose API

## 3. Stack va moi truong backend

### 3.1 Cong nghe

- Spring Boot Web MVC
- Spring Security + JWT
- Spring Data JPA
- MySQL
- MinIO
- Spring Mail
- MoMo payment gateway

### 3.2 Cau hinh local dang hard-code trong repo

- Backend port mac dinh: `8080`
- DB: `jdbc:mysql://localhost:3306/quan-ly-sinh-vien-v2`
- MinIO public URL: `http://localhost:9000`
- Upload multipart toi da: `10MB`
- MoMo return URL: `http://localhost:8080/api/v2/payments/momo/return`

### 3.3 Auth flow

- Login qua `POST /api/v2/auth/login`
- Backend tra ve:
  - `accessToken` trong JSON
  - `refreshToken` trong JSON
  - dong thoi set cookie `refreshToken`
- FE nen:
  - luu `accessToken` tam thoi
  - gui `Authorization: Bearer <accessToken>` cho endpoint duoc bao ve
  - bat `401` de goi refresh token
- Endpoint public:
  - `/api/v2/auth/login`
  - `/api/v2/auth/forgot-password`
  - `/api/v2/auth/reset-password` (co trong security, nhung khong thay controller)
  - `/api/v2/auth/refresh`
  - `/api/v2/payments/momo/**`
  - `/api/payments/momo/**`

## 4. Chuan response va error

### 4.1 Success response

Backend dung wrapper:

```json
{
  "code": 200,
  "message": "string",
  "result": {}
}
```

### 4.2 Error response

Backend dung:

```json
{
  "code": 400,
  "message": "string"
}
```

### 4.3 HTTP status thuong gap

- `400`: validation, authentication failed, already exists, create/update fail
- `401`: JWT het han
- `403`: khong du quyen
- `404`: not found
- `413`: file > 10MB
- `500`: internal server error

## 5. Enum cho FE

### 5.1 User / hoc vu

- `Gender`: `MALE`, `FEMALE`, `OTHER`
- `Role`: `ROLE_ADMIN`, `ROLE_TEACHER`, `ROLE_STUDENT`
- `StudentStatus`: `ACTIVE`, `SUSPENDED`, `GRADUATED`

### 5.2 Lop / dang ky / hoc ky

- `ClassStatus`: `OPEN`, `CLOSE`, `CANCELLED`
- `EnrollmentStatus`: `ENROLLED`, `DROPPED`, `COMPLETED`
- `SemesterName`: `HK1`, `HK2`, `HK3`

### 5.3 Diem / diem danh

- `AttendanceStatus`: `PRESENT`, `ABSENT`, `LATE`, `EXCUSED`
- `GradeComponentType`: `ATTENDANCE`, `MIDTERM`, `FINAL`, `ASSIGNMENT`
- `LetterGrade`: `A`, `B`, `C`, `D`, `F`

### 5.4 Hoc phi / thanh toan

- `TuitionStatus`: `PENDING`, `PAID`, `OVERDUE`, `WAIVED`
- `PaymentMethod`: `BANK_TRANSFER`, `MOMO`, `CASH`
- `PaymentStatus`: `PENDING`, `SUCCESS`, `FAILED`, `REFUNDED`

## 6. Data model tong quat cho FE

### 6.1 User

```ts
type User = {
  id: number
  email: string
  fullName: string
  phone: string
  avatarUrl: string
  role: 'ROLE_ADMIN' | 'ROLE_TEACHER' | 'ROLE_STUDENT'
}
```

### 6.2 StudentProfileResponse

```ts
type StudentProfileResponse = {
  user: {
    id: number
    email: string
    fullName: string
    phone: string
    avatarUrl: string
    personalEmail: string
    dayOfBirth: string
    address: string
    gender: 'MALE' | 'FEMALE' | 'OTHER'
  }
  academic: {
    studentCode: string
    program: { programId: number; name: string }
    department: { departmentId: number; name: string }
    enrollmentYear: number
    className: string
    status: 'ACTIVE' | 'SUSPENDED' | 'GRADUATED'
  }
}
```

### 6.3 TeacherProfileResponse

```ts
type TeacherProfileResponse = {
  id: number
  teacherCode: string
  fullName: string
  email: string
  personalEmail: string
  phone: string
  avatarUrl: string
  gender: 'MALE' | 'FEMALE' | 'OTHER'
  dateOfBirth: string
  department: { id: number; name: string }
  degree: string
  joinedDate: string
  isActive: boolean
}
```

### 6.4 Lop hoc phan

```ts
type ClassResponse = {
  classCode: string
  semesterResponse: {
    isActive: boolean
    name: 'HK1' | 'HK2' | 'HK3'
    academicYear: string
  }
  status: 'OPEN' | 'CLOSE' | 'CANCELLED'
  subjectResponse: {
    code: string
    name: string
    credits: number
    departmentName: string
  }
  teacherResponse: {
    id: number
    teacherCode: string
    fullName: string
    schoolEmail: string
    personalEmail: string
    dayOfBirth: string
    gender: 'MALE' | 'FEMALE' | 'OTHER'
    address: string
    avatarUrl: string
    departmentId: number
    degree: string
    specialization: string
    joinedDate: string
  }
}
```

### 6.5 TKB sinh vien

```ts
type StudentTimetableResponse = {
  timetable: Array<{
    dayOfWeek: number
    classes: Array<{
      classId: number
      classCode: string
      subjectName: string
      room: string
      teacherName: string
      startPeriod: number
      endPeriod: number
    }>
  }>
}
```

### 6.6 Hoc phi sinh vien

```ts
type StudentTuitionResponse = {
  id: number
  semester: {
    id: number
    name: string
    semesterNumber: number
    academicYear: string
  }
  amount: number
  discount: number
  finalAmount: number
  dueDate: string
  status: 'PENDING' | 'PAID' | 'OVERDUE' | 'WAIVED'
  createdAt: string
}
```

## 7. Man hinh FE nen co theo role

### 7.1 Public

- Login
- Forgot password
- MoMo return result page

### 7.2 Student

- Dashboard student
- Profile
- Upload avatar
- Program progress
- Timetable
- Dang ky hoc phan
- Danh sach lop da dang ky
- Bang diem tong hop
- Bang diem theo lop
- Hoc phi
- Lich su thanh toan

### 7.3 Teacher

- Dashboard teacher
- Profile
- Danh sach lop phu trach
- Diem danh theo ngay
- Quan ly grade components
- Nhap diem / sua diem / import diem
- Publish diem

### 7.4 Admin

- User management
- Student management
- Teacher management
- Department management
- Program management
- Subject management
- Academic year management
- Semester management
- Class management
- Enrollment moderation
- Tuition management

## 8. API chi tiet theo module

## 8.1 Auth

Base path: `/api/v2/auth`

### `POST /login`

Request:

```json
{
  "email": "string",
  "password": "string"
}
```

Response `result`:

```json
{
  "email": "string",
  "role": "ROLE_ADMIN",
  "accessToken": "jwt",
  "refreshToken": "jwt"
}
```

### `POST /logout`

- Doc `refreshToken` tu cookie
- Khong co body

### `POST /refresh`

- Doc `refreshToken` tu cookie
- Tra lai `AuthResponse`

### `POST /forgot-password`

Request:

```json
{
  "email": "string"
}
```

### `GET /me`

Response: `UserResponse`

### `PATCH /change-password`

Request:

```json
{
  "oldPassword": "string",
  "newPassword": "string",
  "confirmPassword": "string"
}
```

## 8.2 Academic Years

Base path: `/api/v2/academic-years`

### `GET /`

Query:

- `isCurrent?: boolean`

Response: list `AcademicYear`

### `POST /`

Role: `ADMIN`

Request:

```json
{
  "name": "2025-2026",
  "startDate": "2025-09-01",
  "endDate": "2026-06-30",
  "isCurrent": true
}
```

## 8.3 Semesters

Base path: `/api/v2/semesters`

### `GET /`

Query:

- `academicYearId?: number`
- `isActive?: boolean`
- `semesterNumber?: number`

### `POST /`

Request:

```json
{
  "academicYearId": 1,
  "semesterNumber": 1,
  "startDate": "2025-09-01",
  "endDate": "2025-12-31",
  "registrationStart": "2025-08-15T00:00:00Z",
  "registrationEnd": "2025-08-30T23:59:59Z",
  "isActive": true
}
```

### `PUT /{semesterId}`

Request:

```json
{
  "startDate": "2025-09-01",
  "endDate": "2025-12-31",
  "registrationStart": "2025-08-15T00:00:00Z",
  "registrationEnd": "2025-08-30T23:59:59Z"
}
```

### `PATCH /{semesterId}/toggle-active`

- Khong co body

## 8.4 Departments

Base path: `/api/v2/departments`

### `POST /`

Role: `ADMIN`

```json
{
  "code": "CNTT",
  "name": "Cong nghe thong tin",
  "description": "string",
  "headTeacherId": 1
}
```

### `GET /{departmentId}`

Response: `Department`

### `PUT /{departmentId}`

Role: `ADMIN`

```json
{
  "name": "string",
  "description": "string",
  "headTeacherId": 1
}
```

### `DELETE /{departmentId}`

Role: `ADMIN`

### `GET /{departmentId}/teachers`

Role: `ADMIN`

Query:

- `search?: string`
- `degree?: string`

Response: `TeacherResponse[]`

### `GET /{departmentId}/students`

Role: `ADMIN`

Query:

- `search?: string`
- `programId?: number`
- `enrollmentYear?: number`
- `status?: StudentStatus`

Response: `StudentResponse[]`

## 8.5 Programs

Base path: `/api/v2/programs`

### `GET /`

Query:

- `departmentId?: number`
- `search?: string`

### `POST /`

Theo comment: `ADMIN ONLY`

Request:

```json
{
  "departmentId": 1,
  "code": "KTPM",
  "name": "Ky thuat phan mem",
  "totalCredits": 130,
  "durationYear": 4,
  "description": "string"
}
```

### `GET /{programId}`

### `PUT /{programId}`

Request:

```json
{
  "name": "string",
  "totalCredits": 130,
  "description": "string",
  "durationYear": 4
}
```

### `DELETE /{programId}`

### `GET /{programId}/subjects`

Query:

- `semester?: number`
- `isRequired?: boolean`

Response: `ProgramSubject[]`

### `POST /{programId}/subjects`

```json
{
  "subjectId": 1,
  "semester": 1,
  "isRequired": true,
  "prerequisiteSubjectId": 2
}
```

### `DELETE /{programId}/subjects/{subjectId}`

## 8.6 Subjects

Base path: `/api/v2/subjects`

### `GET /`

Query:

- `departmentId?: number`
- `search?: string`
- `isActive?: boolean`
- `credits?: number`

### `POST /`

Role: `ADMIN`

```json
{
  "departmentId": 1,
  "code": "INT1306",
  "name": "Nhap mon lap trinh",
  "credits": 3,
  "description": "string",
  "isActive": true
}
```

### `GET /{subjectId}`

### `PUT /{subjectId}`

Role: `ADMIN`

```json
{
  "name": "string",
  "description": "string",
  "isActive": true,
  "credits": 3
}
```

### `DELETE /{subjectId}`

## 8.7 Classes

Base path: `/api/v2/classes`

### `GET /`

Query:

- `semesterId?: number`
- `subjectId?: number`
- `teacherId?: number`
- `status?: ClassStatus`
- `search?: string`
- `hasSlot?: boolean`

Response: `ClassResponse[]`

### `POST /`

Role: `ADMIN`

```json
{
  "semesterId": 1,
  "subjectId": 1,
  "teacherId": 1,
  "classCode": "INT1306-01",
  "maxStudents": 50,
  "room": "A101"
}
```

### `GET /{classId}`

Response: `ClassResponse`

### `PATCH /{classId}/change-status`

Role: `ADMIN`

```json
{
  "status": "OPEN"
}
```

### `DELETE /{classId}`

Role: `ADMIN`

### `GET /{classId}/student`

Code guard hien tai: `hasAllRoles('ADMIN', 'TEACHER')`

Query:

- `search?: string`

Response: `StudentResponse[]`

### `POST /{classId}/schedules`

Role: `ADMIN`

```json
{
  "schedules": [
    {
      "dayOfWeek": 2,
      "startPeriod": 1,
      "endPeriod": 3,
      "room": "A101",
      "startWeek": "2025-09-01",
      "endWeek": "2025-12-01"
    }
  ]
}
```

### `GET /{classId}/schedules`

Response:

```json
{
  "schedules": [
    {
      "dayOfWeek": 2,
      "startPeriod": 1,
      "endPeriod": 3,
      "room": "A101",
      "startWeek": "2025-09-01",
      "endWeek": "2025-12-01"
    }
  ]
}
```

## 8.8 Enrollment for Student

Base path: `/api/v2/enrollments`

### `GET /available-classes`

Role: `STUDENT`

Query:

- `semesterId: number` bat buoc
- `subjectId?: number`
- `departmentId?: number`
- `hasSlot?: boolean`
- `notEnrolled?: boolean`
- `search?: string`

Response: `ClassResponse[]`

### `POST /{classId}`

Role: `STUDENT`

- Dang ky vao lop
- Khong co body

### `DELETE /{enrollmentId}`

Role: `STUDENT`

- Huy dang ky

### `GET /my`

Role: `STUDENT`

Query:

- `semesterId?: number`
- `status?: EnrollmentStatus`

Response: `Enrollment[]`

### `GET /{enrollmentId}`

Response: `Enrollment`

## 8.9 Student APIs

Base path: `/api/v2/student`

Alias cu: `/api/student`

### `GET /grades`

Response: `AllGradeStudent`

### `GET /grades/{classId}`

Response: `StudentGradeResponse`

### `GET /profile`

Response: `StudentProfileResponse`

### `PUT /profile`

```json
{
  "fullName": "string",
  "phone": "string",
  "personalEmail": "string",
  "dayOfBirth": "2003-01-01",
  "address": "string",
  "gender": "MALE"
}
```

### `POST /profile/upload-avatar`

Multipart:

- field: `file`

Response:

```json
{
  "avatarUrl": "string"
}
```

### `GET /program-progress`

Response: `ProgramResponse`

### `GET /schedules`

Query:

- `semesterId?: number`
- `fromDate?: yyyy-MM-dd`
- `toDate?: yyyy-MM-dd`

Response: `StudentTimetableResponse`

### `GET /tuition`

Query:

- `semesterId?: number`

Response: `StudentTuitionResponse[]`

### `GET /payments`

Query:

- `status?: PaymentStatus`

Response: `StudentPaymentHistoryResponse[]`

## 8.10 Payment APIs

Base path:

- `/api/v2/payments`
- `/api/payments`

### `POST /momo/create/{tuitionId}`

Role: `STUDENT`

Response:

```json
{
  "paymentId": 1,
  "orderId": "string",
  "requestId": "string",
  "amount": 1000000,
  "status": "PENDING",
  "payUrl": "https://...",
  "deeplink": "string",
  "qrCodeUrl": "string"
}
```

### `GET /momo/return`

Public endpoint de redirect sau thanh toan.

Response:

```json
{
  "orderId": "string",
  "requestId": "string",
  "transId": 123,
  "paymentStatus": "SUCCESS",
  "resultCode": 0,
  "message": "string",
  "signatureValid": true,
  "success": true
}
```

### `POST /momo/ipn`

- Public callback cho gateway
- FE khong can goi truc tiep

## 8.11 Teacher APIs

Base path: `/api/v2/teacher`

Role mac dinh: `TEACHER`

### `PUT /{classId}`

Cap nhat thong tin lop theo teacher.

Request:

```json
{
  "room": "A102"
}
```

Luu y: route nay theo ten method la update class, nhung URL khong phai `/classes/{classId}` ma la `/{classId}`.

### `GET /classes`

Response: `ClassResponse[]`

### `GET /classes/{classId}/attendance`

Query:

- `date: yyyy-MM-dd`

Response:

```json
{
  "classId": 1,
  "date": "2025-09-10",
  "students": [
    {
      "enrollmentId": 1,
      "studentCode": "SV001",
      "name": "Nguyen Van A",
      "status": "PRESENT"
    }
  ]
}
```

### `POST /classes/{classId}/attendace`

Role: `TEACHER`

Luu y backend dang typo `attendace`, khong phai `attendance`.

Query:

- `date: yyyy-MM-dd`

Request:

```json
[
  {
    "enrollmentId": 1,
    "status": "PRESENT"
  }
]
```

### `PATCH /classes/{classId}/attendance/{attendanceId}`

Query:

- `date: yyyy-MM-dd`

Request:

```json
{
  "enrollmentId": 1,
  "status": "LATE"
}
```

### `GET /classes/{classId}/grade-components`

Response: list item

```json
{
  "id": 1,
  "name": "Midterm",
  "weight": 30,
  "type": "MIDTERM",
  "maxScore": 10
}
```

### `POST /classes/{classId}/grade-components`

```json
{
  "weight": 30,
  "type": "MIDTERM",
  "maxScore": 10
}
```

### `PUT /classes/{classId}/grade-components/{gradeComponentId}`

```json
{
  "weight": 40,
  "maxScore": 10
}
```

### `DELETE /classes/{classId}/grade-components/{gradeComponentId}`

### `GET /classes/{classId}/grades`

Query:

- `componentId?: number`
- `isPublished?: boolean`
- `search?: string`

Response:

```json
{
  "classId": 1,
  "students": [
    {
      "enrollmentId": 1,
      "studentCode": "SV001",
      "name": "Nguyen Van A",
      "grades": {
        "MIDTERM": 8.5,
        "FINAL": 9
      }
    }
  ]
}
```

### `POST /classes/{classId}/grades`

```json
{
  "enrollmentId": 1,
  "componentId": 1,
  "score": 8.5
}
```

### `PUT /classes/{classId}/grades/{gradeId}`

```json
{
  "componentId": 1,
  "score": 9
}
```

### `POST /classes/{classId}/grades/import`

Multipart:

- field: `file`

Response: `StudentGradeResponse[]`

### `PATCH /classes/{classId}/grades/publish`

Response `result`: so luong diem duoc publish

### `GET /profile`

Response: `TeacherProfileResponse`

### `PUT /profile`

```json
{
  "fullName": "string",
  "phone": "string",
  "personalEmail": "string",
  "dayOfBirth": "1990-01-01",
  "address": "string",
  "gender": "MALE"
}
```

## 8.12 Admin User APIs

Base path: `/api/v2/admin/users`

Role: `ADMIN`

### `GET /`

Query:

- `search?: string`
- `role?: Role`

### `GET /{userId}`

### `PUT /{userId}`

```json
{
  "fullName": "string",
  "phone": "string",
  "avatarUrl": "string"
}
```

### `PATCH /{userId}`

- Toggle active user

### `DELETE /{userId}`

## 8.13 Admin Student APIs

Base path: `/api/v2/admin/students`

Role: `ADMIN`

### `GET /`

Query:

- `search?: string`
- `departmentId?: number`
- `programId?: number`
- `enrollmentYear?: number`
- `status?: StudentStatus`
- `gpaMin?: number`
- `gpaMax?: number`

### `GET /{studentId}`

### `POST /`

```json
{
  "fullName": "string",
  "personalEmail": "string",
  "phone": "string",
  "avatarUrl": "string",
  "departmentId": 1,
  "programId": 1,
  "dayOfBirth": "2003-01-01",
  "address": "string",
  "gender": "MALE",
  "className": "D21CQCN01-N"
}
```

### `POST /bulk-import`

Multipart:

- field: `file`

### `PUT /{studentId}`

```json
{
  "fullName": "string",
  "phone": "string",
  "personalEmail": "string",
  "dayOfBirth": "2003-01-01",
  "address": "string",
  "gender": "MALE",
  "isActive": true,
  "enrollmentYear": 2021,
  "className": "D21CQCN01-N",
  "status": "ACTIVE"
}
```

## 8.14 Admin Teacher APIs

Base path: `/api/v2/admin/teachers`

Role: `ADMIN`

### `GET /`

Query:

- `search?: string`
- `departmentId?: number`
- `degree?: string`

### `POST /`

```json
{
  "fullName": "string",
  "personalEmail": "string",
  "phone": "string",
  "avatarUrl": "string",
  "teacherCode": "GV001",
  "departmentId": 1,
  "degree": "Thac si",
  "address": "string"
}
```

### `POST /bulk-import`

Multipart:

- field: `file`

### `GET /{teacherId}`

## 8.15 Admin Class APIs

Base path: `/api/v2/admin/classes`

Role: `ADMIN`

### `PUT /{classId}`

```json
{
  "semesterId": 1,
  "subjectId": 1,
  "teacherId": 1,
  "classCode": "INT1306-01",
  "maxStudents": 50,
  "room": "A101",
  "status": "OPEN"
}
```

## 8.16 Admin Enrollment APIs

Base path: `/api/v2/admin/enrollments`

Role: `ADMIN`

### `GET /{enrollmentId}`

Code hien tai dung `@GetMapping` nhung method lai cap nhat status va nhan `@RequestBody`.

Request body thuc te:

```json
{
  "status": "COMPLETED"
}
```

FE khong nen tin endpoint nay cho toi khi backend sua lai thanh `PATCH`.

## 8.17 Admin Tuition APIs

Base path: `/api/v2/admin/tuition`

Role: `ADMIN`

### `POST /generate`

Response:

```json
{
  "semesterId": 1,
  "semesterName": "HK1",
  "creditPrice": 1000000,
  "totalEnrollments": 100,
  "totalStudents": 40,
  "generatedCount": 40,
  "skippedCount": 0
}
```

### `GET /`

Response: `TuitionResponse[]`

### `PUT /{tuitionId}`

```json
{
  "amount": 5000000,
  "discount": 1000000,
  "dueDate": "2025-10-01",
  "status": "PENDING"
}
```

## 9. Goi y typescript types nen tao trong FE

Nen tach theo file:

- `types/auth.ts`
- `types/user.ts`
- `types/student.ts`
- `types/teacher.ts`
- `types/academic.ts`
- `types/class.ts`
- `types/enrollment.ts`
- `types/grade.ts`
- `types/payment.ts`

Core generic:

```ts
export type ApiResponse<T> = {
  code: number
  message: string
  result: T
}

export type ApiError = {
  code: number
  message: string
}
```

## 10. Routing FE de xay nhanh

### 10.1 Public

- `/login`
- `/forgot-password`
- `/payment/momo/result`

### 10.2 Student

- `/student`
- `/student/profile`
- `/student/program`
- `/student/schedule`
- `/student/enrollments`
- `/student/grades`
- `/student/grades/:classId`
- `/student/tuition`
- `/student/payments`

### 10.3 Teacher

- `/teacher`
- `/teacher/profile`
- `/teacher/classes`
- `/teacher/classes/:classId/attendance`
- `/teacher/classes/:classId/grades`
- `/teacher/classes/:classId/components`

### 10.4 Admin

- `/admin`
- `/admin/users`
- `/admin/students`
- `/admin/students/:id`
- `/admin/teachers`
- `/admin/teachers/:id`
- `/admin/departments`
- `/admin/programs`
- `/admin/subjects`
- `/admin/academic-years`
- `/admin/semesters`
- `/admin/classes`
- `/admin/tuition`

## 11. Quy uoc UI/UX nen dung

### 11.1 Bang du lieu

Moi list view nen co:

- search box
- filter theo enum
- reset filter
- phan trang phia FE neu backend chua co pagination
- state `loading`, `empty`, `error`

### 11.2 Form

Moi form nen co:

- mapping enum sang select
- validate ngay thang o FE truoc khi submit
- preview avatar/file truoc upload
- toast cho success/error

### 11.3 Auth

- route guard theo role
- refresh token interceptor
- auto redirect sau login theo role
  - admin -> `/admin`
  - teacher -> `/teacher`
  - student -> `/student`

## 12. Cac diem bat nhat backend FE can biet

Day la phan quan trong nhat neu ban muon lam FE tru tru:

1. `ClassController#getStudentsInClass` dung `hasAllRoles('ADMIN', 'TEACHER')`.
   Dieu nay co nghia user phai dong thoi co ca 2 role, rat co the endpoint nay se khong dung duoc trong thuc te.

2. `TeacherController` endpoint diem danh batch bi typo:
   route la `/classes/{classId}/attendace`, khong phai `/attendance`.

3. `TeacherController#updateClass` map toi `PUT /api/v2/teacher/{classId}`.
   Theo nghia nghiep vu rat de nham voi route dung ra phai la `/classes/{classId}`.

4. `AdminEnrollmentController` dang dung `GET /{enrollmentId}` de cap nhat status va nhan request body.
   Day la sai REST va mot so client/library se khong gui body cho GET.

5. `ProgramController`, `SemesterController`, `SubjectController#deleteSubject` co comment "admin only" nhung mot so method khong co `@PreAuthorize`.
   FE nen role-guard o phia client theo nghiep vu, nhung khi test cung can check thuc te backend.

6. `StudentResponse.from()` gan `schoolEmail` bang `personalEmail`, co kha nang sai field.
   FE khong nen gia dinh `schoolEmail` luon la email truong.

7. Backend co alias route cu va moi:
   - student: `/api/v2/student` va `/api/student`
   - payment: `/api/v2/payments` va `/api/payments`
   FE nen chon mot chuan duy nhat, uu tien `/api/v2/...`.

8. Controller tra ve luc thi entity raw, luc thi DTO.
   FE can parse phong thu truoc, khong nen ky vong cac object cung style.

9. Chua thay API pagination.
   Neu du lieu lon, FE phai tu chia trang client-side hoac yeu cau backend bo sung.

10. `application.yaml` chua secret that trong repo.
    Ve van hanh, can tach env sau; voi FE thi chi can biet moi truong local dang tro toi localhost.

## 13. Thu tu uu tien de lam FE

Neu ban muon lam FE nhanh theo chieu tang dan:

1. Auth + layout + role guard
2. Shared API client + interceptor + types
3. Student module
4. Teacher module
5. Admin core CRUD: users, students, teachers
6. Academic setup: departments, programs, subjects, semesters, classes
7. Tuition + payment

## 14. De xuat cau truc FE

```txt
src/
  app/
  pages/
  layouts/
  components/
  features/
    auth/
    student/
    teacher/
    admin/
  services/
    api-client.ts
    auth.service.ts
    student.service.ts
    teacher.service.ts
    admin.service.ts
  types/
  hooks/
  utils/
  constants/
```

## 15. Ket luan

Backend hien tai du de dung lam nen cho FE quan ly dao tao voi 3 role: admin, giang vien, sinh vien. Phan student va teacher kha ro. Phan admin da co nhieu CRUD, nhung co vai diem khong dong nhat ve route va authorize. Khi lam FE, nen dong bang contract theo tai lieu nay va danh dau cac endpoint "can verify backend" truoc khi wiring man hinh quan trong.
