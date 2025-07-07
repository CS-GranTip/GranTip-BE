package com.grantip.backend.domain.user.service;


import com.grantip.backend.domain.token.dto.TokenDto;
import com.grantip.backend.domain.token.service.TokenService;
import com.grantip.backend.domain.user.dto.CustomUserDetails;
import com.grantip.backend.domain.user.dto.LoginRequest;
import com.grantip.backend.domain.user.dto.LoginResponse;
import com.grantip.backend.domain.user.dto.SignupRequest;
import com.grantip.backend.domain.user.entity.Role;
import com.grantip.backend.global.code.ErrorCode;
import com.grantip.backend.global.exception.CustomException;
import com.grantip.backend.global.util.JWTUtil;
import com.grantip.backend.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final TokenService tokenService;

    private static final String REDIS_PREFIX = "RT:";


    @Transactional
    public void signup(SignupRequest request){
        if(userService.existsByLoginId(request.getLoginId())){
            throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        Role role;
        try {
            // 왜 에러더라
            role = request.getRole();
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_ROLE);
        }
        //////////////////////////////////////////////////////////
        User user = User.builder()
                .loginId(request.getLoginId())
                .password(passwordEncoder.encode(request.getPassword()))
                .username(request.getUsername())
                .role(role)
                .email(request.getEmail())
                .phone(request.getPhone())
                .university(request.getUniversity())
                .highSchool(request.getHighschool())
                .univYear(request.getUnivYear())
                .gender(request.getGender())
                .address(request.getAddress())
                .resiAddress(request.getResiAddress())
                .build();
        userService.saveUser(user);
    }

    public LoginResponse login(LoginRequest request){
        try{
            // 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword())
            );

            // 인증 성공 시 사용자 정보 가져오기
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // 토큰 생성
            String accessToken = jwtUtil.createAccessToken(userDetails);
            String refreshToken = jwtUtil.createRefreshToken(userDetails);

            // 데베에 RefreshToken 저장
            tokenService.saveRefreshToken(userDetails.getUsername(), refreshToken);

            return new LoginResponse(accessToken, refreshToken);
        } catch (BadCredentialsException e){ // 이런거 자동완성 되나봄
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }
    }
    /* 에러 로그찍어보기
    log.warn("FCM 푸시 전송 실패 - token: {}, error: {}", token.getToken(), e.getMessage());
                if (e.getMessage().contains("registration-token-not-registered")) {
                    fcmTokenRepository.delete(token);
                }
     */

    public TokenDto reissue(String refreshToken) {
        System.out.println("1111111111111111111111111111111111");
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 토큰 부분만 추출
        refreshToken = refreshToken.replace("Bearer ", "");

        System.out.println("22222222222222222222222222222222222");
        // refresh토큰 검증
        if (!jwtUtil.isValid(refreshToken) || !"refreshToken".equals(jwtUtil.getCategory(refreshToken)) ) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String loginId = jwtUtil.getSubject(refreshToken);

        System.out.println("333333333333333333333333333333333333333");
        String savedToken = tokenService.getRefreshToken(loginId);
        if(savedToken == null){ // 데베에 없는 리프레시 토큰
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        System.out.println("44444444444444444444444444444444444444  ");

        CustomUserDetails userDetails = userService.loadUserDetailsByLoginId(loginId);

        // refresh rotate네 리이슈할때마다 리프레시도 재발급
        String newAccessToken = jwtUtil.createAccessToken(userDetails);
        String newRefreshToken = jwtUtil.createRefreshToken(userDetails);

        tokenService.saveRefreshToken(loginId, newRefreshToken); // id가 같으니 덮어씌워지네
        // 이전 리프레시토큰 블랙리스트 처리해야되는데 id랑 같이저장하니까 덮어씌워지겠지?

        return new TokenDto(newAccessToken, newRefreshToken);
    }

    public void logout(String identifier, String accessToken){
        // RefreshToken 삭제
        tokenService.deleteRefreshToken(identifier);

        // AccessToken 남은 만료 시간 추출 후 블랙리스트 처리
        accessToken = accessToken.replace("Bearer ", "");
        long expiration = jwtUtil.getRemainingTime(accessToken);
        //tokenService.setBlacklist(accessToken, expiration);
    }
}

