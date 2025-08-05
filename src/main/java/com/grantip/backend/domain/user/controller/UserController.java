package com.grantip.backend.domain.user.controller;

import com.grantip.backend.domain.user.domain.dto.request.UpdateRequest;
import com.grantip.backend.domain.user.domain.dto.response.MyPageResponse;
import com.grantip.backend.domain.user.domain.dto.request.VerifyPassword;
import com.grantip.backend.domain.user.domain.dto.response.UserResponse;
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

    @GetMapping("/mypage")
    public ResponseEntity<ApiResponse<MyPageResponse>> getMyPage(@AuthenticationPrincipal UserDetails userDetails){

        String identifier = userDetails.getUsername();
        MyPageResponse myPageResponse = userService.myPageInfo(identifier);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<MyPageResponse>builder().success(true).code(201).result(myPageResponse).message("마이페이지 접속에 성공했습니다.").build());
    }
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateInfo(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UpdateRequest request){

        String identifier = userDetails.getUsername();
        userService.updateInfo(identifier, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<Void>builder().success(true).code(201).message("회원정보가 수정되었습니다.").build());
    }
    @GetMapping
    public ResponseEntity<ApiResponse<UserResponse>> userInfo(@AuthenticationPrincipal UserDetails userDetails){

        String identifier = userDetails.getUsername();
        UserResponse userRespone = userService.getInfo(identifier);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<UserResponse>builder().success(true).code(201).result(userRespone).message("회원정보 조회에 성공하였습니다.").build());
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
