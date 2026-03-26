package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    List<AcademicYear> findAcademicYearByIsCurrent(Boolean isCurrent);

    boolean existsByNameIgnoreCase(String trim);

    boolean existsByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);
}
