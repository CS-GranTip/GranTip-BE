package com.grantip.backend.domain.user.controller;

import com.grantip.backend.domain.user.dto.SignupRequest;
import com.grantip.backend.domain.user.dto.UpdateRequest;
import com.grantip.backend.domain.user.dto.UserResponse;
import com.grantip.backend.domain.user.dto.VerifyPassword;
import com.grantip.backend.domain.user.service.UserService;
import com.grantip.backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/mypage")
    public ResponseEntity<ApiResponse<UserResponse>> getMyPage(@AuthenticationPrincipal UserDetails userDetails){

        UserResponse userResponse = new UserResponse("asdf", userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<UserResponse>builder().result(userResponse).success(true).code(201).message("마이페이지에 성공했습니다.").build());
    }
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateInfo(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UpdateRequest request){

        String identifier = userDetails.getUsername();
        userService.updateInfo(identifier, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<Void>builder().success(true).code(201).message("회원정보가 수정되었습니다.").build());
    }
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(@AuthenticationPrincipal UserDetails userDetails){
        String identifier = userDetails.getUsername();
        userService.deleteUser(identifier);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<Void>builder().success(true).code(200).message("회원 탈퇴에 성공했습니다.").build());
    }
    // 유효한 비밀번호인지 검증 내용 확인, 프론트에서 처리할수도?
    @PatchMapping("/password/verify")
    public ResponseEntity<ApiResponse<Void>> verifyPassword(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody VerifyPassword verifyPassword){
        userService.verifyPassword(verifyPassword);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<Void>builder().success(true).code(200).message("비밀번호가 일치합니다.").build());
    }


}
