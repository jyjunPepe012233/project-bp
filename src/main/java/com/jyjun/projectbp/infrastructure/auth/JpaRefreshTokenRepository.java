package com.jyjun.projectbp.infrastructure.auth;

import com.jyjun.projectbp.domain.refreshtoken.model.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Transactional
    void deleteAllByAccountId(Long accountId);
}
