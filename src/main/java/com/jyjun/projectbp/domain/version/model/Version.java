package com.jyjun.projectbp.domain.version.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_version")
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long gameId;

    @Column
    private Long patchId;

    public Version() {
    }

    public Version(Long gameId, Long patchId) {
        this.gameId = gameId;
        this.patchId = patchId;
    }

    public Long getId() {
        return id;
    }

    public Long getGameId() {
        return gameId;
    }

    public Long getPatchId() {
        return patchId;
    }

    public void setPatchId(Long patchId) {
        this.patchId = patchId;
    }
}
