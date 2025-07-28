package com.grantip.backend.domain.email.controller;

import com.grantip.backend.domain.email.domain.dto.reqest.EmailRequest;
import com.grantip.backend.domain.email.domain.dto.reqest.EmailVerify;
import com.grantip.backend.domain.email.service.EmailService;
import com.grantip.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendCode(@RequestBody EmailRequest req) {
        emailService.sendVerificationCode(req.getEmail());
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .code(200)
                        .message("인증 코드 발송 완료")
                        .build()
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyCode(@RequestBody EmailVerify req) {
        emailService.verifyCode(req.getEmail(), req.getCode());
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .code(200)
                        .message("인증 성공")
                        .build()
        );
    }
}
