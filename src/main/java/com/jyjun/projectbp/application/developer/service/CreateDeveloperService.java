package com.jyjun.projectbp.application.developer.service;

import com.jyjun.projectbp.application.developer.outbound.DeveloperRepositoryPort;
import com.jyjun.projectbp.domain.developer.model.Developer;
import org.springframework.stereotype.Component;

@Component
public class CreateDeveloperService {
    private final DeveloperRepositoryPort developerRepositoryPort;

    public CreateDeveloperService(DeveloperRepositoryPort developerRepositoryPort) {
        this.developerRepositoryPort = developerRepositoryPort;
    }

    public Developer create(String developerName, Long accountId) {
        Developer developer = new Developer(developerName, accountId);
        return developerRepositoryPort.save(developer);
    }
}
