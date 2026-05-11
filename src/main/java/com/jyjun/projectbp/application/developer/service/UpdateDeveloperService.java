package com.jyjun.projectbp.application.developer.service;

import com.jyjun.projectbp.application.developer.outbound.DeveloperRepositoryPort;
import com.jyjun.projectbp.domain.developer.model.Developer;
import org.springframework.stereotype.Component;

@Component
public class UpdateDeveloperService {

    private final DeveloperRepositoryPort developerRepositoryPort;
    private final LoadDeveloperService loadDeveloperService;

    public UpdateDeveloperService(DeveloperRepositoryPort developerRepositoryPort, LoadDeveloperService loadDeveloperService) {
        this.developerRepositoryPort = developerRepositoryPort;
        this.loadDeveloperService = loadDeveloperService;
    }

    public Developer updateName(Long developerId, String name) {
        Developer developer = loadDeveloperService.loadByIdOrThrow(developerId);
        developer.updateName(name);
        return developerRepositoryPort.save(developer);
    }
}
