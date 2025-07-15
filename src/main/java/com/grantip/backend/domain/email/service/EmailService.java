package com.grantip.backend.domain.email.service;

public interface EmailService {

    /**
     * 주어진 이메일 주소로 인증 코드를 생성하여 전송합니다.
     * 구현체는 내부에서 랜덤 6자리(또는 원하는 길이)의 코드를 생성하고,
     * Redis 등에 TTL과 함께 저장한 뒤 메일을 발송해야 합니다.
     *
     * @param email 수신자 이메일 주소
     * @return 생성된 인증 코드
     */
    void sendVerificationCode(String email);

    void verifyCode(String email, String code);
}

