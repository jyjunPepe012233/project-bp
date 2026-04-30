package com.jyjun.projectbp.application.auth.service;

import com.jyjun.projectbp.application.auth.outbound.GenerateRefreshTokenPort;
import com.jyjun.projectbp.application.auth.outbound.IssueAccessTokenPort;
import com.jyjun.projectbp.application.auth.outbound.RefreshTokenRepositoryPort;
import com.jyjun.projectbp.domain.refreshtoken.model.RefreshToken;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class IssueTokenService {

    private final IssueAccessTokenPort issueAccessTokenPort;
    private final GenerateRefreshTokenPort generateRefreshTokenPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    public IssueTokenService(IssueAccessTokenPort issueAccessTokenPort, RefreshTokenRepositoryPort refreshTokenRepositoryPort) {
        this.issueAccessTokenPort = issueAccessTokenPort;
        this.generateRefreshTokenPort = generateRefreshTokenPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
    }

    public String issueAccessToken(Long accountId) {
        return issueAccessTokenPort.issue(accountId);
    }

    public RefreshToken issueRefreshToken(Long accountId) {
        String token = generateRefreshTokenPort.generate();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
        return refreshTokenRepositoryPort.save(new RefreshToken(accountId, token, expiresAt));
    }
}
