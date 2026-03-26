package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.Semester.CreateSemesterRequest;
import com.example.quan_ly_sinh_vien_v2.DTO.Request.Semester.UpdateSemesterRequest;
import com.example.quan_ly_sinh_vien_v2.Exception.AlreadyExistsException;
import com.example.quan_ly_sinh_vien_v2.Exception.UpdateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.AcademicYear;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.SemesterName;
import com.example.quan_ly_sinh_vien_v2.Repository.AcademicYearRepository;
import com.example.quan_ly_sinh_vien_v2.Repository.SemesterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SemesterServiceTest {
    @Mock
    private SemesterRepository semesterRepository;
    @Mock
    private AcademicYearRepository academicYearRepository;

    private SemesterService semesterService;

    @BeforeEach
    void setUp() {
        semesterService = new SemesterService(semesterRepository, academicYearRepository);
    }

    @Test
    void createSemesterShouldPersistMappedSemester() {
        AcademicYear academicYear = new AcademicYear();
        academicYear.setName("2025-2026");
        ReflectionTestUtils.setField(academicYear, "id", 1L);

        CreateSemesterRequest request = new CreateSemesterRequest();
        ReflectionTestUtils.setField(request, "academicYearId", 1L);
        ReflectionTestUtils.setField(request, "semesterNumber", 2);
        ReflectionTestUtils.setField(request, "startDate", LocalDate.of(2026, 1, 1));
        ReflectionTestUtils.setField(request, "endDate", LocalDate.of(2026, 5, 31));
        ReflectionTestUtils.setField(request, "registrationStart", Instant.parse("2026-01-05T00:00:00Z"));
        ReflectionTestUtils.setField(request, "registrationEnd", Instant.parse("2026-01-10T00:00:00Z"));
        ReflectionTestUtils.setField(request, "isActive", true);

        when(academicYearRepository.findById(1L)).thenReturn(Optional.of(academicYear));
        when(semesterRepository.existsSemesterByAcademicYearIdAndSemesterNumber(1L, 2)).thenReturn(false);
        when(semesterRepository.save(any(Semester.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Semester result = semesterService.createSemester(request);

        ArgumentCaptor<Semester> captor = ArgumentCaptor.forClass(Semester.class);
        verify(semesterRepository).save(captor.capture());
        Semester saved = captor.getValue();

        assertEquals(SemesterName.HK2, saved.getName());
        assertEquals(academicYear, saved.getAcademicYear());
        assertEquals(LocalDate.of(2026, 5, 31), result.getEndDate());
    }

    @Test
    void createSemesterShouldRejectDuplicateSemester() {
        AcademicYear academicYear = new AcademicYear();
        ReflectionTestUtils.setField(academicYear, "id", 1L);

        CreateSemesterRequest request = new CreateSemesterRequest();
        ReflectionTestUtils.setField(request, "academicYearId", 1L);
        ReflectionTestUtils.setField(request, "semesterNumber", 1);
        ReflectionTestUtils.setField(request, "startDate", LocalDate.of(2026, 1, 1));
        ReflectionTestUtils.setField(request, "endDate", LocalDate.of(2026, 5, 31));
        ReflectionTestUtils.setField(request, "isActive", true);

        when(academicYearRepository.findById(1L)).thenReturn(Optional.of(academicYear));
        when(semesterRepository.existsSemesterByAcademicYearIdAndSemesterNumber(1L, 1)).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> semesterService.createSemester(request));
    }

    @Test
    void updateSemesterShouldRejectRegistrationOutsideSemesterDuration() {
        Semester semester = new Semester();
        semester.setStartDate(LocalDate.of(2026, 1, 1));
        semester.setEndDate(LocalDate.of(2026, 5, 31));
        semester.setRegistrationStart(Instant.parse("2026-01-02T00:00:00Z"));
        semester.setRegistrationEnd(Instant.parse("2026-01-10T00:00:00Z"));

        when(semesterRepository.findById(10L)).thenReturn(Optional.of(semester));

        UpdateSemesterRequest request = new UpdateSemesterRequest();
        ReflectionTestUtils.setField(request, "registrationStart", LocalDate.of(2025, 12, 30)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());

        assertThrows(UpdateFailException.class, () -> semesterService.updateSemester(10L, request));
    }
}
