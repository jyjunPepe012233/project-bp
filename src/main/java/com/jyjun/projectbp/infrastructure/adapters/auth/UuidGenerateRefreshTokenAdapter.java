package com.jyjun.projectbp.infrastructure.adapters.auth;

import com.jyjun.projectbp.application.auth.outbound.GenerateRefreshTokenPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidGenerateRefreshTokenAdapter implements GenerateRefreshTokenPort {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
