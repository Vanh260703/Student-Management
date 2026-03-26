package com.example.quan_ly_sinh_vien_v2.Exception.Auth;

public class JwtExpiredException extends RuntimeException {
    public JwtExpiredException(String message) {
        super(message);
    }
}
