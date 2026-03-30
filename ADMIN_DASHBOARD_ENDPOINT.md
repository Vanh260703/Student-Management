# Admin Dashboard Endpoints

## 📋 Tổng Quát

Admin Dashboard cung cấp tổng quát **toàn bộ hệ thống** với các thống kê chi tiết về người dùng, lớp học, học tập và tài chính.

---

## 🔗 Base URL
```
/api/v2/admin/dashboard
```

---

## 📊 Endpoints

### 1. Lấy Admin Dashboard

**Endpoint:**
```http
GET /api/v2/admin/dashboard
Authorization: Bearer <token>
```

**Yêu cầu:**
- Token JWT hợp lệ
- Vai trò: ADMIN

**Response (200 OK):**
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
    "totalTuitionCollected": 1500000000.00,
    "totalTuitionPending": 300000000.00,
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

## 📈 Response Fields Explained

### Thống Kê Người Dùng
| Field | Type | Mô Tả |
|-------|------|-------|
| `totalStudents` | Long | Tổng số sinh viên trong hệ thống |
| `totalTeachers` | Long | Tổng số giáo viên trong hệ thống |
| `totalAdmins` | Long | Tổng số quản trị viên trong hệ thống |
| `totalActiveUsers` | Long | Tổng số người dùng hoạt động |
| `totalInactiveUsers` | Long | Tổng số người dùng không hoạt động |

### Thống Kê Lớp Học
| Field | Type | Mô Tả |
|-------|------|-------|
| `totalClasses` | Long | Tổng số lớp học |
| `totalOpenClasses` | Long | Số lớp học đang mở |
| `totalClosedClasses` | Long | Số lớp học đã đóng |
| `totalEnrollments` | Long | Tổng số đơn đăng ký |

### Thống Kê Học Tập
| Field | Type | Mô Tả |
|-------|------|-------|
| `totalSubjects` | Long | Tổng số môn học |
| `totalPrograms` | Long | Tổng số chương trình đào tạo |
| `totalDepartments` | Long | Tổng số bộ môn/khoa |
| `totalSemesters` | Long | Tổng số kỳ học |

### Thống Kê Tài Chính
| Field | Type | Mô Tả |
|-------|------|-------|
| `totalTuitionCollected` | Double | Tổng học phí đã thu (VND) |
| `totalTuitionPending` | Double | Tổng học phí chưa thu (VND) |
| `totalPaymentTransactions` | Long | Tổng giao dịch thanh toán |
| `totalPaidPayments` | Long | Số giao dịch đã thanh toán |
| `totalPendingPayments` | Long | Số giao dịch chưa thanh toán |

### Thống Kê Thông Báo
| Field | Type | Mô Tả |
|-------|------|-------|
| `totalNotificationsSent` | Long | Tổng thông báo đã gửi |
| `totalUnreadNotifications` | Long | Tổng thông báo chưa đọc |

### Thống Kê Nâng Cao
| Field | Type | Mô Tả |
|-------|------|-------|
| `averageStudentsPerClass` | Integer | Trung bình số sinh viên/lớp |
| `averageGPA` | Double | GPA trung bình toàn hệ thống |
| `totalFailedGrades` | Long | Tổng số sinh viên rớt |

### Thông Tin Hệ Thống
| Field | Type | Mô Tả |
|-------|------|-------|
| `lastUpdated` | LocalDateTime | Thời gian cập nhật cuối cùng |
| `systemStatus` | String | Trạng thái hệ thống (ONLINE/OFFLINE) |

---

## 🔐 Bảo Mật

### Xác Thực
- Yêu cầu **JWT Token** hợp lệ
- Token được gửi qua header: `Authorization: Bearer <token>`

### Phân Quyền
- Chỉ **ADMIN** có thể truy cập endpoint này
- Annotation: `@PreAuthorize("hasRole('ADMIN')")`

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
  "message": "Access denied - Admin role required",
  "result": null
}
```
**Nguyên nhân**: Người dùng không có vai trò ADMIN

---

## 📝 Ghi Chú

1. **Tự động Cập Nhật**: Dashboard được tính toán theo thời gian thực khi API được gọi
2. **Hiệu Suất**: Có thể cache kết quả trong 5-10 phút để tối ưu hóa hiệu suất
3. **Tiền Tệ**: Tất cả số tiền được tính bằng VND (Đồng Việt Nam)
4. **GPA**: Được tính từ trung bình tất cả điểm số đã công bố

---

## 🔄 Ví Dụ Request/Response Hoàn Chỉnh

### Request
```bash
curl -X GET "http://localhost:8080/api/v2/admin/dashboard" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Response
```json
HTTP/1.1 200 OK
Content-Type: application/json

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

**Last Updated**: March 29, 2026

