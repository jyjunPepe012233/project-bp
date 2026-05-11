package com.jyjun.projectbp.application.developer.util;

import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.domain.developer.model.Developer;

import java.util.NoSuchElementException;

public class IsRootAccountOfDeveloperUtil {

    private final LoadDeveloperService loadDeveloperService;

    public IsRootAccountOfDeveloperUtil(LoadDeveloperService loadDeveloperService) {
        this.loadDeveloperService = loadDeveloperService;
    }

    public boolean is(Long rootAccountId, Long developerId) {
        try {
            Developer developer = loadDeveloperService.loadByRootAccountIdOrThrow(rootAccountId);
            return developer.getId().equals(developerId);
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
