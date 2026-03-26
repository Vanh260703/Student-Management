package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudentTimetableResponse {
    private List<DaySchedule> timetable;

    @Getter
    @Setter
    public static class DaySchedule {
        private Integer dayOfWeek;
        private List<ClassItem> classes;
    }

    @Getter
    @Setter
    public static class ClassItem {
        private Long classId;
        private String classCode;
        private String subjectName;
        private String room;
        private String teacherName;
        private Integer startPeriod;
        private Integer endPeriod;
    }
}
