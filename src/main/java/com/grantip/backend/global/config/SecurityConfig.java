package com.grantip.backend.global.config;

import com.grantip.backend.domain.token.service.TokenService;
import com.grantip.backend.domain.user.service.CustomUserDetailsService;
import com.grantip.backend.global.filter.JWTFilter;
import com.grantip.backend.global.util.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final TokenService tokenService;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // cors 처리
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // csrf disable
                .csrf(auth -> auth.disable())

                // form 로그인 방식 disable
                .formLogin(auth -> auth.disable())

                // http basic 인증 방식 disable
                .httpBasic(auth -> auth.disable())

                // 인가 처리
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/actuator/health",
                                "/auth/login", "/auth/signup", "/auth/reissue",
                                "/auth/logout",
                                "/email/send", "/email/verify",
                                "/api/scholarships",
                                "/api/scholarships/**",
                                "/api/regions/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/auth/logout").permitAll()       // 프리플라이트 허용
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()

                )

                // filter 추가
                .addFilterBefore(new JWTFilter(jwtUtil, tokenService, customUserDetailsService), UsernamePasswordAuthenticationFilter.class)

                // 세션 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 익명 사용자 허용
                .anonymous(anonymous -> anonymous
                        .principal("anonymousUser")
                        .authorities("ROLE_ANONYMOUS"))

                // 예외 처리: 모든 인증 불필요 경로에서 403 차단 제거
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_OK); // 403 대신 200 OK
                        }));
        return http.build();
    }

    /**
     * CORS 설정 소스 빈
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList("*")); // 모든 Origin 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

