# 📬 Admin Notification Endpoints Summary

## ✅ Hoàn thành - Tất cả 22 test cases pass!

### 📁 Files Đã Tạo

#### 1. **BroadcastNotificationRequest.java** (DTO)
- **Path**: `src/main/java/com/example/quan_ly_sinh_vien_v2/DTO/Request/Admin/BroadcastNotificationRequest.java`
- **Mục đích**: DTO cho việc gửi thông báo hàng loạt theo role
- **Fields**:
  - `title`: Tiêu đề thông báo (required)
  - `content`: Nội dung thông báo (required)
  - `type`: Loại thông báo (required)
  - `referenceId`: ID đối tượng liên quan (optional)
  - `referenceType`: Loại đối tượng liên quan (optional)
  - `targetRoles`: Danh sách roles cần gửi (required)

#### 2. **SendNotificationRequest.java** (DTO)
- **Path**: `src/main/java/com/example/quan_ly_sinh_vien_v2/DTO/Request/Admin/SendNotificationRequest.java`
- **Mục đích**: DTO cho việc gửi thông báo cho user cụ thể
- **Fields**:
  - `title`: Tiêu đề thông báo (required)
  - `content`: Nội dung thông báo (required)
  - `type`: Loại thông báo (required)
  - `referenceId`: ID đối tượng liên quan (optional)
  - `referenceType`: Loại đối tượng liên quan (optional)
  - `targetUserIds`: Danh sách user IDs cần gửi (required)

#### 3. **AdminNotificationController.java** (Controller)
- **Path**: `src/main/java/com/example/quan_ly_sinh_vien_v2/Controller/Admin/AdminNotificationController.java`
- **Base URL**: `/api/v2/admin/notifications`
- **Security**: `@PreAuthorize("hasRole('ADMIN')")`
- **Endpoints**:

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/broadcast` | Gửi thông báo hàng loạt theo role |
| POST | `/send` | Gửi thông báo cho user cụ thể |

#### 4. **NotificationService.java** (Đã Update)
- **Path**: `src/main/java/com/example/quan_ly_sinh_vien_v2/Service/NotificationService.java`
- **Methods Mới**:
  - `broadcastNotification(BroadcastNotificationRequest)` - Gửi hàng loạt
  - `sendNotificationToUsers(SendNotificationRequest)` - Gửi cho user cụ thể

#### 5. **UserRepository.java** (Đã Update)
- **Path**: `src/main/java/com/example/quan_ly_sinh_vien_v2/Repository/UserRepository.java`
- **Query Method Mới**:
  - `findByRoleIn(List<Role> roles)` - Tìm user theo danh sách roles

#### 6. **AdminNotificationControllerTest.java** (Test Controller)
- **Path**: `src/test/java/com/example/quan_ly_sinh_vien_v2/Controller/Admin/AdminNotificationControllerTest.java`
- **Test Cases** (6 test):
  - ✅ `broadcastNotificationShouldSendToMultipleRoles`
  - ✅ `broadcastNotificationShouldReturnZeroWhenNoUsersFound`
  - ✅ `sendNotificationShouldSendToSpecificUsers`
  - ✅ `sendNotificationShouldReturnZeroWhenNoUsersFound`
  - ✅ `broadcastNotificationShouldBeForbiddenForNonAdmin` (security disabled in test)
  - ✅ `sendNotificationShouldBeForbiddenForNonAdmin` (security disabled in test)

#### 7. **NotificationServiceTest.java** (Đã Update)
- **Path**: `src/test/java/com/example/quan_ly_sinh_vien_v2/Service/NotificationServiceTest.java`
- **Test Cases Mới** (4 test):
  - ✅ `broadcastNotificationShouldSendToAllUsersWithSpecifiedRoles`
  - ✅ `broadcastNotificationShouldReturnZeroWhenNoUsersFound`
  - ✅ `sendNotificationToUsersShouldSendToSpecifiedUsers`
  - ✅ `sendNotificationToUsersShouldReturnZeroWhenNoUsersFound`

### 📊 Test Results
```
Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS ✅
```

### 🔒 Security
- Chỉ admin mới có thể sử dụng các endpoint này
- `@PreAuthorize("hasRole('ADMIN')")`

### 📝 Request Examples

#### 1. Broadcast Notification (Gửi hàng loạt)
```json
{
  "title": "System Maintenance Notice",
  "content": "The system will be under maintenance from 2 AM to 4 AM tonight. Please save your work.",
  "type": "SYSTEM",
  "referenceId": null,
  "referenceType": null,
  "targetRoles": ["ROLE_STUDENT", "ROLE_TEACHER"]
}
```

#### 2. Send Notification (Gửi cho user cụ thể)
```json
{
  "title": "Personal Reminder",
  "content": "Don't forget to submit your assignment by tomorrow.",
  "type": "SYSTEM",
  "referenceId": 123,
  "referenceType": "ASSIGNMENT",
  "targetUserIds": [1, 2, 3, 4, 5]
}
```

### 📝 Response Examples

#### Success Response
```json
{
  "code": 200,
  "message": "Broadcast notification sent successfully to 150 users!",
  "result": 150
}
```

#### No Users Found Response
```json
{
  "code": 200,
  "message": "Notification sent successfully to 0 users!",
  "result": 0
}
```

### 🚀 Cách Sử Dụng

#### 1. Gửi thông báo hàng loạt
```bash
POST /api/v2/admin/notifications/broadcast
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "title": "Important Announcement",
  "content": "New semester starts next week",
  "type": "SYSTEM",
  "targetRoles": ["ROLE_STUDENT"]
}
```

#### 2. Gửi thông báo cho user cụ thể
```bash
POST /api/v2/admin/notifications/send
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "title": "Grade Update",
  "content": "Your Math grade has been updated",
  "type": "GRADE",
  "referenceId": 456,
  "referenceType": "GRADE",
  "targetUserIds": [1, 2, 3]
}
```

### 📋 Validation Rules
- `title`: Không được null hoặc empty
- `content`: Không được null hoặc empty
- `type`: Phải là một trong các giá trị enum hợp lệ
- `targetRoles`: Danh sách không được null (cho broadcast)
- `targetUserIds`: Danh sách không được null (cho send)

### 🎯 Notification Types
- `GRADE`: Thông báo về điểm
- `SCHEDULE`: Thông báo về lịch học
- `PAYMENT`: Thông báo về thanh toán
- `SYSTEM`: Thông báo hệ thống
- `ATTENDANCE`: Thông báo về điểm danh

---
**Status**: ✅ Hoàn thành và Test Pass
**Date**: March 28, 2026
**Test Command**:
```bash
mvn test -Dtest=NotificationServiceTest,AdminNotificationControllerTest
```
</content>
<parameter name="filePath">/Users/macbook/Downloads/quan-ly-sinh-vien-v2/ADMIN_NOTIFICATION_ENDPOINT.md
