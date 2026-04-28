package com.jyjun.projectbp.domain.filemeta.model;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_file_meta")
public class FileMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자가 입력한 파일 이름
    @Column(nullable = false, updatable = false)
    private String originalName;

    // 실제로 스토리지에 저장되는 파일 이름 (생성자에서 UUID로 자동 생성)
    // 충돌나면 안되니까 unique 제약 처리
    @Column(nullable = false, updatable = false, unique = true)
    private String storedName;

    @Column(nullable = false, updatable = false)
    private long sizeByte;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public FileMeta() {
    }

    public FileMeta(String originalName, String storedName, long sizeByte) {

        if (StringUtils.isBlank(originalName)) {
            throw new IllegalArgumentException("Original name must not be null or blank");
        }

        this.originalName = originalName;
        this.storedName = storedName;
        this.sizeByte = sizeByte;
        this.createdAt = LocalDateTime.now();
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getStoredName() {
        return storedName;
    }

    public long getSizeByte() {
        return sizeByte;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
