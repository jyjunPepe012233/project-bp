package com.jyjun.projectbp.domain.refreshtoken.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    protected RefreshToken() {
    }

    public RefreshToken(Long accountId, String token, LocalDateTime expiresAt) {
        this.accountId = accountId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.used = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isUsed() {
        return used;
    }

    public void markAsUsed() {
        this.used = true;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getToken() {
        return token;
    }
}
