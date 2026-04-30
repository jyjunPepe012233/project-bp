package com.jyjun.projectbp.application.auth.outbound;

import com.jyjun.projectbp.domain.refreshtoken.model.RefreshToken;

public interface RefreshTokenRepositoryPort {

    RefreshToken save(RefreshToken refreshToken);

    RefreshToken findByToken(String token);

    void deleteAllByAccountId(Long accountId);
}
