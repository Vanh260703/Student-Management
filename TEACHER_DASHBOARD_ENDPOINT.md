# Teacher Dashboard Endpoints

## 📋 Tổng Quát

Teacher Dashboard cung cấp tổng quát **lớp học, sinh viên, điểm số** của giáo viên với các thống kê chi tiết.

---

## 🔗 Base URL
```
/api/v2/teacher/dashboard
```

---

## 📊 Endpoints

### 1. Lấy Teacher Dashboard

**Endpoint:**
```http
GET /api/v2/teacher/dashboard
Authorization: Bearer <token>
```

**Yêu cầu:**
- Token JWT hợp lệ
- Vai trò: TEACHER

**Response (200 OK):**
```json
{
  "code": 200,
  "message": "Get teacher dashboard successfully!",
  "result": {
    "teacherInfo": {
      "id": 1,
      "fullName": "Nguyễn Văn A",
      "email": "teacher1@school.edu.vn",
      "teacherCode": "GV001",
      "department": "Khoa Công Nghệ Thông Tin",
      "degree": "Thạc Sỹ",
      "specialization": "Lập Trình",
      "joinedDate": "2020-09-15",
      "avatarUrl": "https://..."
    },
    "totalClasses": 4,
    "totalStudents": 180,
    "classes": [
      {
        "id": 1,
        "classCode": "IT101-01",
        "subjectName": "Lập Trình Cơ Bản",
        "room": "A101",
        "maxStudents": 50,
        "currentStudents": 48,
        "scheduleInfo": "Thứ 2,4 10:00-12:00",
        "status": "OPEN"
      },
      {
        "id": 2,
        "classCode": "IT102-01",
        "subjectName": "Lập Trình Nâng Cao",
        "room": "A102",
        "maxStudents": 45,
        "currentStudents": 42,
        "scheduleInfo": "Thứ 3,5 14:00-16:00",
        "status": "OPEN"
      }
    ],
    "totalGradesPosted": 285,
    "totalGradesPending": 42,
    "totalEnrollments": 180,
    "totalAttendanceRecords": 5600,
    "averageAttendanceRate": 89,
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

---

## 📈 Response Fields Explained

### Thông Tin Giáo Viên (teacherInfo)
| Field | Type | Mô Tả |
|-------|------|-------|
| `id` | Long | ID của giáo viên |
| `fullName` | String | Tên đầy đủ |
| `email` | String | Email công việc |
| `teacherCode` | String | Mã giáo viên |
| `department` | String | Bộ môn/Khoa |
| `degree` | String | Trình độ học vấn |
| `specialization` | String | Chuyên môn |
| `joinedDate` | LocalDate | Ngày vào làm |
| `avatarUrl` | String | URL ảnh đại diện |

### Thống Kê Lớp Học
| Field | Type | Mô Tả |
|-------|------|-------|
| `totalClasses` | Long | Tổng số lớp dạy |
| `totalStudents` | Long | Tổng số sinh viên dạy |
| `classes` | List | Danh sách lớp dạy |

### Chi Tiết Lớp Học (classes[])
| Field | Type | Mô Tả |
|-------|------|-------|
| `id` | Long | ID của lớp |
| `classCode` | String | Mã lớp học |
| `subjectName` | String | Tên môn học |
| `room` | String | Phòng học |
| `maxStudents` | Integer | Sức chứa tối đa |
| `currentStudents` | Integer | Số sinh viên hiện tại |
| `scheduleInfo` | String | Lịch học |
| `status` | String | Trạng thái (OPEN/CLOSE) |

### Thống Kê Điểm Số
| Field | Type | Mô Tả |
|-------|------|-------|
| `totalGradesPosted` | Integer | Số điểm đã công bố |
| `totalGradesPending` | Integer | Số điểm chưa công bố |
| `totalEnrollments` | Long | Tổng enrollments |

### Thống Kê Tham Dự
| Field | Type | Mô Tả |
|-------|------|-------|
| `totalAttendanceRecords` | Long | Tổng bản ghi điểm danh |
| `averageAttendanceRate` | Integer | Tỷ lệ tham dự trung bình (%) |

### Thống Kê Sinh Viên
| Field | Type | Mô Tả |
|-------|------|-------|
| `averageClassGPA` | Double | GPA trung bình lớp |
| `totalFailedStudents` | Long | Số sinh viên rớt |
| `totalExcellentStudents` | Long | Số sinh viên xuất sắc (A+, A) |

### Thông Tin Lớp
| Field | Type | Mô Tả |
|-------|------|-------|
| `largestClassName` | String | Mã lớp lớn nhất |
| `largestClassSize` | Integer | Số sinh viên lớp lớn nhất |
| `smallestClassName` | String | Mã lớp nhỏ nhất |
| `smallestClassSize` | Integer | Số sinh viên lớp nhỏ nhất |

### Thông Tin Hệ Thống
| Field | Type | Mô Tả |
|-------|------|-------|
| `lastUpdated` | LocalDateTime | Thời gian cập nhật cuối cùng |
| `departmentName` | String | Tên bộ môn/khoa |

---

## 🔐 Bảo Mật

### Xác Thực
- Yêu cầu **JWT Token** hợp lệ
- Token được gửi qua header: `Authorization: Bearer <token>`

### Phân Quyền
- Chỉ **TEACHER** có thể truy cập endpoint này
- Mỗi giáo viên chỉ xem được dữ liệu của chính họ
- Annotation: `@PreAuthorize("hasRole('TEACHER')")`

---

## ❌ Error Responses

### 401 Unauthorized
```json
{
  "code": 401,
  "message": "Unauthorized access",
  "result": null
}
```
**Nguyên nhân**: Token không hợp lệ hoặc hết hạn

### 403 Forbidden
```json
{
  "code": 403,
  "message": "Access denied - Teacher role required",
  "result": null
}
```
**Nguyên nhân**: Người dùng không có vai trò TEACHER

### 404 Not Found
```json
{
  "code": 404,
  "message": "Teacher not found!",
  "result": null
}
```
**Nguyên nhân**: Không tìm thấy hồ sơ giáo viên

---

## 📝 Ghi Chú

1. **Dữ Liệu Cá Nhân**: Giáo viên chỉ thấy dữ liệu của lớp mình dạy
2. **Tự động Cập Nhật**: Dashboard được tính toán theo thời gian thực
3. **Hiệu Suất**: Có thể cache kết quả trong 5-10 phút
4. **GPA**: Được tính từ trung bình tất cả điểm số đã công bố

---

## 🔄 Ví Dụ Request/Response Hoàn Chỉnh

### Request
```bash
curl -X GET "http://localhost:8080/api/v2/teacher/dashboard" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Response
```json
HTTP/1.1 200 OK
Content-Type: application/json

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
    "totalAttendanceRecords": 5600,
    "averageAttendanceRate": 89,
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

---

**Last Updated**: March 29, 2026

