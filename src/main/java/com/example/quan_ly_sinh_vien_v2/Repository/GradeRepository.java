package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Enrollment;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Grade;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.GradeComponent;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findAllByGradeComponent(GradeComponent gc);

    @Query("""
        SELECT g FROM Grade g 
        WHERE g.gradeComponent.classEntity.id = :classId
        AND (:componentId IS NULL OR g.gradeComponent.id = :componentId)
        
    """)
    List<Grade> findAllByClassId(
            @Param("classId") Long classId,
            @Param("componentId") Long componentId
    );

    boolean existsGradeByEnrollmentAndGradeComponent(Enrollment enrollment, GradeComponent gradeComponent);

    Optional<Grade> findByEnrollmentAndGradeComponent(Enrollment enrollment, GradeComponent component);


    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Grade g
        SET g.isPublished = true 
        WHERE g.gradeComponent.classEntity.id = :classId
        AND g.isPublished = false
    """)
    int publishGrades(@Param("classId") Long classId);

    List<Grade> findAllByEnrollment(Enrollment enrollment);

    List<Grade> findByEnrollmentIdInAndIsPublished(List<Long> enrollmentIds, boolean b);

    @Query("""
        SELECT g FROM Grade g
        WHERE g.enrollment.student.id = :studentId
        AND g.isPublished = true
    """)
    List<Grade> findAllGradesByStudentId(@Param("studentId") Long studentId);
}
