package com.autocrypt.websocket_dispatcher.config.channel;

import com.autocrypt.websocket_dispatcher.channel.PublishChannel;
import com.autocrypt.websocket_dispatcher.channel.redis.RedisPublishChannel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 김대호
 * channel 등록
 * redis pubsub 구현체 등록
 */
@Profile("redis")
@Configuration
public class RedisChannelConfig {

    @Bean
    public PublishChannel publishChannel(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        return new RedisPublishChannel(stringRedisTemplate, objectMapper);
    }
}
