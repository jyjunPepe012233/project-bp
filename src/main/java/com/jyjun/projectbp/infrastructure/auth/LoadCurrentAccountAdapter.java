package com.jyjun.projectbp.infrastructure.auth;

import com.jyjun.projectbp.application.auth.outbound.LoadCurrentAccountPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LoadCurrentAccountAdapter implements LoadCurrentAccountPort {

    @Override
    public Long getCurrentAccountId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long)) {
            throw new IllegalStateException("인증을 읽어들일 수 없음");
        }
        return (Long)auth.getPrincipal();
    }
}
