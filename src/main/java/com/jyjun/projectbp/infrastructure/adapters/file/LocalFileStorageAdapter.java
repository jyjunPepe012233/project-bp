package com.jyjun.projectbp.infrastructure.adapters.file;

import com.jyjun.projectbp.application.file.outbound.FileStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jyjun.projectbp.common.exception.FileStorageException;
import java.io.IOException;
import java.io.InputStream;
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
            throw new FileStorageException("파일 저장 디렉토리 생성 실패: " + baseDir, e);
        }
    }

    @Override
    public long save(String storedName, InputStream fileData) {
        try {
            return Files.copy(
                    fileData,
                    baseDir.resolve(storedName),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new FileStorageException("파일 저장 실패: " + storedName, e);
        }
    }

    @Override
    public InputStream load(String storedName) {
        try {
            return Files.newInputStream(baseDir.resolve(storedName));
        } catch (IOException e) {
            throw new FileStorageException("파일 로드 실패: " + storedName, e);
        }
    }

    @Override
    public void delete(String storedName) {
        try {
            Files.deleteIfExists(baseDir.resolve(storedName));
        } catch (IOException e) {
            throw new FileStorageException("파일 삭제 실패: " + storedName, e);
        }
    }
}
