package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Subject.CreateSubjectRequest;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Department;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Subject;
import com.example.quan_ly_sinh_vien_v2.Repository.DepartmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.ProgramSubjectRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private ProgramSubjectRepository programSubjectRepository;

    private SubjectService subjectService;

    @BeforeEach
    void setUp() {
        subjectService = new SubjectService(
                subjectRepository,
                departmentRepository,
                programSubjectRepository
        );
    }

    @Test
    void createSubjectShouldPersistNormalizedNameAndStatus() {
        Department department = new Department();
        ReflectionTestUtils.setField(department, "id", 1L);

        CreateSubjectRequest request = new CreateSubjectRequest();
        ReflectionTestUtils.setField(request, "departmentId", 1L);
        ReflectionTestUtils.setField(request, "code", "IT201");
        ReflectionTestUtils.setField(request, "name", "Lap trinh Java");
        ReflectionTestUtils.setField(request, "credits", 3);
        ReflectionTestUtils.setField(request, "isActive", true);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(subjectRepository.existsSubjectByCode("IT201")).thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Subject subject = subjectService.createSubject(request);

        verify(subjectRepository).save(any(Subject.class));
        assertEquals("laptrinhjava", subject.getNormalizeName());
        assertEquals(true, subject.getIsActive());
    }
}
