package com.autocrypt.websocket_dispatcher.config.dispatch.repository;

import com.autocrypt.websocket_dispatcher.dispatch.repository.SessionRepository;
import com.autocrypt.websocket_dispatcher.dispatch.repository.redis.RedisSessionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 김대호
 * repository 등록
 * redis 구현체 등록
 */
@Configuration
@Profile("redis")
public class RedisRepositoryConfig {

    /**
     * 김대호
     * sessionRepository redis구현체 등록
     * @param redisTemplate spring redis 자동 등록
     * @return
     */
    @Bean
    public SessionRepository sessionRepository(StringRedisTemplate redisTemplate) {
        return new RedisSessionRepository(redisTemplate);
    }
}
