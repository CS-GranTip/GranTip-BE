package com.grantip.backend.domain.user.controller;

import com.grantip.backend.domain.user.dto.SignupRequest;
import com.grantip.backend.domain.user.dto.UserResponse;
import com.grantip.backend.domain.user.service.UserService;
import com.grantip.backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
