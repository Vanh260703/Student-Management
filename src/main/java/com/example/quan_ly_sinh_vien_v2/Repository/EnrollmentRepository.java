package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.ClassEntity;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Enrollment;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("""
        SELECT sp FROM Enrollment e
        JOIN StudentProfile sp ON sp.id = e.student.id
        WHERE e.classEntity.id = :classId
        AND e.status = 'COMPLETED'
        AND (
            :search IS NOT NULL
            OR LOWER(e.student.studentCode) LIKE LOWER(CONCAT('%', :search, '%'))
            OR e.student.user.normalizeName LIKE CONCAT('%', :search, '%')
        )
    """)
    List<StudentProfile> findStudentsByClassId(Long classId, @Param("search") String search);

    @Query("""
        SELECT e FROM Enrollment e
        WHERE e.student.id = :studentId
        AND (:semesterId IS NULL OR e.classEntity.semester.id = :semesterId)
        AND (:status IS NULL OR e.status =: status)
    """)
    List<Enrollment> findMyClasses(
            @Param("studentId") Long studentId,
            @Param("semesterId") Long semesterId,
            @Param("status") EnrollmentStatus status
    );

    List<Enrollment> findAllByClassEntityId(Long classId);

    List<Enrollment> findByStudentId(Long id);

    Enrollment findByStudentIdAndClassEntityId(Long id, Long id1);

    @Query("""
        SELECT e FROM Enrollment  e
        WHERE e.student.id = :studentId
        AND e.status = :status
        AND (:semesterId IS NULL OR e.classEntity.semester.id = :semesterId)
    """)
    List<Enrollment> findByStudentIdAndSemesterIdAndStatus(
            @Param("studentId") Long studentId,
            @Param("semesterId") Long semesterId,
            @Param("status") EnrollmentStatus status
    );

    @Query("""
        SELECT e FROM Enrollment e
        WHERE e.classEntity.semester.id = :semesterId
        AND e.status = :status
    """)
    List<Enrollment> findBySemesterIdAndStatus(
            @Param("semesterId") Long semesterId,
            @Param("status") EnrollmentStatus status
    );

    List<Enrollment> findByClassEntity(ClassEntity classEntity);
}
