package com.grantip.backend.domain.user.controller;


//import io.swagger.v3.oas.annotations.tags.Tag;
import com.grantip.backend.domain.token.domain.dto.TokenDto;
import com.grantip.backend.domain.token.service.TokenService;
import com.grantip.backend.domain.user.domain.dto.CustomUserDetails;
import com.grantip.backend.domain.user.domain.dto.request.LoginRequest;
import com.grantip.backend.domain.user.domain.dto.request.SignupRequest;
import com.grantip.backend.domain.user.service.AuthService;
import com.grantip.backend.global.code.ErrorCode;
import com.grantip.backend.global.exception.CustomException;
import com.grantip.backend.global.response.ApiResponse;
import com.grantip.backend.global.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
//@Tag(name = "사용자 인증 API")
public class AuthController{
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final TokenService tokenService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request){
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<Void>builder().success(true).code(201).message("회원가입에 성공했습니다.").build());
    }

    /*
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok()
                .body(ApiResponse.<LoginResponse>builder().result(response).success(true).code(200).message("로그인에 성공했습니다.").build());
    }

     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@RequestBody LoginRequest request) {

        try {
            TokenDto tokenDto = authService.login(request);
            // ⬇️ RefreshToken을 HttpOnly 쿠키로 설정
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenDto.getRefreshToken())
                    .httpOnly(true)
                    .secure(true) // HTTPS 사용할 경우 true
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7일
                    .sameSite("Strict") // 또는 "Lax", 필요에 따라 조정
                    .build();

            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + tokenDto.getAccessToken())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(ApiResponse.<Void>builder().success(true).code(201).message("로그인에 성공했습니다.").build());
        } catch (BadCredentialsException e){
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

    }


    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<Void>> reissue(HttpServletRequest request){
        // String refreshToken = (String) request.getAttribute("refreshToken"); 로컬스토리지 일때인듯
        String requestRefreshToken = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie: cookies) {
            if(cookie.getName().equals("refreshToken")) {
                requestRefreshToken = cookie.getValue();
            }
        }
        if (requestRefreshToken == null) {
            //return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }
        TokenDto tokenDto = authService.reissue(requestRefreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokenDto.getRefreshToken())
                .httpOnly(true)
                .secure(true) // HTTPS 사용할 경우 true
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7일
                .sameSite("Strict") // 또는 "Lax", 필요에 따라 조정
                .build();

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + tokenDto.getAccessToken())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.<Void>builder().success(true).code(201).message("토큰이 성공적으로 재발급 되었습니다.").build());
    }
    // 아니다 refreshToken 도갖고와서 또 쿠키적용까지 똑같이 해야겠네

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                    HttpServletRequest request){
        // 쿠키확인해서 refresh 기간 지났는지, 있는지 확인해서 예외처리 가능
        String accessToken = (String) request.getAttribute("accessToken");
        authService.logout(userDetails.getEmail(), accessToken);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(ApiResponse.<Void>builder().success(true).code(201).message("로그아웃에 성공하였습니다.").build());

    }
}

