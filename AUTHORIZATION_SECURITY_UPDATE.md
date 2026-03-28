# Authorization Security Update - Notification Endpoints

## Status: COMPLETE ✅

Tất cả 27 test cases đều pass!

### Changes Made

#### 1. NotificationService.java Updates
- Updated `getNotificationById(String username, Long id)` - Add ownership check
- Updated `markAsRead(String username, Long id)` - Add ownership check
- Both throw `AuthorizationDeniedException` if user is not the owner

#### 2. NotificationController.java Updates
- Updated `getNotificationById()` - Pass username from @AuthenticationPrincipal
- Updated `markAsRead()` - Pass username from @AuthenticationPrincipal

#### 3. Test Cases Added
- `getNotificationByIdShouldReturn403WhenAccessingOtherUserNotification`
- `markAsReadShouldReturn403WhenAccessingOtherUserNotification`
- Service level tests for AuthorizationDeniedException

### Test Results
```
Tests run: 27, Failures: 0, Errors: 0
BUILD SUCCESS
```

### Security Features
- Ownership verification on notification view
- Ownership verification on notification update
- 403 Forbidden response for unauthorized access
- 404 Not Found for non-existent notifications

