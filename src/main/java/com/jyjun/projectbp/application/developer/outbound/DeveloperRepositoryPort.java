package com.jyjun.projectbp.application.developer.outbound;

import com.jyjun.projectbp.domain.developer.model.Developer;

import java.util.List;
import java.util.Optional;

public interface DeveloperRepositoryPort {

    Developer save(Developer developer);

    Optional<Developer> findById(Long id);

    Optional<Developer> findByRootAccountId(Long rootAccountId);

    List<Developer> findAllByIds(List<Long> ids);
}
