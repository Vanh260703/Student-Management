package com.example.quan_ly_sinh_vien_v2.Exception.Auth;

public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}
