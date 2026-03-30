# 📚 Quản Lý Sinh Viên V2 - Student Management System

## 📌 Tổng Quan

**Quan Lý Sinh Viên V2** là một hệ thống quản lý sinh viên toàn diện, xây dựng bằng **Spring Boot 4.0.3** với các tính năng quản lý học tập, tài chính, và hỗ trợ giáo viên, sinh viên, và quản trị viên.

### Công Nghệ Sử Dụng
- **Backend**: Spring Boot 4.0.3 (Java 17)
- **Database**: MySQL 8.0+
- **Authentication**: Spring Security + JWT
- **Payment**: MoMo Payment Gateway
- **Storage**: MinIO (Object Storage)
- **Email**: Gmail SMTP
- **Build Tool**: Maven

---

## 🎯 Tính Năng Chính

### 1. 🔐 Xác Thực & Phân Quyền
- Đăng nhập với email và mật khẩu
- Phân vai trò: ADMIN, TEACHER, STUDENT
- JWT Token-based authentication
- OAuth2 support

### 2. 👥 Quản Lý Người Dùng
- Quản lý sinh viên, giáo viên, quản trị viên
- Cập nhật thông tin cá nhân
- Quản lý hồ sơ

### 3. 📬 Hệ Thống Thông Báo (Mới)

#### 3.1 Tính Năng Người Dùng
- **Lấy tất cả thông báo** của người dùng
- **Lấy thông báo chưa đọc** (unread)
- **Đếm số thông báo chưa đọc**
- **Xem chi tiết thông báo**
- **Đánh dấu thông báo là đã đọc**
- **Đánh dấu tất cả thông báo là đã đọc**
- **Xóa thông báo**

#### 3.2 Tính Năng Admin
- **Gửi thông báo hàng loạt (Broadcast)** theo vai trò
- **Gửi thông báo cá nhân** cho người dùng cụ thể
- Hỗ trợ các loại thông báo: GRADE, SCHEDULE, PAYMENT, SYSTEM, ATTENDANCE

### 4. 📊 Dashboard Endpoints (Mới) ✨

#### 4.1 Admin Dashboard
**Endpoint**: `GET /api/v2/admin/dashboard`

Cung cấp tổng quát **toàn bộ hệ thống**:
- 👥 Thống kê người dùng (sinh viên, giáo viên, admin, hoạt động/không hoạt động)
- 📚 Thống kê lớp học (tổng, mở, đóng, enrollments)
- 🎓 Thống kê học tập (môn học, chương trình, bộ môn, kỳ học)
- 💰 Thống kê tài chính (học phí thu/chưa thu, giao dịch)
- 🔔 Thống kê thông báo
- 📊 Thống kê nâng cao (GPA, sinh viên rớt)

#### 4.2 Teacher Dashboard
**Endpoint**: `GET /api/v2/teacher/dashboard`

Cung cấp thông tin về **lớp học, sinh viên, điểm số** của giáo viên:
- 👨‍🏫 Thông tin giáo viên, bộ môn
- 📚 Tổng lớp dạy, sinh viên, danh sách lớp
- 📝 Thống kê điểm (đã công bố, chưa công bố)
- 👥 Thống kê sinh viên (GPA, rớt, xuất sắc)
- 📊 Tham dự, lớp lớn nhất/nhỏ nhất

#### 4.3 Student Dashboard
**Endpoint**: `GET /api/v2/student/dashboard`

Cung cấp thông tin **cá nhân, điểm số, lịch học, học phí** của sinh viên:
- 👨‍🎓 Thông tin cá nhân, chương trình, bộ môn
- 📚 Thống kê học tập (GPA, tín chỉ, trạng thái)
- 📊 Điểm số gần đây, môn đạt/trượt
- ✅ Tỷ lệ tham dự, vắng, muộn
- 💰 Thông tin học phí (tổng, đã/còn lại, trạng thái)
- 📅 Lịch học sắp tới

---

## 🛠️ Công Nghệ Sử Dụng

### Backend
- **Framework**: Spring Boot 4.0.3
- **Language**: Java 17
- **Security**: Spring Security 6.0+
- **ORM**: Spring Data JPA
- **Database**: MySQL 8.0+
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito

### DevOps
- **Container**: Docker & Docker Compose
- **Database**: MySQL in Docker

---

## 📋 Yêu Cầu Hệ Thống

- Java 17 trở lên
- Maven 3.6+
- MySQL 8.0+ (hoặc Docker)
- Git

---

## 🚀 Cài Đặt & Chạy Ứng Dụng

### 1. Clone Repository
```bash
git clone <repository-url>
cd quan-ly-sinh-vien-v2
```

### 2. Cấu Hình Cơ Sở Dữ Liệu

#### Option A: Sử dụng Docker Compose (Khuyến nghị)
```bash
docker-compose up -d
```

Cấu hình sẽ được tự động từ file `compose.yaml`:
- MySQL port: 3306
- Database: `quan_ly_sinh_vien_db`
- Username: `root`
- Password: `password` (mặc định)

#### Option B: Cấu hình MySQL thủ công
```bash
# Cập nhật file application.yaml
nano src/main/resources/application.yaml
```

### 3. Cài Đặt Dependencies
```bash
./mvnw clean install
```

### 4. Chạy Ứng Dụng
```bash
./mvnw spring-boot:run
```

Ứng dụng sẽ chạy trên: `http://localhost:8080`

### 5. Chạy Tests
```bash
./mvnw test
```

---

## 📚 API Endpoints

### 1. 🔔 Notification Endpoints (Người Dùng)

**Base URL**: `/api/v2/notifications` hoặc `/api/notifications`

#### Lấy tất cả thông báo
```http
GET /api/v2/notifications
Authorization: Bearer <token>
```

**Response**:
```json
{
  "code": 200,
  "message": "Get all notifications successfully!",
  "result": [
    {
      "id": 1,
      "title": "Grade Posted",
      "content": "Your grade for Mathematics has been posted",
      "type": "GRADE",
      "isRead": false,
      "referenceId": 123,
      "referenceType": "COURSE",
      "createdAt": "2026-03-28T10:30:00"
    }
  ]
}
```

#### Lấy thông báo chưa đọc
```http
GET /api/v2/notifications/unread
Authorization: Bearer <token>
```

#### Đếm thông báo chưa đọc
```http
GET /api/v2/notifications/unread/count
Authorization: Bearer <token>
```

**Response**:
```json
{
  "code": 200,
  "message": "Get unread notification count successfully!",
  "result": 5
}
```

### 3. 📊 Dashboard Endpoints

#### 3.1 Admin Dashboard
**Endpoint**: `GET /api/v2/admin/dashboard`
**Role Required**: ADMIN

```http
GET /api/v2/admin/dashboard
Authorization: Bearer <token>
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

#### 3.2 Teacher Dashboard
**Endpoint**: `GET /api/v2/teacher/dashboard`
**Role Required**: TEACHER

```http
GET /api/v2/teacher/dashboard
Authorization: Bearer <token>
```

**Response**:
```json
{
  "code": 200,
  "message": "Get teacher dashboard successfully!",
  "result": {
    "teacherInfo": { ... },
    "totalClasses": 4,
    "totalStudents": 180,
    "classes": [ ... ],
    "totalGradesPosted": 285,
    "totalGradesPending": 42,
    "totalEnrollments": 180,
    "averageClassGPA": 3.52,
    "totalFailedStudents": 8,
    "totalExcellentStudents": 35,
    "largestClassName": "IT101-01",
    "largestClassSize": 48,
    "smallestClassName": "IT104-01",
    "smallestClassSize": 32,
    "lastUpdated": "2026-03-29T14:30:00",
    "departmentName": "Khoa Công Nghệ Thông Tin"
  }
}
```

#### 3.3 Student Dashboard
**Endpoint**: `GET /api/v2/student/dashboard`
**Role Required**: STUDENT

```http
GET /api/v2/student/dashboard
Authorization: Bearer <token>
```

**Response**:
```json
{
  "code": 200,
  "message": "Get student dashboard successfully!",
  "result": {
    "studentInfo": { ... },
    "programName": "Kỹ Sư Phần Mềm",
    "departmentName": "Khoa Công Nghệ Thông Tin",
    "enrollmentYear": 2021,
    "totalEnrolledClasses": 6,
    "totalCompletedCredits": 85,
    "currentGPA": 3.65,
    "studentStatus": "ACTIVE",
    "averageScore": 7.85,
    "totalPassedSubjects": 24,
    "totalFailedSubjects": 1,
    "recentGrades": [ ... ],
    "attendanceRate": 92.5,
    "totalAbsentDays": 2,
    "totalLateArrivals": 3,
    "totalTuitionFee": 10000000.0,
    "paidAmount": 5000000.0,
    "remainingAmount": 5000000.0,
    "tuitionStatus": "PARTIAL",
    "upcomingClasses": 3,
    "nextClassName": "Lập Trình Nâng Cao",
    "nextClassRoom": "A102",
    "nextClassTime": "2026-03-30T10:00:00",
    "lastUpdated": "2026-03-29T14:30:00"
  }
}
```


```http
GET /api/v2/notifications/{id}
Authorization: Bearer <token>
```

#### Đánh dấu thông báo là đã đọc
```http
PUT /api/v2/notifications/{id}/mark-as-read
Authorization: Bearer <token>
```

#### Đánh dấu tất cả thông báo là đã đọc
```http
PUT /api/v2/notifications/mark-all-as-read
Authorization: Bearer <token>
```

#### Xóa thông báo
```http
DELETE /api/v2/notifications/{id}
Authorization: Bearer <token>
```

### 2. 📤 Admin Notification Endpoints (Chỉ Admin)

**Base URL**: `/api/v2/admin/notifications`

**Yêu cầu**: Xác thực và vai trò ADMIN

#### Gửi thông báo hàng loạt theo vai trò
```http
POST /api/v2/admin/notifications/broadcast
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "System Maintenance Notice",
  "content": "The system will be under maintenance from 2 AM to 4 AM tonight.",
  "type": "SYSTEM",
  "referenceId": null,
  "referenceType": null,
  "targetRoles": ["ROLE_STUDENT", "ROLE_TEACHER"]
}
```

**Response**:
```json
{
  "code": 200,
  "message": "Broadcast notification sent successfully!",
  "result": 150
}
```

#### Gửi thông báo cho người dùng cụ thể
```http
POST /api/v2/admin/notifications/send
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Personal Reminder",
  "content": "Don't forget to submit your assignment by tomorrow.",
  "type": "SYSTEM",
  "referenceId": 123,
  "referenceType": "ASSIGNMENT",
  "targetUserIds": [1, 2, 3]
}
```

**Response**:
```json
{
  "code": 200,
  "message": "Notification sent to users successfully!",
  "result": 3
}
```

---

## 📁 Cấu Trúc Thư Mục

```
quan-ly-sinh-vien-v2/
├── src/
│   ├── main/
│   │   ├── java/com/example/quan_ly_sinh_vien_v2/
│   │   │   ├── Controller/
│   │   │   │   ├── NotificationController.java              ✨ (Mới)
│   │   │   │   ├── AdminDashboardController.java           ✨ (Mới)
│   │   │   │   ├── TeacherDashboardController.java         ✨ (Mới)
│   │   │   │   ├── StudentDashboardController.java         ✨ (Mới)
│   │   │   │   └── Admin/AdminNotificationController.java  ✨ (Mới)
│   │   │   ├── Service/
│   │   │   │   ├── NotificationService.java                ✨ (Mới)
│   │   │   │   └── DashboardService.java                   ✨ (Mới)
│   │   │   ├── DTO/
│   │   │   │   ├── Request/Admin/
│   │   │   │   │   ├── BroadcastNotificationRequest.java   ✨ (Mới)
│   │   │   │   │   └── SendNotificationRequest.java        ✨ (Mới)
│   │   │   │   └── Response/
│   │   │   │       ├── NotificationResponse.java           ✨ (Mới)
│   │   │   │       └── Dashboard/
│   │   │   │           ├── AdminDashboardResponse.java     ✨ (Mới)
│   │   │   │           ├── TeacherDashboardResponse.java   ✨ (Mới)
│   │   │   │           └── StudentDashboardResponse.java   ✨ (Mới)
│   │   │   ├── Repository/
│   │   │   │   ├── UserRepository.java                     ✏️ (Cập nhật)
│   │   │   │   ├── ClassEntityRepository.java              ✏️ (Cập nhật)
│   │   │   │   ├── NotificationRepository.java             ✏️ (Cập nhật)
│   │   │   │   ├── PaymentRepository.java                  ✏️ (Cập nhật)
│   │   │   │   ├── EnrollmentRepository.java               ✏️ (Cập nhật)
│   │   │   │   ├── AttendanceRepository.java               ✏️ (Cập nhật)
│   │   │   │   └── TuitionFeeRepository.java               ✏️ (Cập nhật)
│   │   │   ├── Modal/ (Entities)
│   │   │   ├── Security/
│   │   │   ├── Config/
│   │   │   ├── Exception/
│   │   │   └── Util/
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/com/example/quan_ly_sinh_vien_v2/
│           ├── Service/
│           │   └── NotificationServiceTest.java           ✨ (Mới)
│           └── Controller/
│               ├── NotificationControllerTest.java        ✨ (Mới)
│               └── Admin/AdminNotificationControllerTest.java ✨ (Mới)
├── compose.yaml
├── pom.xml
├── NOTIFICATION_ENDPOINT.md            ✨ (Mới)
├── ADMIN_NOTIFICATION_ENDPOINT.md      ✨ (Mới)
├── ADMIN_DASHBOARD_ENDPOINT.md         ✨ (Mới)
├── TEACHER_DASHBOARD_ENDPOINT.md       ✨ (Mới)
├── STUDENT_DASHBOARD_ENDPOINT.md       ✨ (Mới)
├── DASHBOARD_ENDPOINTS_SUMMARY.md      ✨ (Mới)
├── DASHBOARD_USAGE_GUIDE.md            ✨ (Mới)
└── README.md
└── README.md
```

---

## 🧪 Test Coverage

### Test Results
```
Notification Service Tests:     12 tests ✅
Notification Controller Tests:   7 tests ✅
Admin Notification Tests:       22 tests ✅
────────────────────────────────────────
Total:                         41 tests ✅

BUILD SUCCESS ✅
```

### Chạy Các Test Cụ Thể
```bash
# Tất cả tests
./mvnw test

# Test Service
./mvnw test -Dtest=NotificationServiceTest

# Test Controller
./mvnw test -Dtest=NotificationControllerTest

# Test Admin
./mvnw test -Dtest=AdminNotificationControllerTest
```

---

## 🔒 Bảo Mật

### Xác Thực
- Tất cả endpoints yêu cầu JWT Token
- Token được truyền qua header: `Authorization: Bearer <token>`

### Phân Quyền
- Endpoints thông báo người dùng: `@PreAuthorize("isAuthenticated()")`
- Endpoints Admin: `@PreAuthorize("hasRole('ADMIN')")`

### Dữ Liệu Người Dùng
- Users chỉ có thể xem thông báo của chính họ
- Admin có thể quản lý tất cả thông báo

---

## 📖 Tài Liệu Chi Tiết

- [Notification Endpoints](./NOTIFICATION_ENDPOINT.md) - API Thông Báo cho Người Dùng
- [Admin Notification Endpoints](./ADMIN_NOTIFICATION_ENDPOINT.md) - API Thông Báo cho Admin
- [Admin Dashboard](./ADMIN_DASHBOARD_ENDPOINT.md) - Tổng quát Hệ Thống
- [Teacher Dashboard](./TEACHER_DASHBOARD_ENDPOINT.md) - Dashboard Giáo Viên
- [Student Dashboard](./STUDENT_DASHBOARD_ENDPOINT.md) - Dashboard Sinh Viên
- [Dashboard Summary](./DASHBOARD_ENDPOINTS_SUMMARY.md) - Tóm tắt tất cả Dashboard
- [Dashboard Usage Guide](./DASHBOARD_USAGE_GUIDE.md) - Hướng dẫn sử dụng
- [Authorization Security Update](./AUTHORIZATION_SECURITY_UPDATE.md) - Cập nhật Bảo Mật

---

## 🤝 Đóng Góp

Để đóng góp vào dự án:

1. Fork repository
2. Tạo branch feature (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

---

## 📝 Commit Convention

Các commit message nên tuân theo chuẩn:
```
[FEATURE] - Thêm tính năng mới
[FIX] - Sửa lỗi
[REFACTOR] - Cải thiện code
[TEST] - Thêm/sửa test
[DOCS] - Cập nhật tài liệu
```

---

## 📞 Liên Hệ & Support

- **Issue Tracker**: GitHub Issues
- **Documentation**: Xem thư mục docs/

---

## 📄 Giấy Phép

Dự án này được cấp phép dưới MIT License.

---

## 🎉 Changelog

### Version 0.0.1-SNAPSHOT

#### ✨ Features
- ✅ Notification system (user endpoints)
- ✅ Admin notification system (broadcast & send)
- ✅ **Dashboard Endpoints** (Admin, Teacher, Student) ⭐ NEW
- ✅ Full test coverage (41 tests)
- ✅ Security implementation
- ✅ Comprehensive API documentation

#### 📖 Documentation
- NOTIFICATION_ENDPOINT.md
- ADMIN_NOTIFICATION_ENDPOINT.md
- **ADMIN_DASHBOARD_ENDPOINT.md** ⭐ NEW
- **TEACHER_DASHBOARD_ENDPOINT.md** ⭐ NEW
- **STUDENT_DASHBOARD_ENDPOINT.md** ⭐ NEW
- **DASHBOARD_ENDPOINTS_SUMMARY.md** ⭐ NEW
- **DASHBOARD_USAGE_GUIDE.md** ⭐ NEW
- AUTHORIZATION_SECURITY_UPDATE.md
- README.md

---

**Last Updated**: March 29, 2026

---

## 🚀 Tiếp Theo

Các tính năng sắp tới:
- [ ] WebSocket real-time notifications
- [ ] Email notifications
- [ ] SMS notifications
- [ ] Push notifications
- [ ] Notification preferences/settings
- [ ] Dashboard caching optimization
- [ ] Export dashboard data to PDF/Excel
- [ ] Notification scheduling
- [ ] Advanced notification filters


