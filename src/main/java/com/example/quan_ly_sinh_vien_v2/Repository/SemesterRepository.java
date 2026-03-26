package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Semester;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.SemesterName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    @Query("""
    SELECT s FROM Semester s
    WHERE (:academicYearId IS NULL OR s.academicYear.id = :academicYearId)
    AND (:isActive IS NULL OR s.isActive = :isActive)
    AND (:semesterNumber IS NULL OR s.semesterNumber = :semesterNumber)
""")
    List<Semester> searchSemesters(
            @Param("academicYearId") Long academicYearId,
            @Param("isActive") Boolean isActive,
            @Param("semesterNumber") Integer semesterNumber
    );

    boolean existsSemesterByAcademicYearIdAndSemesterNumber(Long academicYearId, Integer semesterNumber);

    Optional<Semester> findFirstByIsActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate startDate, LocalDate endDate);
}
