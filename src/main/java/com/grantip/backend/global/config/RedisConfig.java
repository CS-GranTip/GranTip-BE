package com.grantip.backend.global.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    // ← .ssl.enabled 로 바꿔야 yml 값이 바인딩됩니다.
    @Value("${spring.data.redis.ssl.enabled}")
    private boolean useSsl;

    // 선택 사항: application-prod.yml 의 timeout (30000ms) 바인딩 예시
    @Value("${spring.data.redis.timeout}")
    private Duration commandTimeout;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        // 1) 서버 정보
        RedisStandaloneConfiguration serverConfig =
                new RedisStandaloneConfiguration(redisHost, redisPort);

        // 2) 클라이언트 옵션 (SSL / 타임아웃 등)
        LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigBuilder =
                LettuceClientConfiguration.builder()
                        .commandTimeout(commandTimeout);
        if (useSsl) {
            clientConfigBuilder.useSsl();
        }
        LettuceClientConfiguration clientConfig = clientConfigBuilder.build();

        // 3) 팩토리 생성
        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(LettuceConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
