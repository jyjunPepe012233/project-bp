package com.jyjun.projectbp.infrastructure.adapters.auth;

import com.jyjun.projectbp.domain.refreshtoken.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteAllByAccountId(Long accountId);
}
