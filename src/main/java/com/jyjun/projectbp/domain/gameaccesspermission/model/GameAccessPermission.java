package com.jyjun.projectbp.domain.gameaccesspermission.model;

import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import jakarta.persistence.*;

// permission 아니고 gameaccesspermission

@Entity
@Table(name = "tb_permission")
public class GameAccessPermission {

    // account 도메인 (하위 계정 생성)
    // + 기존 create account api 삭제
    // 현재 accountId 확인
    // root account로 game 찾기
    // 생성된 account와 game 조합해서 game access permission 생성

    // GetAccountPermissionService
    // getAccountPermissionService.get(currentAccountService.get())

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Long accountId;

    @Column(nullable = false, updatable = false)
    private Long gameId;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private GameAccessPermissionType permission;

    public GameAccessPermission() {
    }

    public GameAccessPermission(Long accountId, Long gameId, GameAccessPermissionType permission) {
        this.accountId = accountId;
        this.gameId = gameId;
        this.permission = permission;
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getGameId() {
        return gameId;
    }

    public GameAccessPermissionType getPermission() {
        return permission;
    }
}
