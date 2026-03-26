package com.example.quan_ly_sinh_vien_v2.Util;

import com.example.quan_ly_sinh_vien_v2.Repository.StudentProfileRepository;

import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

public class GenerateCode {
    public static String generateStudentCode(StudentProfileRepository repository) {

        String code;

        do {
            int year = Year.now().getValue();
            int randomNumber = ThreadLocalRandom.current().nextInt(1000, 10000);
            code = year + String.valueOf(randomNumber);
        } while (repository.existsStudentProfileByStudentCode(code));

        return code;
    }

    public static String generateTeacherCode() {

        int year = Year.now().getValue();
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);

        return "GV" + year + random;
    }

}
