# Student Dashboard Endpoints

## 📋 Tổng Quát

Student Dashboard cung cấp tổng quát **thông tin cá nhân, điểm số, lịch học, học phí** của sinh viên.

---

## 🔗 Base URL
```
/api/v2/student/dashboard
```

---

## 📊 Endpoints

### 1. Lấy Student Dashboard

**Endpoint:**
```http
GET /api/v2/student/dashboard
Authorization: Bearer <token>
```

**Yêu cầu:**
- Token JWT hợp lệ
- Vai trò: STUDENT

**Response (200 OK):**
```json
{
  "code": 200,
  "message": "Get student dashboard successfully!",
  "result": {
    "studentInfo": {
      "id": 1,
      "studentCode": "SV20210001",
      "fullName": "Trần Văn B",
      "email": "student1@school.edu.vn",
      "personalEmail": "tranb@gmail.com",
      "phone": "0987654321",
      "gender": "MALE",
      "dateOfBirth": "2003-05-15",
      "address": "123 Đường ABC, TP HCM",
      "avatarUrl": "https://...",
      "enrollmentYear": 2021,
      "className": "IT-K21",
      "gpa": 3.65,
      "accumulatedCredits": 85,
      "status": "ACTIVE"
    },
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
    "recentGrades": [
      {
        "classCode": "IT101",
        "subjectName": "Lập Trình Cơ Bản",
        "numericGrade": 8.5,
        "letterGrade": "A",
        "credits": 3
      },
      {
        "classCode": "IT102",
        "subjectName": "Cấu Trúc Dữ Liệu",
        "numericGrade": 7.8,
        "letterGrade": "B+",
        "credits": 4
      }
    ],
    "attendanceRate": 92.5,
    "totalAbsentDays": 2,
    "totalLateArrivals": 3,
    "tuitionInfo": {
      "totalAmount": 10000000.0,
      "paidAmount": 5000000.0,
      "remainingAmount": 5000000.0,
      "paidDate": "2026-03-15"
    },
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

---

## 📈 Response Fields Explained

### Thông Tin Sinh Viên (studentInfo)
| Field | Type | Mô Tả |
|-------|------|-------|
| `id` | Long | ID của sinh viên |
| `studentCode` | String | Mã sinh viên |
| `fullName` | String | Tên đầy đủ |
| `email` | String | Email trường |
| `personalEmail` | String | Email cá nhân |
| `phone` | String | Số điện thoại |
| `gender` | String | Giới tính |
| `dateOfBirth` | LocalDate | Ngày sinh |
| `address` | String | Địa chỉ |
| `avatarUrl` | String | URL ảnh đại diện |
| `enrollmentYear` | Integer | Năm nhập học |
| `className` | String | Lớp |
| `gpa` | Double | GPA hiện tại |
| `accumulatedCredits` | Integer | Tín chỉ tích lũy |
| `status` | String | Trạng thái (ACTIVE/INACTIVE) |

### Thông Tin Chương Trình
| Field | Type | Mô Tả |
|-------|------|-------|
| `programName` | String | Tên chương trình đào tạo |
| `departmentName` | String | Tên bộ môn/khoa |
| `enrollmentYear` | Integer | Năm nhập học |

### Thống Kê Học Tập
| Field | Type | Mô Tả |
|-------|------|-------|
| `totalEnrolledClasses` | Integer | Tổng lớp đã đăng ký |
| `totalCompletedCredits` | Integer | Tín chỉ tích lũy |
| `currentGPA` | Double | GPA hiện tại |
| `studentStatus` | String | Trạng thái sinh viên |

### Thống Kê Điểm Số
| Field | Type | Mô Tả |
|-------|------|-------|
| `averageScore` | Double | Điểm trung bình |
| `totalPassedSubjects` | Integer | Số môn học đạt |
| `totalFailedSubjects` | Integer | Số môn học trượt |
| `recentGrades` | List | Danh sách điểm gần đây |

### Chi Tiết Điểm Số (recentGrades[])
| Field | Type | Mô Tả |
|-------|------|-------|
| `classCode` | String | Mã lớp học |
| `subjectName` | String | Tên môn học |
| `numericGrade` | Double | Điểm số (0-10) |
| `letterGrade` | String | Xếp loại (A, B+, B, ...) |
| `credits` | Integer | Số tín chỉ |

### Thống Kê Tham Dự
| Field | Type | Mô Tả |
|-------|------|-------|
| `attendanceRate` | Double | Tỷ lệ tham dự (%) |
| `totalAbsentDays` | Integer | Số ngày vắng |
| `totalLateArrivals` | Integer | Số lần muộn |

### Thông Tin Học Phí (tuitionInfo)
| Field | Type | Mô Tả |
|-------|------|-------|
| `totalAmount` | Double | Tổng học phí (VND) |
| `paidAmount` | Double | Số tiền đã thanh toán (VND) |
| `remainingAmount` | Double | Số tiền còn lại (VND) |
| `paidDate` | LocalDate | Ngày thanh toán cuối cùng |

### Tóm Tắt Học Phí
| Field | Type | Mô Tả |
|-------|------|-------|
| `totalTuitionFee` | Double | Tổng học phí |
| `paidAmount` | Double | Đã thanh toán |
| `remainingAmount` | Double | Còn lại |
| `tuitionStatus` | String | Trạng thái (PAID/PARTIAL/PENDING) |

### Thời Khóa Biểu
| Field | Type | Mô Tả |
|-------|------|-------|
| `upcomingClasses` | Integer | Số lớp sắp tới |
| `nextClassName` | String | Tên lớp tiếp theo |
| `nextClassRoom` | String | Phòng học tiếp theo |
| `nextClassTime` | LocalDateTime | Thời gian lớp tiếp theo |

### Thông Tin Hệ Thống
| Field | Type | Mô Tả |
|-------|------|-------|
| `lastUpdated` | LocalDateTime | Thời gian cập nhật cuối cùng |

---

## 🔐 Bảo Mật

### Xác Thực
- Yêu cầu **JWT Token** hợp lệ
- Token được gửi qua header: `Authorization: Bearer <token>`

### Phân Quyền
- Chỉ **STUDENT** có thể truy cập endpoint này
- Mỗi sinh viên chỉ xem được dữ liệu của chính họ
- Annotation: `@PreAuthorize("hasRole('STUDENT')")`

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
  "message": "Access denied - Student role required",
  "result": null
}
```
**Nguyên nhân**: Người dùng không có vai trò STUDENT

### 404 Not Found
```json
{
  "code": 404,
  "message": "Student not found!",
  "result": null
}
```
**Nguyên nhân**: Không tìm thấy hồ sơ sinh viên

---

## 📝 Ghi Chú

1. **Dữ Liệu Cá Nhân**: Sinh viên chỉ thấy dữ liệu của chính họ
2. **Tự động Cập Nhật**: Dashboard được tính toán theo thời gian thực
3. **Hiệu Suất**: Có thể cache kết quả trong 5-10 phút
4. **Tiền Tệ**: Tất cả số tiền được tính bằng VND
5. **Trạng Thái Học Phí**:
   - **PAID**: Đã thanh toán toàn bộ
   - **PARTIAL**: Thanh toán một phần
   - **PENDING**: Chưa thanh toán

---

## 🔄 Ví Dụ Request/Response Hoàn Chỉnh

### Request
```bash
curl -X GET "http://localhost:8080/api/v2/student/dashboard" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Response
```json
HTTP/1.1 200 OK
Content-Type: application/json

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
    "tuitionInfo": { ... },
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

---

**Last Updated**: March 29, 2026

