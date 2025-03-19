package com.autocrypt.websocket_dispatcher.dispatch.dto;

import com.autocrypt.websocket_dispatcher.channel.dto.ChannelRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;

/**
 * 김대호
 * Dispatcher가 처리할 DTO
 * spring validation 적용되어있음.
 */
@Getter
@AllArgsConstructor
@Builder
public class DispatchMessage {

    //처리 단위 고유값
    @NotBlank
    protected String trxId;
    // 메세지 본문
    @NotBlank
    protected String body;
    // 전송 타겟 커넥션
    protected String target;
    // 전송 타겟 커넥션이 속한 그룹 (옵션)
    protected String targetGroup;
    // 내부 정의 메세지 이벤트 값
    protected String event;
    // dispatch dto type (현재 유니케스트, 브로드케스트 타입만 존재)
    protected DispatchType type;

    public boolean isBroadcast() {
        return type == DispatchType.BROADCAST;
    }

    public ChannelRequest generateChannelRequest() {
        return this.baseBuilder()
                .build();
    }

    public ChannelRequest generateChannelRequest(String channel) {
        return this.baseBuilder()
                .channel(channel)
                .build();
    }

    private ChannelRequest.ChannelRequestBuilder baseBuilder(){
        return ChannelRequest.builder()
                .trxId(this.trxId)
                .to(this.target)
                .group(this.targetGroup)
                .event(this.event)
                .msg(this.body)
                .broadcast(this.isBroadcast());
    }

    public enum DispatchType {
        BROADCAST, UNICAST;

        public static DispatchType fromHeader(String broadcastHeader) {
            return Arrays.stream(DispatchType.values())
                    .filter(type -> type.name().equalsIgnoreCase(broadcastHeader))
                    .findFirst()
                    .orElse(DispatchType.UNICAST);
        }
    }

}
