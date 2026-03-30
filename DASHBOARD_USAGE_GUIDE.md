# 🎯 Hướng Dẫn Sử Dụng Dashboard Endpoints

## 📌 Giới Thiệu

Hệ thống Quản Lý Sinh Viên V2 cung cấp 3 Dashboard riêng biệt cho **Admin**, **Teacher**, và **Student** với các chức năng tương ứng.

---

## 🚀 Bắt Đầu Nhanh

### 1. Đăng Nhập & Lấy Token

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@school.edu.vn",
    "password": "password123"
  }'
```

**Response**:
```json
{
  "code": 200,
  "message": "Login successfully!",
  "result": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "...",
    "user": { ... }
  }
}
```

### 2. Sử Dụng Token Để Truy Cập Dashboard

```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
curl -X GET "http://localhost:8080/api/v2/admin/dashboard" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 👨‍💼 Admin Dashboard

### Mục Đích
Cung cấp tổng quát **toàn bộ hệ thống** với các thống kê chi tiết.

### Endpoint
```http
GET /api/v2/admin/dashboard
```

### Yêu Cầu
- Vai trò: **ADMIN**
- Token: JWT hợp lệ

### Dữ Liệu Trả Về

#### 1. Thống Kê Người Dùng
```json
"totalStudents": 150,           // Tổng sinh viên
"totalTeachers": 20,             // Tổng giáo viên
"totalAdmins": 3,                // Tổng admin
"totalActiveUsers": 170,         // Người dùng hoạt động
"totalInactiveUsers": 3          // Người dùng không hoạt động
```

#### 2. Thống Kê Lớp Học
```json
"totalClasses": 45,              // Tổng lớp học
"totalOpenClasses": 30,          // Lớp đang mở
"totalClosedClasses": 15,        // Lớp đã đóng
"totalEnrollments": 2500         // Tổng enrollments
```

#### 3. Thống Kê Học Tập
```json
"totalSubjects": 80,             // Tổng môn học
"totalPrograms": 5,              // Tổng chương trình
"totalDepartments": 8,           // Tổng bộ môn
"totalSemesters": 3              // Tổng kỳ học
```

#### 4. Thống Kê Tài Chính
```json
"totalTuitionCollected": 1500000000,    // Học phí đã thu (VND)
"totalTuitionPending": 300000000,       // Học phí chưa thu (VND)
"totalPaymentTransactions": 2850,       // Tổng giao dịch
"totalPaidPayments": 2100,              // Giao dịch đã thanh toán
"totalPendingPayments": 750             // Giao dịch chưa thanh toán
```

### Ví Dụ Request/Response

**Request**:
```bash
curl -X GET "http://localhost:8080/api/v2/admin/dashboard" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Response**:
```json
{
  "code": 200,
  "message": "Get admin dashboard successfully!",
  "result": {
    "totalStudents": 150,
    "totalTeachers": 20,
    "totalAdmins": 3,
    "totalActiveUsers": 170,
    "totalInactiveUsers": 3,
    "totalClasses": 45,
    "totalOpenClasses": 30,
    "totalClosedClasses": 15,
    "totalEnrollments": 2500,
    "totalSubjects": 80,
    "totalPrograms": 5,
    "totalDepartments": 8,
    "totalSemesters": 3,
    "totalTuitionCollected": 1500000000.0,
    "totalTuitionPending": 300000000.0,
    "totalPaymentTransactions": 2850,
    "totalPaidPayments": 2100,
    "totalPendingPayments": 750,
    "totalNotificationsSent": 5420,
    "totalUnreadNotifications": 234,
    "averageStudentsPerClass": 55,
    "averageGPA": 3.45,
    "totalFailedGrades": 145,
    "lastUpdated": "2026-03-29T14:30:00",
    "systemStatus": "ONLINE"
  }
}
```

---

## 👨‍🏫 Teacher Dashboard

### Mục Đích
Cung cấp thông tin về **lớp học, sinh viên, điểm số** của giáo viên.

### Endpoint
```http
GET /api/v2/teacher/dashboard
```

### Yêu Cầu
- Vai trò: **TEACHER**
- Token: JWT hợp lệ

### Dữ Liệu Trả Về

#### 1. Thông Tin Giáo Viên
```json
"teacherInfo": {
  "id": 1,
  "fullName": "Nguyễn Văn A",
  "email": "teacher1@school.edu.vn",
  "teacherCode": "GV001",
  "department": "Khoa Công Nghệ Thông Tin",
  "degree": "Thạc Sỹ",
  "specialization": "Lập Trình",
  "joinedDate": "2020-09-15"
}
```

#### 2. Thống Kê Lớp Học
```json
"totalClasses": 4,               // Tổng lớp dạy
"totalStudents": 180,            // Tổng sinh viên dạy
"classes": [                     // Danh sách lớp
  {
    "id": 1,
    "classCode": "IT101-01",
    "subjectName": "Lập Trình Cơ Bản",
    "room": "A101",
    "maxStudents": 50,
    "currentStudents": 48,
    "scheduleInfo": "Thứ 2,4 10:00-12:00",
    "status": "OPEN"
  }
]
```

#### 3. Thống Kê Điểm Số
```json
"totalGradesPosted": 285,        // Số điểm đã công bố
"totalGradesPending": 42,        // Số điểm chưa công bố
"totalEnrollments": 180          // Tổng enrollments
```

#### 4. Thống Kê Sinh Viên
```json
"averageClassGPA": 3.52,         // GPA trung bình lớp
"totalFailedStudents": 8,        // Sinh viên rớt
"totalExcellentStudents": 35     // Sinh viên xuất sắc
```

### Ví Dụ Request

```bash
curl -X GET "http://localhost:8080/api/v2/teacher/dashboard" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 🎒 Student Dashboard

### Mục Đích
Cung cấp thông tin **cá nhân, điểm số, lịch học, học phí** của sinh viên.

### Endpoint
```http
GET /api/v2/student/dashboard
```

### Yêu Cầu
- Vai trò: **STUDENT**
- Token: JWT hợp lệ

### Dữ Liệu Trả Về

#### 1. Thông Tin Cá Nhân
```json
"studentInfo": {
  "id": 1,
  "studentCode": "SV20210001",
  "fullName": "Trần Văn B",
  "email": "student1@school.edu.vn",
  "phone": "0987654321",
  "address": "123 Đường ABC, TP HCM",
  "enrollmentYear": 2021,
  "gpa": 3.65,
  "accumulatedCredits": 85,
  "status": "ACTIVE"
}
```

#### 2. Thống Kê Học Tập
```json
"totalEnrolledClasses": 6,       // Tổng lớp đăng ký
"totalCompletedCredits": 85,     // Tín chỉ tích lũy
"currentGPA": 3.65,              // GPA hiện tại
"studentStatus": "ACTIVE"        // Trạng thái sinh viên
```

#### 3. Thống Kê Điểm Số
```json
"averageScore": 7.85,            // Điểm trung bình
"totalPassedSubjects": 24,       // Môn đạt
"totalFailedSubjects": 1,        // Môn trượt
"recentGrades": [                // Danh sách điểm gần đây
  {
    "classCode": "IT101",
    "subjectName": "Lập Trình Cơ Bản",
    "numericGrade": 8.5,
    "letterGrade": "A",
    "credits": 3
  }
]
```

#### 4. Thông Tin Học Phí
```json
"totalTuitionFee": 10000000,     // Tổng học phí (VND)
"paidAmount": 5000000,           // Đã thanh toán (VND)
"remainingAmount": 5000000,      // Còn lại (VND)
"tuitionStatus": "PARTIAL"       // Trạng thái (PAID/PARTIAL/PENDING)
```

#### 5. Thời Khóa Biểu
```json
"upcomingClasses": 3,            // Lớp sắp tới
"nextClassName": "Lập Trình Nâng Cao",
"nextClassRoom": "A102",
"nextClassTime": "2026-03-30T10:00:00"
```

### Ví Dụ Request

```bash
curl -X GET "http://localhost:8080/api/v2/student/dashboard" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 🔐 Bảo Mật & Phân Quyền

### Token Authorization
Tất cả endpoints yêu cầu token trong header:
```
Authorization: Bearer <token>
```

### Phân Quyền
- **Admin**: Có thể xem tất cả dữ liệu
- **Teacher**: Chỉ xem dữ liệu của lớp mình dạy
- **Student**: Chỉ xem dữ liệu của chính họ

### Xử Lý Lỗi

#### 401 Unauthorized
```json
{
  "code": 401,
  "message": "Unauthorized access",
  "result": null
}
```
**Nguyên nhân**: Token hết hạn hoặc không hợp lệ

#### 403 Forbidden
```json
{
  "code": 403,
  "message": "Access denied - Admin role required",
  "result": null
}
```
**Nguyên nhân**: Người dùng không có vai trò tương ứng

#### 404 Not Found
```json
{
  "code": 404,
  "message": "Student not found!",
  "result": null
}
```
**Nguyên nhân**: Không tìm thấy người dùng

---

## 📊 So Sánh Các Dashboard

| Tính Năng | Admin | Teacher | Student |
|-----------|-------|---------|---------|
| Người dùng | ✅ | ❌ | ❌ |
| Lớp học | ✅ | ✅ | ✅ (đã đăng ký) |
| Điểm số | ✅ (tất cả) | ✅ (lớp mình) | ✅ (của mình) |
| Học phí | ✅ (tất cả) | ❌ | ✅ (của mình) |
| Thông báo | ✅ (tất cả) | ❌ | ❌ |
| Tham dự | ✅ (tất cả) | ✅ (lớp mình) | ✅ (của mình) |
| Chương trình | ✅ (tất cả) | ❌ | ✅ (của mình) |

---

## 💡 Tips & Tricks

### 1. Cache Response
Nếu cần tối ưu hiệu suất, có thể cache response trong **5-10 phút**:
```bash
# Lần đầu: Từ server
curl -i -X GET "http://localhost:8080/api/v2/admin/dashboard" \
  -H "Authorization: Bearer $TOKEN"

# Các lần tiếp theo: Từ cache (trong 10 phút)
```

### 2. Lấy Token Mới
Nếu token hết hạn, sử dụng refreshToken:
```bash
curl -X POST "http://localhost:8080/api/auth/refresh" \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "your_refresh_token"}'
```

### 3. Kiểm Tra Trạng Thái Token
```bash
curl -X GET "http://localhost:8080/api/auth/me" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📚 Tài Liệu Chi Tiết

- **Admin Dashboard**: [ADMIN_DASHBOARD_ENDPOINT.md](./ADMIN_DASHBOARD_ENDPOINT.md)
- **Teacher Dashboard**: [TEACHER_DASHBOARD_ENDPOINT.md](./TEACHER_DASHBOARD_ENDPOINT.md)
- **Student Dashboard**: [STUDENT_DASHBOARD_ENDPOINT.md](./STUDENT_DASHBOARD_ENDPOINT.md)
- **Dashboard Summary**: [DASHBOARD_ENDPOINTS_SUMMARY.md](./DASHBOARD_ENDPOINTS_SUMMARY.md)

---

## 🎯 Use Cases

### Scenario 1: Admin Kiểm Tra Tổng Quát Hệ Thống
1. Đăng nhập bằng tài khoản Admin
2. Lấy token từ response
3. Gọi `/api/v2/admin/dashboard`
4. Xem thống kê người dùng, lớp, tài chính, v.v.

### Scenario 2: Teacher Kiểm Tra Lớp Học
1. Đăng nhập bằng tài khoản Teacher
2. Lấy token từ response
3. Gọi `/api/v2/teacher/dashboard`
4. Xem danh sách lớp dạy, sinh viên, điểm số

### Scenario 3: Student Xem Thông Tin Cá Nhân
1. Đăng nhập bằng tài khoản Student
2. Lấy token từ response
3. Gọi `/api/v2/student/dashboard`
4. Xem thông tin cá nhân, điểm, học phí, lịch học

---

## 📞 Hỗ Trợ

- **Documentation**: Xem các file `.md` trong repository
- **Issues**: GitHub Issues
- **Contact**: Liên hệ team phát triển

---

**Last Updated**: March 29, 2026

