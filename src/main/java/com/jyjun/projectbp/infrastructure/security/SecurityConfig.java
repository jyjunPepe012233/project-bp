package com.jyjun.projectbp.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .headers(headers ->
                        headers.frameOptions(frame -> frame.disable())
                )

                .authorizeHttpRequests(auth -> auth

                        // Swagger, H2 Console(안 쓰고 있긴 함)
                        .requestMatchers("/h2-console/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // ResourceHandlerConfig에서 설정한 번들 리소스 접근 경로
                        .requestMatchers("/bundles/**").permitAll()


                        // 현재 서비스는 권한 별로 특정 기능 사용을 제한하고 있는데,
                        // hasRole이나 HasAuthority를 걸지 않고 비즈니스 로직에서 직접 권한을 확인하고 있음
                        // 이유:
                        //   수행평가 조건 때문에 Jwt를 사용해야해서 토큰에 role을 담으면 권한 관리가 어렵고,
                        //   UseCase에서 권한을 제어하면 로직이 간편해짐 (권한 검증 과정에서 얻은 데이터를 실제 로직에서 바로 사용 가능)


                        // Auth 도메인
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/reissue").permitAll()

                        // Account 도메인
                        .requestMatchers(HttpMethod.GET, "/accounts").permitAll()
                        .requestMatchers(HttpMethod.POST, "/accounts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/accounts/me").permitAll()
                        .requestMatchers(HttpMethod.GET, "/accounts/me/permissions").permitAll()
                        .requestMatchers(HttpMethod.GET, "/accounts/{accountId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/accounts/{accountId}/permissions").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/accounts/{accountId}").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/accounts/{accountId}/password").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/accounts/{accountId}/developer-permissions/{developerId}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/accounts/{accountId}/game-permissions/{gameId}").permitAll()

                        // Developer 도메인
                        .requestMatchers(HttpMethod.POST, "/developers").permitAll() // 개발자 등록을 해야 루트 계정이 만들어짐. 이 API는 공개되어 있어야 함
                        .requestMatchers(HttpMethod.GET, "/developers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/developers/{developerId}").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/developers/{developerId}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/developers/{developerId}").permitAll()

                        // Game 도메인
                        .requestMatchers(HttpMethod.GET, "/games").permitAll()
                        .requestMatchers(HttpMethod.POST, "/games").permitAll()
                        .requestMatchers(HttpMethod.GET, "/games/{gameId}").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/games/{gameId}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/games/{gameId}").permitAll()

                        // Patch 도메인
                        .requestMatchers(HttpMethod.POST, "/games/{gameId}/patches").permitAll()
                        .requestMatchers(HttpMethod.GET, "/games/{gameId}/patches/{patchId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/games/{gameId}/patches").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/patches/{patchId}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/patches/{patchId}").permitAll()

                        // Catalog 도메인
                        .requestMatchers(HttpMethod.POST, "/patches/{patchId}/catalog").permitAll()
                        .requestMatchers(HttpMethod.POST, "/patches/{patchId}/catalog-hash").permitAll()
                        .requestMatchers(HttpMethod.GET, "/patches/{patchId}/catalog/uploaded").permitAll()
                        .requestMatchers(HttpMethod.GET, "/patches/{patchId}/catalog-hash/uploaded").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/patches/{patchId}/catalog").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/patches/{patchId}/catalog-hash").permitAll()

                        // Bundle 도메인
                        .requestMatchers(HttpMethod.GET, "/patches/{patchId}/bundles").permitAll()
                        .requestMatchers(HttpMethod.GET, "/games/{gameId}/bundles").permitAll()
                        .requestMatchers(HttpMethod.POST, "/games/{gameId}/bundles").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/games/{gameId}/bundles").permitAll()

                        // Permission 도메인
                        .requestMatchers(HttpMethod.PUT, "/developers/{developerId}/permissions/{accountId}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/games/{gameId}/permissions/{accountId}").permitAll()

                        .anyRequest().denyAll()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
