package com.example.quan_ly_sinh_vien_v2;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.LetterGrade;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.text.Normalizer;

public class Helper {
    public static String normalizeFullName(String fullName) {
        if (fullName == null) return "";

        // 1. Convert to lowercase + trim
        String normalized = fullName.trim().toLowerCase();

        // 2. Chuẩn hoá Unicode (tách dấu)
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);

        // 3. Xoá toàn bộ dấu tiếng Việt
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // 4. Đổi đ -> d
        normalized = normalized.replace("đ", "d");

        // 5. Xoá tất cả ký tự không phải chữ hoặc số
        normalized = normalized.replaceAll("[^a-z0-9]", "");

        // 6. Viết liền tất cả các ký tự
        normalized = normalized.replace(" ", "");

        return normalized;
    }

    public static LetterGrade convertScoreToLetterGrade(Double score) {
        LetterGrade letterGrade = null;

        if (score < 4) {
            letterGrade = LetterGrade.F;
        } else if (score >= 4 && score < 6) {
            letterGrade = LetterGrade.D;
        } else if (score >= 6 && score < 7) {
            letterGrade = LetterGrade.C;
        } else if (score >= 7 && score < 9) {
            letterGrade = LetterGrade.B;
        } else {
            letterGrade = LetterGrade.A;
        }

        return letterGrade;
    }

    public static String getCellString(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        return cell.getStringCellValue().trim();
    }
}
