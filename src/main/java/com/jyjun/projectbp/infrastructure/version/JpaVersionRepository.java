package com.jyjun.projectbp.infrastructure.version;

import com.jyjun.projectbp.domain.version.model.Version;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaVersionRepository extends JpaRepository<Version, Long> {

    Optional<Version> findByGameId(Long gameId);

    void deleteByGameId(Long gameId);
}
