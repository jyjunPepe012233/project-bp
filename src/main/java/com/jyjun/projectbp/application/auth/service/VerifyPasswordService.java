package com.jyjun.projectbp.application.auth.service;

import com.jyjun.projectbp.application.auth.outbound.MatchPasswordPort;
import com.jyjun.projectbp.common.exception.InvalidPasswordException;
import org.springframework.stereotype.Component;

@Component
public class VerifyPasswordService {

    private final MatchPasswordPort matchPasswordPort;

    public VerifyPasswordService(MatchPasswordPort matchPasswordPort) {
        this.matchPasswordPort = matchPasswordPort;
    }

    public void verify(String rawPassword, String encodedPassword) {
        if (!matchPasswordPort.matches(rawPassword, encodedPassword)) {
            throw new InvalidPasswordException("Password mismatch");
        }
    }
}
