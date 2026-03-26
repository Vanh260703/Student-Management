package com.example.quan_ly_sinh_vien_v2.Repository;

;
import com.example.quan_ly_sinh_vien_v2.Modal.Entity.User;
import com.example.quan_ly_sinh_vien_v2.Modal.Enum.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("""
        SELECT u FROM User u
        WHERE 
        (:role IS NULL OR u.role = :role)
        AND
        (
            :search IS NULL 
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
            OR u.normalizeName LIKE CONCAT('%', :search, '%')
        )
    """)
    List<User> searchUsers(@Param("search") String search,
                           @Param("role") Role role);

    boolean existsUserByPersonalEmail(String personalEmail);
}
