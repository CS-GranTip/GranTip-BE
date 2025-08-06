package com.grantip.backend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
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
  public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory, ObjectMapper objectMapper) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
    return template;
  }

  /**
   * @Cacheable을 위한 CacheManager 설정
   */
  @Bean
  public CacheManager cacheManager(LettuceConnectionFactory factory, ObjectMapper objectMapper) {
    RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)))
        .entryTtl(Duration.ofDays(1));

    return RedisCacheManager.RedisCacheManagerBuilder
        .fromConnectionFactory(factory)
        .cacheDefaults(redisCacheConfiguration)
        .build();
  }
}
