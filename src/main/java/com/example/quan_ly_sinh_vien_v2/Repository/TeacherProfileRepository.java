package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.TeacherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {
    @Query("""
        SELECT t FROM TeacherProfile t 
        WHERE :departmentId IS NULL OR t.department.id = :departmentId
        AND (:degree IS NULL OR t.degree = :degree)
        AND (
             :search IS NULL
            OR t.user.normalizeName LIKE CONCAT('%', :search, '%')
            OR LOWER(t.teacherCode) LIKE LOWER(CONCAT('%', :search, '%'))
            )
    """)
    List<TeacherProfile> searchTeachers(
        @Param("departmentId") Long departmentId,
        @Param("search") String search,
        @Param("degree") String degree
    );

    boolean existsTeacherProfileByTeacherCode(String teacherCode);

    @Query("""
        SELECT t FROM TeacherProfile t 
        WHERE t.user.email = :username
    """)
    Optional<TeacherProfile> findTeacherProfileByEmail(@Param("username") String username);
}
