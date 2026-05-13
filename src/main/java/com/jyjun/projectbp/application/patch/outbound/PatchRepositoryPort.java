package com.jyjun.projectbp.application.patch.outbound;

import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import com.jyjun.projectbp.domain.patch.model.Patch;

import java.util.List;
import java.util.Optional;

public interface PatchRepositoryPort {

    Patch save(Patch patch);

    Optional<Patch> findById(Long id);

    List<Patch> findByGameId(Long gameId);

    boolean existsByGameIdAndVersionAndPlatform(Long gameId, String version, PatchPlatform platform);
}
