package com.example.quan_ly_sinh_vien_v2.Repository;

import com.example.quan_ly_sinh_vien_v2.Modal.Entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByToken(String refreshToken);

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM RefreshToken rt
        WHERE rt.expiresAt < :now
        OR rt.revokedAt IS NOT NULL
    """)
    int deleteExpiredOrRevokedTokens(Instant now);
}
