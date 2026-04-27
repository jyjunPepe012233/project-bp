package com.jyjun.projectbp.domain.filemeta;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_file_meta")
public class FileMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String originalName;

    @Column(nullable = false, updatable = false, unique = true)
    private String storedName;

    @Column(nullable = false, updatable = false)
    private long sizeByte;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public FileMeta() {
    }

    public FileMeta(String originalName, String storedName, long sizeByte, LocalDateTime createdAt) {

        if (StringUtils.isBlank(originalName)) {
            throw new IllegalArgumentException("Original name must not be null or blank");
        }

        if (StringUtils.isBlank(storedName)) {
            throw new IllegalArgumentException("Stored name must not be null or blank");
        }

        this.originalName = originalName;
        this.storedName = storedName;
        this.sizeByte = sizeByte;
        this.createdAt = createdAt;
    }
}
