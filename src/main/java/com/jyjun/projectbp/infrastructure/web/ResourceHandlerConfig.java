package com.jyjun.projectbp.infrastructure.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Configuration
public class ResourceHandlerConfig implements WebMvcConfigurer {

    private final String baseDir;

    public ResourceHandlerConfig(@Value("${file.storage.base-dir}") String baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String bundlesLocation = "file:" + Paths.get(baseDir, "bundles").toAbsolutePath() + "/";

        registry.addResourceHandler("/bundles/**")
                .addResourceLocations(bundlesLocation)
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS)
                        .cachePublic() // CDN 캐싱 허용
                        .immutable()) // 변경 불가능한 리소스임을 명시하여 클라이언트 최적화
                .resourceChain(true); // 파일 접근 최적화
    }
}
