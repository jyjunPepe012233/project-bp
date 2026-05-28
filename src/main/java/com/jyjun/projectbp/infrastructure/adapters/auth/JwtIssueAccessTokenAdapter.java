package com.jyjun.projectbp.infrastructure.adapters.auth;

import com.jyjun.projectbp.application.auth.outbound.IssueAccessTokenPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtIssueAccessTokenAdapter implements IssueAccessTokenPort {

    private final SecretKey signingKey;
    private final long accessTokenExpirationMs;

    public JwtIssueAccessTokenAdapter(@Value("${jwt.secret}") String secret, @Value("${jwt.access-token-expiration-ms}") long accessTokenExpirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    @Override
    public String issue(Long accountId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .subject(String.valueOf(accountId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }
}
