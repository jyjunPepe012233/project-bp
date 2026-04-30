package com.jyjun.projectbp.application.developer.outbound;

import com.jyjun.projectbp.domain.developer.model.Developer;

public interface DeveloperRepositoryPort {

    Developer save(Developer developer);
}
