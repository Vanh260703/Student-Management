package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Department.CreateDepartmentRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Department.UpdateDepartmentRequest;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Department;
import com.example.quan_ly_sinh_vien_v2.Repository.DepartmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.StudentProfileRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.TeacherProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private TeacherProfileRepository teacherProfileRepository;
    @Mock
    private StudentProfileRepository studentProfileRepository;

    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        departmentService = new DepartmentService(
                departmentRepository,
                teacherProfileRepository,
                studentProfileRepository
        );
    }

    @Test
    void createDepartmentShouldPersistWhenCodeAndNamePresent() {
        CreateDepartmentRequest request = new CreateDepartmentRequest();
        ReflectionTestUtils.setField(request, "code", "CNTT");
        ReflectionTestUtils.setField(request, "name", "Cong nghe thong tin");
        ReflectionTestUtils.setField(request, "description", "Mo ta");

        when(departmentRepository.existsDepartmentByCode("CNTT")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Department department = departmentService.createDepartment(request);

        verify(departmentRepository).save(any(Department.class));
        assertEquals("CNTT", department.getCode());
        assertEquals("Cong nghe thong tin", department.getName());
    }

    @Test
    void updateDepartmentShouldRejectWhenNoFieldProvided() {
        Department department = new Department();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        UpdateDepartmentRequest request = new UpdateDepartmentRequest();

        assertThrows(UpdateFailException.class, () -> departmentService.updateDepartment(1L, request));
    }
}
