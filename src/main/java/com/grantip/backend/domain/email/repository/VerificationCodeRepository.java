package com.grantip.backend.domain.email.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class VerificationCodeRepository {

    private static final String KEY_PREFIX = "email:code:";
    private final RedisTemplate<String, String> redisTemplate;
    private final Duration ttl;

    @Autowired
    public VerificationCodeRepository(
            RedisTemplate<String, String> redisTemplate,
            @Value("${app.otp.ttl}") long ttlMillis
    ) {
        this.redisTemplate = redisTemplate;
        this.ttl = Duration.ofMillis(ttlMillis);
    }

    public void saveCode(String email, String code) {
        String key = KEY_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, ttl);
    }

    public String getCode(String email) {
        return redisTemplate.opsForValue().get(KEY_PREFIX + email);
    }

    public void deleteCode(String email) {
        redisTemplate.delete(KEY_PREFIX + email);
    }
}

