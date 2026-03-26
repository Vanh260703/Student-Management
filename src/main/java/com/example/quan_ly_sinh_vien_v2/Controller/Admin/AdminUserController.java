package com.example.quan_ly_sinh_vien_v2.Controller.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.UpdateUserRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.APIResponse;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.UserResponse;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;
import com.example.quan_ly_sinh_vien_v2.Service.Admin.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    // [GET] api/v2/admin/users
    @GetMapping()
    public ResponseEntity<?> users(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Role role
            ) {
        List<UserResponse> users = adminUserService.getUsers(search, role);

        APIResponse response = new APIResponse(
                200,
                "Get users success!",
                users
        );

        return ResponseEntity.ok(response);
    }

    // [GET] api/v2/admin/users/:userId
    @GetMapping("/{userId}")
    public ResponseEntity<?> getDetailUser(@PathVariable Long userId) {
        UserResponse user = adminUserService.detailUser(userId);

        APIResponse response = new APIResponse(
                200,
                "Get detail user success!",
                user
        );

        return ResponseEntity.ok(response);
    }

    // [PUT] api/v2/admin/users/:userId
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = adminUserService.updateUser(userId, request);

        APIResponse response = new APIResponse(
                200,
                "Update user success!",
                user
        );

        return ResponseEntity.ok(response);
    }

    // [PATCH] api/v2/admin/users/:userId
    @PatchMapping("/{userId}")
    public ResponseEntity<?> toggleActiveUser(@PathVariable Long userId) {
        adminUserService.toggleActiveUser(userId);

        APIResponse response = new APIResponse(
                200,
                "Disable user success!",
                null
        );

        return ResponseEntity.ok(response);
    }

    // [DELETE] api/v2/admin/users/:userId
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);

        APIResponse response = new APIResponse(
                200,
                "Delete user success!",
                null
        );

        return ResponseEntity.ok(response);
    }
}
