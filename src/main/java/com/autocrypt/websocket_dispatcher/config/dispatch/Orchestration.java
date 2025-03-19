package com.autocrypt.websocket_dispatcher.config.dispatch;

import com.autocrypt.websocket_dispatcher.channel.PublishChannel;
import com.autocrypt.websocket_dispatcher.dispatch.Dispatcher;
import com.autocrypt.websocket_dispatcher.dispatch.dto.DispatchMessage;
import com.autocrypt.websocket_dispatcher.dispatch.handler.impl.BroadcastHandler;
import com.autocrypt.websocket_dispatcher.dispatch.handler.DispatchHandler;
import com.autocrypt.websocket_dispatcher.dispatch.handler.impl.UnicastHandler;
import com.autocrypt.websocket_dispatcher.dispatch.repository.SessionRepository;
import com.autocrypt.websocket_dispatcher.dispatch.util.FirstValidFinder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 김대호
 * dispatcher 등록에 필요한 전반적인 설정 클래스
 */
@Configuration
public class Orchestration {

    @Bean
    @DependsOn("handlerMap")
    public Dispatcher dispatcher(Map<DispatchMessage.DispatchType, DispatchHandler> dispatchers) {
        return new Dispatcher(dispatchers);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


    /**
     * 김대호
     * handler 등록 클래스
     */
    @Configuration
    public static class DispatchHandlerRegistration {
        @Bean
        public DispatchHandler broadcastHandler(PublishChannel publishChannel) {
            return new BroadcastHandler(publishChannel);
        }

        @Bean
        public DispatchHandler unicastHandler(PublishChannel publishChannel, SessionRepository sessionRepository, Optional<FirstValidFinder<String>> firstValidFinder) {
            //별도로 등록한 validfinder 없을시 default로 넣어줌
            return new UnicastHandler(publishChannel, sessionRepository, firstValidFinder.orElse(FirstValidFinder.defaultFinder()));
        }
    }

    /**
     * 김대호
     * dispatch handler들을 Map 구조로 등록해주기 위한 빈 후처리기
     */
    @Slf4j
    @Configuration
    public static class DispatchHandlerPostProcessor implements BeanPostProcessor {

        private final Map<DispatchMessage.DispatchType, DispatchHandler> handlerMap = new HashMap<>();

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof DispatchHandler dispatchHandler) {
                DispatchMessage.DispatchType dispatchType = determineDispatchType(dispatchHandler);
                if (dispatchType != null) {
                    handlerMap.put(dispatchType, dispatchHandler);
                    log.info("✅ Registered DispatchHandler for type: {}", dispatchType);
                }
            }
            return bean;
        }

        /**
         * DispatchHandler의 타입을 판별하여 Enum과 매핑
         */
        private DispatchMessage.DispatchType determineDispatchType(DispatchHandler handler) {
            return handler.handleFor(); // 매칭되는 타입이 없는 경우 null 반환
        }

        /**
         * handlerMap 등록
         * @return
         */
        @Bean
        public Map<DispatchMessage.DispatchType, DispatchHandler> handlerMap() {
            return this.handlerMap;
        }

        //handler 등록 확인 로그용
        @EventListener(ApplicationReadyEvent.class)
        public void initializeHandlerMap() {
            log.info("🚀 All DispatchHandlers registered: {}", handlerMap);
        }
    }
}
