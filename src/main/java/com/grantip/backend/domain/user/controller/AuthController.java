package com.grantip.backend.domain.user.controller;


//import io.swagger.v3.oas.annotations.tags.Tag;
import com.grantip.backend.domain.user.dto.CustomUserDetails;
import com.grantip.backend.domain.user.dto.LoginRequest;
import com.grantip.backend.domain.user.dto.LoginResponse;
import com.grantip.backend.domain.user.dto.SignupRequest;
import com.grantip.backend.domain.user.service.AuthService;
import com.grantip.backend.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
//@Tag(name = "사용자 인증 API")
public class AuthController{
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request){
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<Void>builder().success(true).code(201).message("회원가입에 성공했습니다.").build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok()
                .body(ApiResponse.<LoginResponse>builder().result(response).success(true).code(200).message("로그인에 성공했습니다.").build());
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<LoginResponse>> reissue(HttpServletRequest request){
        String refreshToken = (String) request.getAttribute("refreshToken");
        LoginResponse response = authService.reissue(refreshToken);
        return ResponseEntity.ok()
                .body(ApiResponse.<LoginResponse>builder().result(response).success(true).code(200).message("토큰이 성공적으로 재발급되었습니다.").build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    HttpServletRequest request){
        String accessToken = (String) request.getAttribute("accessToken");
        authService.logout(userDetails.getUsername(), accessToken);
        return ResponseEntity.ok().body(ApiResponse.<Void>builder().success(true).code(200).message("로그아웃에 성공했습니다.").build());
    }
}

