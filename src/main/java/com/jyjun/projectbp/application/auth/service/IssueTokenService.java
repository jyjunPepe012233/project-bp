package com.jyjun.projectbp.application.auth.service;

import com.jyjun.projectbp.application.auth.outbound.GenerateRefreshTokenPort;
import com.jyjun.projectbp.application.auth.outbound.IssueAccessTokenPort;
import com.jyjun.projectbp.application.auth.outbound.RefreshTokenRepositoryPort;
import com.jyjun.projectbp.domain.refreshtoken.model.RefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class IssueTokenService {

    private final IssueAccessTokenPort issueAccessTokenPort;
    private final GenerateRefreshTokenPort generateRefreshTokenPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    private final long refreshTokenExpirationDays; // application에서 주입받음

    public IssueTokenService(
            IssueAccessTokenPort issueAccessTokenPort,
            GenerateRefreshTokenPort generateRefreshTokenPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            @Value("${jwt.refresh-token-expiration-days}") long refreshTokenExpirationDays
    ) {
        this.issueAccessTokenPort = issueAccessTokenPort;
        this.generateRefreshTokenPort = generateRefreshTokenPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
    }

    public String issueAccessToken(Long accountId) {
        return issueAccessTokenPort.issue(accountId);
    }

    public RefreshToken issueRefreshToken(Long accountId) {
        String token = generateRefreshTokenPort.generate();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(refreshTokenExpirationDays);
        return refreshTokenRepositoryPort.save(new RefreshToken(accountId, token, expiresAt));
    }
}
