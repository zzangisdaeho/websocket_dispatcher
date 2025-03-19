package com.autocrypt.websocket_dispatcher.dispatch.handler.impl;

import com.autocrypt.websocket_dispatcher.channel.dto.ChannelRequest;
import com.autocrypt.websocket_dispatcher.channel.PublishChannel;
import com.autocrypt.websocket_dispatcher.dispatch.dto.DispatchMessage;
import com.autocrypt.websocket_dispatcher.dispatch.handler.AbstractDispatchHandler;
import com.autocrypt.websocket_dispatcher.dispatch.repository.SessionRepository;
import com.autocrypt.websocket_dispatcher.dispatch.util.FirstValidFinder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 김대호
 * DispatchMessage type UNICAST 처리 핸들러
 */
@Slf4j
public class UnicastHandler extends AbstractDispatchHandler {

    private final SessionRepository sessionRepository;
    private final FirstValidFinder<String> firstValidFinder;
    //커넥션 대상이 붙어있을 수 있는 ws 서버 그룹
    private static final List<String> PREFIXES = List.of("user:", "car:");

    public UnicastHandler(PublishChannel publishChannel, SessionRepository sessionRepository, FirstValidFinder<String> firstValidFinder) {
        super(publishChannel);
        this.sessionRepository = sessionRepository;
        this.firstValidFinder = firstValidFinder;
    }

    @Override
    public DispatchMessage.DispatchType handleFor() {
        return DispatchMessage.DispatchType.UNICAST;
    }

    @Override
    public void handle(DispatchMessage dispatchMessage) {
        String target = dispatchMessage.getTarget();
        log.info("🔍 Searching for target session: {}", target);

        // 병렬적으로 Redis 조회 실행
        List<CompletableFuture<String>> futures = PREFIXES.stream()
                .map(prefix -> CompletableFuture.supplyAsync(() -> sessionRepository.get(prefix + target))) // 안전한 조회
                .toList();

        // 첫 번째 유효한 값 찾기
        CompletableFuture<String> firstValidFuture = firstValidFinder.findFirstValid(futures);
        ChannelRequest channelRequest = dispatchMessage.generateChannelRequest(firstValidFuture.join());

        publishChannel.publish(channelRequest);
    }

}