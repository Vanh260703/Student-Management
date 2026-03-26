package com.example.quan_ly_sinh_vien_v2.Service;

import com.example.quan_ly_sinh_vien_v2.DTO.Request.AcademicYear.CreateAcademicYearRequest;
import com.example.quan_ly_sinh_vien_v2.Exception.CreateFailException;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.AcademicYear;
import com.example.quan_ly_sinh_vien_v2.Repository.AcademicYearRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcademicYearServiceTest {
    @Mock
    private AcademicYearRepository academicYearRepository;

    private AcademicYearService academicYearService;

    @BeforeEach
    void setUp() {
        academicYearService = new AcademicYearService(academicYearRepository);
    }

    @Test
    void createAcademicYearShouldPersistWhenDateRangeValid() {
        CreateAcademicYearRequest request = new CreateAcademicYearRequest();
        ReflectionTestUtils.setField(request, "name", "2025-2026");
        ReflectionTestUtils.setField(request, "startDate", LocalDate.of(2025, 9, 1));
        ReflectionTestUtils.setField(request, "endDate", LocalDate.of(2026, 6, 30));
        ReflectionTestUtils.setField(request, "isCurrent", true);

        when(academicYearRepository.existsByNameIgnoreCase("2025-2026")).thenReturn(false);
        when(academicYearRepository.existsByStartDateAndEndDate(
                LocalDate.of(2025, 9, 1),
                LocalDate.of(2026, 6, 30))
        ).thenReturn(false);
        when(academicYearRepository.save(any(AcademicYear.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AcademicYear academicYear = academicYearService.createAcademicYear(request);

        verify(academicYearRepository).save(any(AcademicYear.class));
        assertEquals("2025-2026", academicYear.getName());
        assertEquals(true, academicYear.getIsCurrent());
    }

    @Test
    void createAcademicYearShouldRejectWhenStartDateAfterEndDate() {
        CreateAcademicYearRequest request = new CreateAcademicYearRequest();
        ReflectionTestUtils.setField(request, "name", "2025-2026");
        ReflectionTestUtils.setField(request, "startDate", LocalDate.of(2026, 6, 30));
        ReflectionTestUtils.setField(request, "endDate", LocalDate.of(2025, 9, 1));

        when(academicYearRepository.existsByNameIgnoreCase("2025-2026")).thenReturn(false);
        when(academicYearRepository.existsByStartDateAndEndDate(
                LocalDate.of(2026, 6, 30),
                LocalDate.of(2025, 9, 1))
        ).thenReturn(false);

        assertThrows(CreateFailException.class, () -> academicYearService.createAcademicYear(request));
    }
}
