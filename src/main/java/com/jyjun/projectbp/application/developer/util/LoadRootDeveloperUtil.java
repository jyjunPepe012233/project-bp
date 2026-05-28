package com.jyjun.projectbp.application.developer.util;

import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.domain.developer.model.Developer;

import java.util.NoSuchElementException;

public class LoadRootDeveloperUtil {

    private final LoadDeveloperService loadDeveloperService;

    public LoadRootDeveloperUtil(LoadDeveloperService loadDeveloperService) {
        this.loadDeveloperService = loadDeveloperService;
    }

    // root 계정은 developer당 하나이므로 단일 반환. 없으면 null.
    public Developer load(Long accountId) {
        try {
            return loadDeveloperService.loadByRootAccountIdOrThrow(accountId);
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
