package com.jyjun.projectbp.infrastructure.patch;

import com.jyjun.projectbp.application.patch.outbound.AddressableFileStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jyjun.projectbp.common.exception.FileStorageException;
import com.jyjun.projectbp.common.exception.InvalidPathException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Component
public class AddressableFileStorageAdapter implements AddressableFileStoragePort {

    private static final String CATALOG_FILENAME = "catalog.json";
    private static final String CATALOG_HASH_FILENAME = "catalog.hash";

    private final Path patchesDir;

    public AddressableFileStorageAdapter(@Value("${file.storage.base-dir}") String baseDir) {
        this.patchesDir = Paths.get(baseDir, "bundles");

        try {
            Files.createDirectories(this.patchesDir);
        } catch (IOException e) {
            throw new FileStorageException("패치 파일 저장 디렉토리 생성 실패", e);
        }
    }

    @Override
    public void saveCatalog(String gameUuid, String version, String platform, InputStream data) {
        saveCatalogFile(gameUuid, version, platform, CATALOG_FILENAME, data);
    }

    @Override
    public void deleteCatalog(String gameUuid, String version, String platform) {
        deleteCatalogFile(gameUuid, version, platform, CATALOG_FILENAME);
    }

    @Override
    public boolean catalogExists(String gameUuid, String version, String platform) {
        return catalogFileExists(gameUuid, version, platform, CATALOG_FILENAME);
    }

    @Override
    public void saveCatalogHash(String gameUuid, String version, String platform, InputStream data) {
        saveCatalogFile(gameUuid, version, platform, CATALOG_HASH_FILENAME, data);
    }

    @Override
    public void deleteCatalogHash(String gameUuid, String version, String platform) {
        deleteCatalogFile(gameUuid, version, platform, CATALOG_HASH_FILENAME);
    }

    @Override
    public boolean catalogHashExists(String gameUuid, String version, String platform) {
        return catalogFileExists(gameUuid, version, platform, CATALOG_HASH_FILENAME);
    }

    @Override
    public void saveBundle(String gameUuid, String version, String platform, String filename, InputStream data) {
        validatePathSegment(version);
        validatePathSegment(platform);
        validatePathSegment(filename);

        Path targetDir = patchesDir.resolve(gameUuid).resolve(version).resolve(platform).resolve("bundles");

        try {
            Files.createDirectories(targetDir);
            Files.copy(data, targetDir.resolve(filename));
        } catch (IOException e) {
            throw new FileStorageException("번들 파일 저장 실패: " + filename, e);
        }
    }

    @Override
    public void deleteBundle(String gameUuid, String version, String platform, String filename) {
        validatePathSegment(version);
        validatePathSegment(platform);
        validatePathSegment(filename);

        Path target = patchesDir.resolve(gameUuid).resolve(version).resolve(platform).resolve("bundles").resolve(filename);

        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new FileStorageException("번들 파일 삭제 실패: " + filename, e);
        }
    }

    @Override
    public List<String> listBundleFiles(String gameUuid, String version, String platform) {
        validatePathSegment(version);
        validatePathSegment(platform);

        Path bundleDir = patchesDir.resolve(gameUuid).resolve(version).resolve(platform).resolve("bundles");

        if (!Files.isDirectory(bundleDir)) {
            return Collections.emptyList();
        }

        try (Stream<Path> stream = Files.list(bundleDir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new FileStorageException("번들 파일 목록 조회 실패", e);
        }
    }

    private void saveCatalogFile(String gameUuid, String version, String platform, String filename, InputStream data) {
        validatePathSegment(version);
        validatePathSegment(platform);

        Path targetDir = patchesDir.resolve(gameUuid).resolve(version).resolve(platform);

        try {
            Files.createDirectories(targetDir);
            Files.copy(data, targetDir.resolve(filename));
        } catch (IOException e) {
            throw new FileStorageException("카탈로그 파일 저장 실패: " + filename, e);
        }
    }

    private void deleteCatalogFile(String gameUuid, String version, String platform, String filename) {
        validatePathSegment(version);
        validatePathSegment(platform);

        Path target = patchesDir.resolve(gameUuid).resolve(version).resolve(platform).resolve(filename);

        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new FileStorageException("카탈로그 파일 삭제 실패: " + filename, e);
        }
    }

    private boolean catalogFileExists(String gameUuid, String version, String platform, String filename) {
        validatePathSegment(version);
        validatePathSegment(platform);

        Path target = patchesDir.resolve(gameUuid).resolve(version).resolve(platform).resolve(filename);
        return Files.isRegularFile(target);
    }

    // (../, / 등) 방지
    private void validatePathSegment(String segment) {
        if (segment == null || segment.isBlank()) {
            throw new InvalidPathException("경로 세그먼트가 비어있습니다.");
        }
        if (segment.contains("..") || segment.contains("/") || segment.contains("\\")) {
            throw new InvalidPathException("허용되지 않는 경로 세그먼트입니다: " + segment);
        }
    }
}
