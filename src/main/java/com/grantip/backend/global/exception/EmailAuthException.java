package com.grantip.backend.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 이메일 인증(OTP) 과정에서
 * - 코드 불일치
 * - 만료
 * 등의 이유로 인증에 실패했을 때 던지는 전용 예외입니다.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailAuthException extends RuntimeException {
    public EmailAuthException() {
        super("이메일 인증에 실패했습니다.");
    }

    public EmailAuthException(String message) {
        super(message);
    }

    public EmailAuthException(String message, Throwable cause) {
        super(message, cause);
    }

}

