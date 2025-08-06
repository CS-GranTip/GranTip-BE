package com.grantip.backend.domain.email.controller;

import com.grantip.backend.domain.email.domain.dto.reqest.EmailRequest;
import com.grantip.backend.domain.email.domain.dto.reqest.EmailVerify;
import com.grantip.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;

public interface EmailControllerDocs {
  @Operation(
      summary = "이메일 인증 코드 전송",
      description = """
            ### 요청 파라미터
            - `email` (String, required): 인증 코드를 전송할 이메일 주소

            ### 응답 데이터
            - 없음 (Void)

            ### 사용 방법
            1. HTTP `POST /email/send` 요청을 보냅니다.
            2. 요청 JSON 바디 예시:
               ```json
               {
                 "email": "user@example.com"
               }
               ```
            3. 서버는 해당 이메일로 인증 코드를 전송합니다.

            ### 유의 사항
            - `email` 필드는 올바른 이메일 형식을 준수해야 합니다.
            - 인증 코드 재전송 정책이 있을 수 있습니다.

            ### 예외 처리
            - `BAD_REQUEST` (400): 이메일 형식 오류 또는 필수 값 누락 시 반환됩니다.
            - `INTERNAL_SERVER_ERROR` (500): 서버 내부 오류 발생 시 반환됩니다.
            <!-- EmailService에서 발생 가능한 커스텀 예외 코드가 있다면 알려주세요. -->
            """
  )
  ResponseEntity<ApiResponse<Void>> sendCode(
      EmailRequest req
  );

  @Operation(
      summary = "이메일 인증 코드 검증",
      description = """
            ### 요청 파라미터
            - `email` (String, required): 인증 대상 이메일
            - `code` (String, required): 사용자가 입력한 인증 코드

            ### 응답 데이터
            - 없음 (Void)

            ### 사용 방법
            1. HTTP `POST /email/verify` 요청을 보냅니다.
            2. 요청 JSON 바디 예시:
               ```json
               {
                 "email": "user@example.com",
                 "code": "123456"
               }
               ```
            3. 서버는 입력한 코드가 일치하면 인증을 완료합니다.

            ### 유의 사항
            - 인증 코드는 이메일 수신 후 일정 시간 내에 사용해야 합니다.
            - 재전송된 코드가 있을 경우 최신 코드만 유효합니다.

            ### 예외 처리
            - `BAD_REQUEST` (400): 이메일 형식 오류, 필수 값 누락 또는 코드 불일치 시 반환됩니다.
            - `INTERNAL_SERVER_ERROR` (500): 서버 내부 오류 발생 시 반환됩니다.
            <!-- EmailService에서 발생 가능한 커스텀 예외 코드가 있다면 알려주세요. -->
            """
  )
  ResponseEntity<ApiResponse<Void>> verifyCode(
      EmailVerify req
  );
}
