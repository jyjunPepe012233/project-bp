package com.jyjun.projectbp.infrastructure.auth;

import com.jyjun.projectbp.application.auth.outbound.RefreshTokenRepositoryPort;
import com.jyjun.projectbp.domain.refreshtoken.model.RefreshToken;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;

@Repository
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;

    public RefreshTokenRepositoryAdapter(JpaRefreshTokenRepository jpaRefreshTokenRepository) {
        this.jpaRefreshTokenRepository = jpaRefreshTokenRepository;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return jpaRefreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken findByToken(String token) {
        return jpaRefreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new NoSuchElementException("Refresh token not found"));
    }

    @Override
    public void deleteAllByAccountId(Long accountId) {
        jpaRefreshTokenRepository.deleteAllByAccountId(accountId);
    }
}
