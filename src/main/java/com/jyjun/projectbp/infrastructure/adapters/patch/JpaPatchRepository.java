package com.jyjun.projectbp.infrastructure.adapters.patch;

import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import com.jyjun.projectbp.domain.patch.model.Patch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaPatchRepository extends JpaRepository<Patch, Long> {

    List<Patch> findByGameId(Long gameId);

    boolean existsByGameIdAndVersionAndPlatform(Long gameId, String version, PatchPlatform platform);
}
