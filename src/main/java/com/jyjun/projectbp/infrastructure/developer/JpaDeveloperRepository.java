package com.jyjun.projectbp.infrastructure.developer;

import com.jyjun.projectbp.domain.developer.model.Developer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDeveloperRepository extends JpaRepository<Developer, Long> {
}
