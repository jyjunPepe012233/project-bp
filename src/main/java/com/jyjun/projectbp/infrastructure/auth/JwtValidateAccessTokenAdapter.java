package com.jyjun.projectbp.infrastructure.auth;

import com.jyjun.projectbp.application.auth.outbound.ValidateAccessTokenPort;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtValidateAccessTokenAdapter implements ValidateAccessTokenPort {

    private final SecretKey signingKey;

    public JwtValidateAccessTokenAdapter(@Value("${jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    @Override
    public Long extractAccountId(String accessToken) {
        try {
            String subject = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload()
                    .getSubject();
            return Long.valueOf(subject);
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid access token", e);
        }
    }
}
