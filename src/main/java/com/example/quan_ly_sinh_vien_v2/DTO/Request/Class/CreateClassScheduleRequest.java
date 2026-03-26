package com.example.quan_ly_sinh_vien_v2.DTO.Request.Class;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class CreateClassScheduleRequest {
    @NotEmpty(message = "Schedules must not be empty")
    private List<ScheduleItem> schedules;

    @Getter
    @Setter
    public static class ScheduleItem {

        @NotNull
        private Integer dayOfWeek;

        @NotNull
        private Integer startPeriod;

        @NotNull
        private Integer endPeriod;

        @NotBlank
        private String room;

        @NotNull
        private LocalDate startWeek;

        @NotNull
        private LocalDate endWeek;
    }
}
