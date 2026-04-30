package com.jyjun.projectbp.infrastructure.developer;

import com.jyjun.projectbp.application.developer.outbound.DeveloperRepositoryPort;
import com.jyjun.projectbp.domain.developer.model.Developer;
import org.springframework.stereotype.Repository;

@Repository
public class DeveloperRepositoryAdapter implements DeveloperRepositoryPort {

    private final JpaDeveloperRepository jpaDeveloperRepository;

    public DeveloperRepositoryAdapter(JpaDeveloperRepository jpaDeveloperRepository) {
        this.jpaDeveloperRepository = jpaDeveloperRepository;
    }

    @Override
    public Developer save(Developer developer) {
        return jpaDeveloperRepository.save(developer);
    }
}
