package com.jyjun.projectbp.infrastructure.patch;

import com.jyjun.projectbp.domain.patch.model.Patch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPatchRepository extends JpaRepository<Patch, Long> {
}
