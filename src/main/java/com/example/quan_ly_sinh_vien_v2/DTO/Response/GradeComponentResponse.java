package com.example.quan_ly_sinh_vien_v2.DTO.Response;

import com.example.quan_ly_sinh_vien_v2.Modal.Enum.GradeComponentType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeComponentResponse {
    private Long classId;
    private GradeComponentType type;
    private Integer weight;
    private String name;
}
