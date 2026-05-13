package com.jyjun.projectbp.infrastructure.developer;

import com.jyjun.projectbp.application.developer.outbound.DeveloperRepositoryPort;
import com.jyjun.projectbp.domain.developer.model.Developer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Developer> findById(Long id) {
        return jpaDeveloperRepository.findById(id);
    }

    @Override
    public Optional<Developer> findByRootAccountId(Long rootAccountId) {
        return jpaDeveloperRepository.findByRootAccountId(rootAccountId);
    }

    @Override
    public List<Developer> findAllByIds(List<Long> ids) {
        return jpaDeveloperRepository.findAllById(ids);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaDeveloperRepository.existsByName(name);
    }
}
