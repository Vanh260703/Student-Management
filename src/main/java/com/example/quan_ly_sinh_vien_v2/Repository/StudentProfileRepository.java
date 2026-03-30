package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    @Query("""
    SELECT s FROM StudentProfile s
    WHERE (:departmentId IS NULL OR s.department.id = :departmentId)
    
    AND (:programId IS NULL OR s.program.id = :programId)
    AND (:enrollmentYear IS NULL OR s.enrollmentYear = :enrollmentYear)
    AND (:status IS NULL OR s.status = :status)
    AND (:gpaMin IS NULL OR s.gpa >= :gpaMin)
    AND (:gpaMax IS NULL OR s.gpa <= :gpaMax)
    
    AND (
        :search IS NULL
        OR s.user.normalizeName LIKE CONCAT('%', :search, '%')
        OR LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :search, '%'))
    )
""")
    List<StudentProfile> searchStudents(
            @Param("departmentId") Long departmentId,
            @Param("search") String search,
            @Param("programId") Long programId,
            @Param("enrollmentYear") Integer enrollmentYear,
            @Param("status") StudentStatus status,
            @Param("gpaMin") Float gpaMin,
            @Param("gpaMax") Float gpaMax
    );

    boolean existsStudentProfileByStudentCode(String code);

    @Query("""
        SELECT s FROM StudentProfile s 
        WHERE s.user.email = :username
    """)
    Optional<StudentProfile> findStudentByEmail(@Param("username") String username);

    Optional<StudentProfile> findByUserId(Long userId);
}
