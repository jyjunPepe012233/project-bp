package com.jyjun.projectbp.application.developer.service;

import com.jyjun.projectbp.application.developer.outbound.DeveloperRepositoryPort;
import com.jyjun.projectbp.domain.developer.model.Developer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
public class LoadDeveloperService {

    private final DeveloperRepositoryPort developerRepositoryPort;

    public LoadDeveloperService(DeveloperRepositoryPort developerRepositoryPort) {
        this.developerRepositoryPort = developerRepositoryPort;
    }

    public Developer loadByIdOrThrow(Long id) {
        return developerRepositoryPort.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Developer not found: id=" + id));
    }

    public Developer loadByRootAccountIdOrThrow(Long rootAccountId) {
        return developerRepositoryPort.findByRootAccountId(rootAccountId)
                .orElseThrow(() -> new NoSuchElementException("Developer not found: rootAccountId=" + rootAccountId));
    }

    public List<Developer> loadAllByIds(List<Long> ids) {
        return developerRepositoryPort.findAllByIds(ids);
    }
}
