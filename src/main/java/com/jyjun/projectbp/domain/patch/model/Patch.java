package com.jyjun.projectbp.domain.patch.model;

import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_patch")
public class Patch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Long gameId;

    @Column(nullable = false, updatable = false)
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private PatchPlatform platform;

    @Column(nullable = false, length = 4096)
    private String patchNote;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Patch() {
    }

    public Patch(Long gameId, String version, PatchPlatform platform, String patchNote, String fileName) {
        this.gameId = gameId;
        this.version = version;
        this.platform = platform;
        this.patchNote = patchNote;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getGameId() {
        return gameId;
    }

    public String getVersion() {
        return version;
    }

    public PatchPlatform getPlatform() {
        return platform;
    }

    public String getPatchNote() {
        return patchNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setPatchNote(String patchNote) {
        this.patchNote = patchNote;
    }
}
