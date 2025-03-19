package com.autocrypt.websocket_dispatcher.dispatch.repository.redis;

import com.autocrypt.websocket_dispatcher.dispatch.repository.SessionRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 김대호
 * connection 정보를 redis에 보관하기 위한 구현체
 */
@Slf4j
public class RedisSessionRepository implements SessionRepository {
    private final StringRedisTemplate redisTemplate;

    public RedisSessionRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String get(String userId) {
        return redisTemplate.opsForValue().get(userId);
    }

    @PostConstruct
    public void init() {
        log.info("✅ Initializing Redis SessionRepository");
    }
}