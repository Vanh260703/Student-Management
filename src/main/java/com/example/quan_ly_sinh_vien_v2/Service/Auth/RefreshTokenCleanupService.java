package com.example.quan_ly_sinh_vien_v2.Service.Auth;

import com.example.quan_ly_sinh_vien_v2.Repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenCleanupService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenCleanupService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 */3 * *")
    public void cleanupExpiredOrRevokedRefreshTokens() {
        refreshTokenRepository.deleteExpiredOrRevokedTokens(Instant.now());
    }
}
