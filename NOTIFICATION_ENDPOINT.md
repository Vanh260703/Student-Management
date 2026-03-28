# 📬 Notification Endpoint Summary

## ✅ Hoàn thành - Tất cả 19 test cases pass!

### 📁 Files Đã Tạo

#### 1. **NotificationResponse.java** (DTO)
- **Path**: `src/main/java/com/example/quan_ly_sinh_vien_v2/DTO/Response/NotificationResponse.java`
- **Mục đích**: DTO để trả về thông tin thông báo
- **Fields**:
  - `id`: ID của thông báo
  - `title`: Tiêu đề thông báo
  - `content`: Nội dung thông báo
  - `type`: Loại thông báo (GRADE, SCHEDULE, PAYMENT, SYSTEM, ATTENDANCE)
  - `isRead`: Trạng thái đã đọc/chưa đọc
  - `referenceId`: ID của đối tượng liên quan
  - `referenceType`: Loại đối tượng liên quan
  - `createdAt`: Thời gian tạo

#### 2. **NotificationService.java** (Service)
- **Path**: `src/main/java/com/example/quan_ly_sinh_vien_v2/Service/NotificationService.java`
- **Phương thức chính**:
  - `getUserNotifications(email)` - Lấy tất cả thông báo của user
  - `getUnreadNotifications(email)` - Lấy các thông báo chưa đọc
  - `getUnreadNotificationCount(email)` - Đếm số thông báo chưa đọc
  - `getNotificationById(id)` - Lấy chi tiết một thông báo
  - `markAsRead(id)` - Đánh dấu thông báo là đã đọc
  - `markAllAsRead(email)` - Đánh dấu tất cả thông báo là đã đọc
  - `deleteNotification(id)` - Xóa thông báo

#### 3. **NotificationController.java** (Controller)
- **Path**: `src/main/java/com/example/quan_ly_sinh_vien_v2/Controller/NotificationController.java`
- **Base URL**: `/api/v2/notifications` hoặc `/api/notifications`
- **Endpoints**:

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/` | Lấy tất cả thông báo |
| GET | `/unread` | Lấy thông báo chưa đọc |
| GET | `/unread/count` | Đếm số thông báo chưa đọc |
| GET | `/{id}` | Lấy chi tiết thông báo |
| PUT | `/{id}/mark-as-read` | Đánh dấu thông báo là đã đọc |
| PUT | `/mark-all-as-read` | Đánh dấu tất cả là đã đọc |
| DELETE | `/{id}` | Xóa thông báo |

#### 4. **NotificationRepository.java** (Đã Update)
- **Path**: `src/main/java/com/example/quan_ly_sinh_vien_v2/Repository/NotificationRepository.java`
- **Query Methods Mới**:
  - `findByUserOrderByCreatedAtDesc(User)` - Lấy tất cả thông báo của user
  - `findByUserOrderByCreatedAtDesc(User, Pageable)` - Với pagination
  - `findByUserAndIsReadFalseOrderByCreatedAtDesc(User)` - Lấy chưa đọc
  - `countByUserAndIsReadFalse(User)` - Đếm chưa đọc
  - `findAllByUserWithOrdering(User)` - Custom query

#### 5. **NotificationServiceTest.java** (Test Service)
- **Path**: `src/test/java/com/example/quan_ly_sinh_vien_v2/Service/NotificationServiceTest.java`
- **Test Cases** (12 test):
  - ✅ `getUserNotificationsShouldReturnAllNotificationsOrderByNewest`
  - ✅ `getUserNotificationsShouldThrowNotFoundExceptionWhenUserNotFound`
  - ✅ `getUnreadNotificationsShouldReturnOnlyUnreadNotifications`
  - ✅ `getUnreadNotificationCountShouldReturnCorrectCount`
  - ✅ `getNotificationByIdShouldReturnNotificationDetails`
  - ✅ `getNotificationByIdShouldThrowNotFoundExceptionWhenNotFound`
  - ✅ `markAsReadShouldUpdateNotificationAndReturnResponse`
  - ✅ `markAsReadShouldThrowNotFoundExceptionWhenNotificationNotFound`
  - ✅ `markAllAsReadShouldUpdateAllUnreadNotifications`
  - ✅ `markAllAsReadShouldThrowNotFoundExceptionWhenUserNotFound`
  - ✅ `deleteNotificationShouldCallRepositoryDelete`
  - ✅ `deleteNotificationShouldThrowNotFoundExceptionWhenNotFound`

#### 6. **NotificationControllerTest.java** (Test Controller)
- **Path**: `src/test/java/com/example/quan_ly_sinh_vien_v2/Controller/NotificationControllerTest.java`
- **Test Cases** (7 test):
  - ✅ `getAllNotificationsShouldReturnListOfNotifications`
  - ✅ `getUnreadNotificationsShouldReturnOnlyUnreadNotifications`
  - ✅ `getUnreadCountShouldReturnNumberOfUnreadNotifications`
  - ✅ `getNotificationByIdShouldReturnNotificationDetails`
  - ✅ `markAsReadShouldUpdateNotificationAndReturnIt`
  - ✅ `markAllAsReadShouldUpdateAllUnreadNotifications`
  - ✅ `deleteNotificationShouldRemoveNotification`

### 📊 Test Results
```
Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS ✅
```

### 🔒 Security
- Tất cả endpoints yêu cầu authentication (`@PreAuthorize("isAuthenticated()")`)
- Users chỉ có thể lấy thông báo của chính họ thông qua email

### 📝 Response Format
```json
{
  "code": 200,
  "message": "Get all notifications successfully!",
  "result": [
    {
      "id": 1,
      "title": "Grade Posted",
      "content": "Your grade for Math is posted",
      "type": "GRADE",
      "isRead": false,
      "referenceId": 123,
      "referenceType": "GRADE",
      "createdAt": "2026-03-27 00:54:42"
    }
  ]
}
```

### 🚀 Cách Sử Dụng

#### 1. Lấy tất cả thông báo
```bash
GET /api/v2/notifications
Authorization: Bearer <token>
```

**Response:**
```json
{
  "code": 200,
  "message": "Get all notifications successfully!",
  "result": [...]
}
```

#### 2. Lấy thông báo chưa đọc
```bash
GET /api/v2/notifications/unread
Authorization: Bearer <token>
```

#### 3. Đếm thông báo chưa đọc
```bash
GET /api/v2/notifications/unread/count
Authorization: Bearer <token>
```

**Response:**
```json
{
  "code": 200,
  "message": "Get unread notification count successfully!",
  "result": 5
}
```

#### 4. Lấy chi tiết thông báo
```bash
GET /api/v2/notifications/{id}
Authorization: Bearer <token>
```

#### 5. Đánh dấu thông báo là đã đọc
```bash
PUT /api/v2/notifications/{id}/mark-as-read
Authorization: Bearer <token>
```

#### 6. Đánh dấu tất cả là đã đọc
```bash
PUT /api/v2/notifications/mark-all-as-read
Authorization: Bearer <token>
```

#### 7. Xóa thông báo
```bash
DELETE /api/v2/notifications/{id}
Authorization: Bearer <token>
```

---
**Status**: ✅ Hoàn thành và Test Pass
**Date**: March 27, 2026
**Test Command**: 
```bash
mvn test -Dtest=NotificationServiceTest,NotificationControllerTest
```

