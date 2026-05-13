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


                        // 현재 서비스는 권한 별로 특정 기능 사용을 제한하고 있는데,
                        // hasRole이나 HasAuthority를 걸지 않고 비즈니스 로직에서 직접 권한을 확인하고 있음
                        // 이유:
                        //   수행평가 조건 때문에 Jwt를 사용해야해서 토큰에 role을 담으면 권한 관리가 어렵고,
                        //   UseCase에서 권한을 제어하면 로직이 간편해짐 (권한 검증 과정에서 얻은 데이터를 실제 로직에서 바로 사용 가능)


                        // Auth 도메인
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/reissue").permitAll()

                        // Account 도메인
                        .requestMatchers(HttpMethod.GET, "/accounts").authenticated()
                        .requestMatchers(HttpMethod.POST, "/accounts").authenticated()
                        .requestMatchers(HttpMethod.GET, "/accounts/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/accounts/me/permissions").authenticated()
                        .requestMatchers(HttpMethod.GET, "/accounts/{accountId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/accounts/{accountId}/permissions").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/accounts/{accountId}").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/accounts/{accountId}/password").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/accounts/{accountId}/developer-permissions/{developerId}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/accounts/{accountId}/game-permissions/{gameId}").authenticated()

                        // Developer 도메인
                        .requestMatchers(HttpMethod.GET, "/developers").authenticated()
                        .requestMatchers(HttpMethod.POST, "/developers").authenticated()
                        .requestMatchers(HttpMethod.GET, "/developers/{developerId}").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/developers/{developerId}").authenticated()

                        // Game 도메인
                        .requestMatchers(HttpMethod.GET, "/games").authenticated()
                        .requestMatchers(HttpMethod.POST, "/games").authenticated()
                        .requestMatchers(HttpMethod.GET, "/games/{gameId}").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/games/{gameId}").authenticated()

                        // Patch 도메인
                        .requestMatchers(HttpMethod.POST, "/games/{gameId}/patches").authenticated()
                        .requestMatchers(HttpMethod.GET, "/games/{gameId}/patches/{patchId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/games/{gameId}/patches").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/patches/{patchId}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/patches/{patchId}/catalog").authenticated()
                        .requestMatchers(HttpMethod.POST, "/patches/{patchId}/catalog-hash").authenticated()
                        .requestMatchers(HttpMethod.POST, "/patches/{patchId}/bundles").authenticated()
                        .requestMatchers(HttpMethod.GET, "/patches/{patchId}/catalog/uploaded").authenticated()
                        .requestMatchers(HttpMethod.GET, "/patches/{patchId}/catalog-hash/uploaded").authenticated()
                        .requestMatchers(HttpMethod.GET, "/patches/{patchId}/bundles").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/patches/{patchId}/catalog").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/patches/{patchId}/catalog-hash").authenticated()
                        .requestMatchers(HttpMethod.GET, "/games/{gameId}/bundles").authenticated()

                        // Permission 도메인
                        .requestMatchers(HttpMethod.PUT, "/developers/{developerId}/permissions/{accountId}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/games/{gameId}/permissions/{accountId}").authenticated()

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
