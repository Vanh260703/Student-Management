package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProgramResponse {
    private ProgramInfo program;
    private List<SubjectBySemester> subjectsBySemester;

    @Getter
    @Setter
    public static class ProgramInfo {
        private Long id;
        private String code;
        private String name;
        private Integer totalCredits;
        private Integer durationYears;
    }

    @Getter
    @Setter
    public static class PrerequisiteInfo {
        private Long id;
        private String code;
        private String name;
    }

    @Getter
    @Setter
    public static class SubjectInfo {
        private Long id;
        private String code;
        private String name;
        private Integer credits;
        private Boolean isRequired;
        private PrerequisiteInfo prerequisite;
    }

    @Getter
    @Setter
    public static class SubjectBySemester {
        private Integer semester;
        private List<SubjectInfo> subjectsBySemester;
    }
}
