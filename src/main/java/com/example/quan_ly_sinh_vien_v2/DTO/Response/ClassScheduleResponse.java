package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ClassScheduleResponse {
    @Getter
    @Setter
    public static class ScheduleInfo {
        private Integer dayOfWeek;
        private Integer startPeriod;
        private Integer endPeriod;
        private String room;
        private LocalDate startWeek;
        private LocalDate endWeek;
    }

    List<ScheduleInfo> schedules;
}
