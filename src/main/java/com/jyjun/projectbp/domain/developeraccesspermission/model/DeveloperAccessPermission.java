package com.jyjun.projectbp.domain.developeraccesspermission.model;

import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_developer_permission")
public class DeveloperAccessPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Long accountId;

    @Column(nullable = false, updatable = false)
    private Long developerId;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private DeveloperAccessPermissionType permission;

    public DeveloperAccessPermission() {
    }

    public DeveloperAccessPermission(Long accountId, Long developerId, DeveloperAccessPermissionType permission) {
        this.accountId = accountId;
        this.developerId = developerId;
        this.permission = permission;
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getDeveloperId() {
        return developerId;
    }

    public DeveloperAccessPermissionType getPermission() {
        return permission;
    }
}
