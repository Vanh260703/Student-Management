package com.example.quan_ly_sinh_vien_v2.Util;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;

import java.text.Normalizer;

public class GenerateSchoolEmail {
    public static String generateSchoolEmail(String fullName, String code, Role role) {
        // Chuẩn hóa tên (bỏ dấu, trim khoảng trắng)
        String normalizedName = Normalizer.normalize(fullName, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .trim()
                .toLowerCase();

        String[] parts = normalizedName.split("\\s+");

        // Tên (từ cuối)
        String lastName = parts[parts.length - 1];

        // Lấy chữ cái đầu của họ + tên đệm
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            initials.append(parts[i].charAt(0));
        }

        String shortCode = null;
        // Bỏ 2 ký tự đầu của studentCode
        if (role == Role.ROLE_STUDENT) {
            shortCode = code.substring(2);
        } else if (role == Role.ROLE_TEACHER) {
            shortCode = code.substring(4);
        }

        return lastName + "." + initials + shortCode + "@edu.vn";
    }
}
