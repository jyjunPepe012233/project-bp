package com.jyjun.projectbp.application.auth.service;

import com.jyjun.projectbp.application.auth.outbound.RefreshTokenRepositoryPort;
import com.jyjun.projectbp.domain.refreshtoken.model.RefreshToken;
import org.springframework.stereotype.Component;

@Component
public class ValidateRefreshTokenService {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    public ValidateRefreshTokenService(RefreshTokenRepositoryPort refreshTokenRepositoryPort) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
    }

    public Long validate(String token) {
        RefreshToken refreshToken = refreshTokenRepositoryPort.findByToken(token);

        if (refreshToken.isUsed()) {
            // 사용된 토큰 재사용 시 해킹으로 간주하고 모든 토큰 삭제
            refreshTokenRepositoryPort.deleteAllByAccountId(refreshToken.getAccountId());
            throw new SecurityException("Refresh token reuse detected");
        }

        if (refreshToken.isExpired()) {
            // 이미 만료된 토큰 사용 시 모든 토큰 삭제
            refreshTokenRepositoryPort.deleteAllByAccountId(refreshToken.getAccountId());
            throw new IllegalStateException("Refresh token expired");
        }

        // 사용된 토큰은 사용 처리
        refreshToken.markAsUsed();
        refreshTokenRepositoryPort.save(refreshToken);

        return refreshToken.getAccountId();
    }
}
