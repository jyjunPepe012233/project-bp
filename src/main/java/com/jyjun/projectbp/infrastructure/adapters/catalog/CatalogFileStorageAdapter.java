package com.jyjun.projectbp.infrastructure.adapters.catalog;

import com.jyjun.projectbp.application.catalog.outbound.CatalogFileStoragePort;
import com.jyjun.projectbp.common.exception.FileStorageException;
import com.jyjun.projectbp.common.exception.InvalidPathException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class CatalogFileStorageAdapter implements CatalogFileStoragePort {

    private static final String CATALOG_FILENAME = "catalog.json";
    private static final String CATALOG_HASH_FILENAME = "catalog.hash";

    private final Path patchesDir;

    public CatalogFileStorageAdapter(@Value("${file.storage.base-dir}") String baseDir) {
        this.patchesDir = Paths.get(baseDir, "bundles");

        try {
            Files.createDirectories(this.patchesDir);
        } catch (IOException e) {
            throw new FileStorageException("카탈로그 파일 저장 디렉토리 생성 실패", e);
        }
    }

    @Override
    public void saveCatalog(String gameUuid, String platform, String version, InputStream data) {
        saveCatalogFile(gameUuid, platform, version, CATALOG_FILENAME, data);
    }

    @Override
    public void deleteCatalog(String gameUuid, String platform, String version) {
        deleteCatalogFile(gameUuid, platform, version, CATALOG_FILENAME);
    }

    @Override
    public boolean catalogExists(String gameUuid, String platform, String version) {
        return catalogFileExists(gameUuid, platform, version, CATALOG_FILENAME);
    }

    @Override
    public void saveCatalogHash(String gameUuid, String platform, String version, InputStream data) {
        saveCatalogFile(gameUuid, platform, version, CATALOG_HASH_FILENAME, data);
    }

    @Override
    public void deleteCatalogHash(String gameUuid, String platform, String version) {
        deleteCatalogFile(gameUuid, platform, version, CATALOG_HASH_FILENAME);
    }

    @Override
    public boolean catalogHashExists(String gameUuid, String platform, String version) {
        return catalogFileExists(gameUuid, platform, version, CATALOG_HASH_FILENAME);
    }

    private void saveCatalogFile(String gameUuid, String platform, String version, String filename, InputStream data) {
        validatePathSegment(platform);
        validatePathSegment(version);

        Path targetDir = patchesDir.resolve(gameUuid).resolve(platform).resolve(version);

        try {
            Files.createDirectories(targetDir);
            Files.copy(data, targetDir.resolve(filename));
        } catch (IOException e) {
            throw new FileStorageException("카탈로그 파일 저장 실패: " + filename, e);
        }
    }

    private void deleteCatalogFile(String gameUuid, String platform, String version, String filename) {
        validatePathSegment(platform);
        validatePathSegment(version);

        Path target = patchesDir.resolve(gameUuid).resolve(platform).resolve(version).resolve(filename);

        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new FileStorageException("카탈로그 파일 삭제 실패: " + filename, e);
        }
    }

    private boolean catalogFileExists(String gameUuid, String platform, String version, String filename) {
        validatePathSegment(platform);
        validatePathSegment(version);

        Path target = patchesDir.resolve(gameUuid).resolve(platform).resolve(version).resolve(filename);
        return Files.isRegularFile(target);
    }

    private void validatePathSegment(String segment) {
        if (segment == null || segment.isBlank()) {
            throw new InvalidPathException("경로 세그먼트가 비어있습니다.");
        }
        if (segment.contains("..") || segment.contains("/") || segment.contains("\\")) {
            throw new InvalidPathException("허용되지 않는 경로 세그먼트입니다: " + segment);
        }
    }
}
