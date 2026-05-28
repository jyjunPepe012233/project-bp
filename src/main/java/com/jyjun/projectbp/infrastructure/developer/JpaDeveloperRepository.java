package com.jyjun.projectbp.infrastructure.developer;

import com.jyjun.projectbp.domain.developer.model.Developer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaDeveloperRepository extends JpaRepository<Developer, Long> {

    Optional<Developer> findByRootAccountId(Long rootAccountId);

    boolean existsByName(String name);
}
