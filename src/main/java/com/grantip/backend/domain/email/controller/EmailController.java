package com.grantip.backend.domain.email.controller;

import com.grantip.backend.domain.email.domain.dto.reqest.EmailRequest;
import com.grantip.backend.domain.email.domain.dto.reqest.EmailVerify;
import com.grantip.backend.domain.email.service.EmailService;
import com.grantip.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@Tag(
    name = "이메일 API",
    description = "이메일 관련 API 제공"
)
public class EmailController implements EmailControllerDocs {
    private final EmailService emailService;

    @Override
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

    @Override
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
