package com.grantip.backend.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grantip.backend.domain.token.service.TokenService;
import com.grantip.backend.global.code.ErrorCode;
import com.grantip.backend.global.response.ErrorResponse;
import com.grantip.backend.global.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        // 예외 URI 목록
        List<String> permitAllUris = Arrays.asList(
                "/auth/login", "/auth/signup", "/swagger-ui/", "/swagger-ui.html",
                "/swagger-resources/"
        );

        // 예외 URI는 필터링 없이 통과
        if (permitAllUris.stream().anyMatch(uri::startsWith) || uri.equals("/swagger-ui.html")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 인증 필수 URI만 검사
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.replace("Bearer ", "");

        // JWT 유효성 검사
        try {
            jwtUtil.isValid(token);
        } catch (ExpiredJwtException e) {
            // error response 설정
            setErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
            return;
        }

        /*
        // 블랙리스트 여부 확인
        if (tokenService.isBlacklisted(token)) {
            // error response 설정
            setErrorResponse(response, ErrorCode.TOKEN_ALREADY_LOGOUT);
            return;
        }
         */

        if (uri.equals("/auth/reissue")) {
            request.setAttribute("refreshToken", token);
        } else {
            request.setAttribute("accessToken", token);
        }

        // 사용자 정보 추출
        String identifier = jwtUtil.getSubject(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(identifier);
        Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // SecurityContext에 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 다음 필터로 넘기기
        filterChain.doFilter(request, response);
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getHttpStatus().value())
                .error(errorCode.getHttpStatus().name())
                .message(errorCode.getMessage())
                .build();

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}

