package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    @Query("""
    SELECT s FROM Subject s
    WHERE (:departmentId IS NULL OR s.department.id = :departmentId)
    AND (:isActive IS NULL OR s.isActive = :isActive)
    AND (:credits IS NULL OR s.credits = :credits)
    AND (
        :search IS NULL
        OR s.normalizeName LIKE CONCAT('%', :search, '%')
        OR LOWER(s.code) LIKE LOWER(CONCAT('%', :search, '%'))
    )
""")
    List<Subject> searchSubjects(
            @Param("departmentId") Long departmentId,
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            @Param("credits") Integer credits
    );

    @Query("""
        SELECT COUNT (t) > 0 FROM Subject t 
        WHERE LOWER(t.code) LIKE LOWER(:code)  
    """)
    boolean existsSubjectByCode(@Param("code") String code);
}
