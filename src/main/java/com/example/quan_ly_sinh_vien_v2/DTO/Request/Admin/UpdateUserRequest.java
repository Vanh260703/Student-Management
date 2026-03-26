package com.example.quan_ly_sinh_vien_v2.DTO.Request.Admin;

import lombok.Getter;

@Getter
public class UpdateUserRequest {
    private String fullName;
    private String phone;
    private String avatarUrl;
}
