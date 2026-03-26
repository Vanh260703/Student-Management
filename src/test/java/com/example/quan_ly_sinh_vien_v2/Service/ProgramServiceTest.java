package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Program.CreateProgramRequest;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Department;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Program;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ProgramSubject;
import com.example.quan_ly_sinh_vien_v2.Repository.DepartmentRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.ProgramRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.ProgramSubjectRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgramServiceTest {
    @Mock
    private ProgramRepository programRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private ProgramSubjectRepository programSubjectRepository;
    @Mock
    private SubjectRepository subjectRepository;

    private ProgramService programService;

    @BeforeEach
    void setUp() {
        programService = new ProgramService(
                programRepository,
                departmentRepository,
                programSubjectRepository,
                subjectRepository
        );
    }

    @Test
    void createProgramShouldPersistNormalizedName() {
        Department department = new Department();
        ReflectionTestUtils.setField(department, "id", 1L);

        CreateProgramRequest request = new CreateProgramRequest();
        ReflectionTestUtils.setField(request, "departmentId", 1L);
        ReflectionTestUtils.setField(request, "code", "SE");
        ReflectionTestUtils.setField(request, "name", "Software Engineering");
        ReflectionTestUtils.setField(request, "totalCredits", 130);
        ReflectionTestUtils.setField(request, "durationYear", 4);

        when(programRepository.existsProgramByCode("SE")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(programRepository.save(any(Program.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Program program = programService.createProgram(request);

        verify(programRepository).save(any(Program.class));
        assertEquals("softwareengineering", program.getNormalizeName());
        assertEquals(130, program.getTotalCredits());
    }

    @Test
    void getSubjectsInProgramShouldUseCombinedFilterWhenSemesterAndRequiredProvided() {
        Program program = new Program();
        ProgramSubject combined = new ProgramSubject();

        when(programRepository.findById(1L)).thenReturn(Optional.of(program));
        when(programSubjectRepository.findAllByProgramAndSemesterAndIsRequired(program, 2, true))
                .thenReturn(List.of(combined));

        List<ProgramSubject> result = programService.getSubjectsInProgram(1L, 2, true);

        assertEquals(1, result.size());
        assertEquals(combined, result.get(0));
    }
}
