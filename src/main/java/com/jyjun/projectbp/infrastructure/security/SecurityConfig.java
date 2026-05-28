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
                        // 이 경로로 bundle이나 catalog에 접근할 수 있음
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
                        .requestMatchers(HttpMethod.GET, "/accounts").authenticated()
                        .requestMatchers(HttpMethod.POST, "/accounts").authenticated()
                        .requestMatchers(HttpMethod.GET, "/accounts/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/accounts/me/permissions").authenticated()
                        .requestMatchers(HttpMethod.GET, "/accounts/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/accounts/*/permissions").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/accounts/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/accounts/*/password").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/accounts/*").authenticated()

                        // Developer 도메인
                        .requestMatchers(HttpMethod.POST, "/developers").permitAll() // 개발자 등록을 해야 루트 계정이 만들어짐. 이 API는 공개되어 있어야 함
                        .requestMatchers(HttpMethod.GET, "/developers").authenticated()
                        .requestMatchers(HttpMethod.GET, "/developers/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/developers/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/developers/*").authenticated()

                        // Game 도메인
                        .requestMatchers(HttpMethod.GET, "/games").authenticated()
                        .requestMatchers(HttpMethod.POST, "/games").authenticated()
                        .requestMatchers(HttpMethod.GET, "/games/*/version").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/games/*/version").authenticated()
                        .requestMatchers(HttpMethod.GET, "/games/*").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/games/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/games/*").authenticated()

                        // Patch 도메인
                        .requestMatchers(HttpMethod.POST, "/games/*/patches").authenticated()
                        .requestMatchers(HttpMethod.GET, "/games/*/patches/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/games/*/patches").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/patches/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/patches/*").authenticated()

                        // Catalog 도메인
                        .requestMatchers(HttpMethod.POST, "/patches/*/catalog").authenticated()
                        .requestMatchers(HttpMethod.POST, "/patches/*/catalog-hash").authenticated()
                        .requestMatchers(HttpMethod.GET, "/patches/*/catalog/uploaded").authenticated()
                        .requestMatchers(HttpMethod.GET, "/patches/*/catalog-hash/uploaded").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/patches/*/catalog").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/patches/*/catalog-hash").authenticated()

                        // Bundle 도메인
                        .requestMatchers(HttpMethod.GET, "/patches/*/bundles").authenticated()
                        .requestMatchers(HttpMethod.GET, "/games/*/bundles").authenticated()
                        .requestMatchers(HttpMethod.POST, "/games/*/bundles").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/games/*/bundles").authenticated()

                        // Permission 도메인
                        .requestMatchers(HttpMethod.PUT, "/developers/*/permissions/*").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/games/*/permissions/*").authenticated()

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
