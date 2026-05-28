package com.jyjun.projectbp.application.version.outbound;

import com.jyjun.projectbp.domain.version.model.Version;

import java.util.Optional;

public interface VersionRepositoryPort {

    Version save(Version version);

    Optional<Version> findByGameId(Long gameId);

    void deleteByGameId(Long gameId);
}
