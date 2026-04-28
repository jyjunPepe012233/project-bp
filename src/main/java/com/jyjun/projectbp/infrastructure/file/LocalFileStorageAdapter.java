package com.jyjun.projectbp.infrastructure.file;

import com.jyjun.projectbp.application.file.outbound.FileStoragePort;
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
public class LocalFileStorageAdapter implements FileStoragePort {

    private final Path baseDir;

    public LocalFileStorageAdapter(@Value("${file.storage.base-dir}") String baseDir) {
        this.baseDir = Paths.get(baseDir);

        try {
            // 파일 저장 디렉토리가 존재하지 않으면 생성
            Files.createDirectories(this.baseDir);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 저장 디렉토리 생성 실패: " + baseDir, e);
        }
    }

    @Override
    public void save(String storedName, InputStream fileData) {
        try {
            Files.copy(
                    fileData,
                    baseDir.resolve(storedName), // 파일 저장 경로
                    StandardCopyOption.REPLACE_EXISTING // 덮어쓰기
            );
            System.out.println(baseDir.resolve(storedName).toAbsolutePath());
        } catch (IOException e) {
            throw new UncheckedIOException("파일 저장 실패: " + storedName, e);
        }
    }

    @Override
    public InputStream load(String storedName) {
        try {
            return Files.newInputStream(baseDir.resolve(storedName));
        } catch (IOException e) {
            throw new UncheckedIOException("파일 로드 실패: " + storedName, e);
        }
    }

    @Override
    public void delete(String storedName) {
        try {
            Files.deleteIfExists(baseDir.resolve(storedName));
        } catch (IOException e) {
            throw new UncheckedIOException("파일 삭제 실패: " + storedName, e);
        }
    }
}
