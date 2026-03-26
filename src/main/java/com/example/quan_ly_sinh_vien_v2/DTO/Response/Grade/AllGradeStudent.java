package com.example.quan_ly_sinh_vien_v2.DTO.Response.Grade;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.GradeComponentType;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.LetterGrade;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.SemesterName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AllGradeStudent {
    private StudentInfo student;
    private List<SemesterInfo> semesters;
    private SummaryInfo summary;

    @Getter
    @Setter
    public static class StudentInfo {
        private String studentCode;
        private String name;
    }

    @Getter
    @Setter
    public static class SemesterInfo {
        private Long semesterId;
        private SemesterName semesterName;
        private List<CourseInfo> courses;
    }

    @Getter
    @Setter
    public static class CourseInfo {
        private Long classId;
        private String classCode;
        private String subjectName;
        private Integer credits;

        private Map<GradeComponentType, Double> grades;

        private Double finalScore;
        private LetterGrade finalLetterGrade;
        private Boolean isPassed;
        private Boolean isPublished;
    }

    @Getter
    @Setter
    public static class SummaryInfo {
        private Float gpa;
        private Integer totalCredits;
        private Integer passedCredits;
    }
}

