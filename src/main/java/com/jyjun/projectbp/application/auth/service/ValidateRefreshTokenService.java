package com.jyjun.projectbp.application.auth.service;

import com.jyjun.projectbp.common.exception.TokenExpiredException;
import com.jyjun.projectbp.common.exception.TokenReuseDetectedException;
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
            throw new TokenReuseDetectedException("Refresh token reuse detected");
        }

        if (refreshToken.isExpired()) {
            // 이미 만료된 토큰 사용 시 모든 토큰 삭제
            refreshTokenRepositoryPort.deleteAllByAccountId(refreshToken.getAccountId());
            throw new TokenExpiredException("Refresh token expired");
        }

        // 이미 사용된 토큰이라도 삭제하지 않고 사용 처리만 함.
        // DB에 남아있는 토큰을 통해 어떤 계정의 토큰이 재사용되었는지 확인 가능하도록 하기 위함임
        refreshToken.markAsUsed();
        refreshTokenRepositoryPort.save(refreshToken);

        return refreshToken.getAccountId();
    }
}
