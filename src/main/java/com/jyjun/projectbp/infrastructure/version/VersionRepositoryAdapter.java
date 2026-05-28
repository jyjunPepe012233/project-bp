package com.jyjun.projectbp.infrastructure.version;

import com.jyjun.projectbp.application.version.outbound.VersionRepositoryPort;
import com.jyjun.projectbp.domain.version.model.Version;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class VersionRepositoryAdapter implements VersionRepositoryPort {

    private final JpaVersionRepository jpaVersionRepository;

    public VersionRepositoryAdapter(JpaVersionRepository jpaVersionRepository) {
        this.jpaVersionRepository = jpaVersionRepository;
    }

    @Override
    public Version save(Version version) {
        return jpaVersionRepository.save(version);
    }

    @Override
    public Optional<Version> findByGameId(Long gameId) {
        return jpaVersionRepository.findByGameId(gameId);
    }

    @Override
    public void deleteByGameId(Long gameId) {
        jpaVersionRepository.deleteByGameId(gameId);
    }
}
