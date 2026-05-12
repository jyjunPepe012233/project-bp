package com.jyjun.projectbp.infrastructure.patch;

import com.jyjun.projectbp.application.patch.outbound.PatchFileStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class PatchFileStorageAdapter implements PatchFileStoragePort {

    private final Path patchesDir;

    public PatchFileStorageAdapter(@Value("${file.storage.base-dir}") String baseDir) {
        this.patchesDir = Paths.get(baseDir, "patches");

        try {
            Files.createDirectories(this.patchesDir);
        } catch (IOException e) {
            throw new UncheckedIOException("패치 파일 저장 디렉토리 생성 실패", e);
        }
    }

    @Override
    public void save(String gameUuid, String platform, String version, String filename, InputStream data) {
        validatePathSegment(platform);
        validatePathSegment(version);
        validatePathSegment(filename);

        Path targetDir = patchesDir.resolve(gameUuid).resolve(platform).resolve(version);

        try {
            Files.createDirectories(targetDir);
            Files.copy(data, targetDir.resolve(filename)); // 충돌 나면 예외 뜸
        } catch (IOException e) {
            throw new UncheckedIOException("패치 파일 저장 실패: " + filename, e);
        }
    }

    @Override
    public void delete(String gameUuid, String platform, String version, String filename) {
        validatePathSegment(platform);
        validatePathSegment(version);
        validatePathSegment(filename);

        Path target = patchesDir.resolve(gameUuid).resolve(platform).resolve(version).resolve(filename);

        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new UncheckedIOException("패치 파일 삭제 실패: " + filename, e);
        }
    }

    // (../, / 등) 방지
    private void validatePathSegment(String segment) {
        if (segment == null || segment.isBlank()) {
            throw new IllegalArgumentException("경로 세그먼트가 비어있습니다.");
        }
        if (segment.contains("..") || segment.contains("/") || segment.contains("\\")) {
            throw new IllegalArgumentException("허용되지 않는 경로 세그먼트입니다: " + segment);
        }
    }
}
