package com.example.quan_ly_sinh_vien_v2.Service.Admin;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin.UpdateUserRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Response.UserResponse;
import com.example.quan_ly_sinh_vien_v2.Exception.NotFoundException;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Helper;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;
import com.example.quan_ly_sinh_vien_v2.Repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {
    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get users
    public List<UserResponse> getUsers(String search, Role role) {
        String keyword = null;

        if (search != null && !search.trim().isEmpty()) {
            keyword = search.trim().toLowerCase();
        }

        List<User> users = userRepository.searchUsers(keyword, role);

        return users.stream().map(UserResponse::from).toList();
    }

    // Get detail user
    public UserResponse detailUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));

        return UserResponse.from(user);
    }

    // Update user
    public UserResponse updateUser(Long userId, @Valid UpdateUserRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));

        if (request.getFullName() == null && request.getAvatarUrl() == null && request.getPhone() == null) {
            throw new UpdateFailException("Update user fail!");
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
            user.setNormalizeName(Helper.normalizeFullName(request.getFullName()));
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        userRepository.save(user);

        return UserResponse.from(user);
    }

    // Disable user
    @Transactional
    public void toggleActiveUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));

        user.setIsActive(!user.getIsActive());
    }


    // Soft delete user
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found!"));

        user.setIsDelete(true);
    }
}
