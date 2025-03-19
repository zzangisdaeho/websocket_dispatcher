package com.autocrypt.websocket_dispatcher.channel.redis;

import com.autocrypt.websocket_dispatcher.channel.PublishChannel;
import com.autocrypt.websocket_dispatcher.channel.dto.ChannelRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 김대호
 * redis pub/sub push용
 */
public class RedisPublishChannel implements PublishChannel {

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;

    public RedisPublishChannel(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    //특정 채널에 게시
    @Override
    public void publishUnicast(String channelName, ChannelRequest message) {
        stringRedisTemplate.convertAndSend(channelName, serialize(message));
    }

    //broadCast용 채널에 게시
    @Override
    public void publishBroadcast(ChannelRequest message) {
        stringRedisTemplate.convertAndSend(PublishChannel.WEBSOCKET_CHANNEL, serialize(message));
    }

    public String serialize(ChannelRequest message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
