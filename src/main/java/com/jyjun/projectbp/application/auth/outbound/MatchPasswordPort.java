package com.jyjun.projectbp.application.auth.outbound;

public interface MatchPasswordPort {

    boolean matches(String rawPassword, String encodedPassword);
}
