package com.jyjun.projectbp.infrastructure.account;

import com.jyjun.projectbp.application.account.outbound.EncodePasswordPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EncodePasswordAdapter implements EncodePasswordPort {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // 아직 security config 추가 안 해서 직접 객체 생성

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
