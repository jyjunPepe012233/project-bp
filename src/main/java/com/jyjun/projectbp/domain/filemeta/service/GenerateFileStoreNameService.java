package com.jyjun.projectbp.domain.filemeta.service;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GenerateFileStoreNameService {

    public String generate(String originalName) { // originalName은 사용하지 않음
        return UUID.randomUUID().toString();
    }
}
