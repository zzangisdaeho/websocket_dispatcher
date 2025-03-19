package com.autocrypt.websocket_dispatcher.listener;

/**
 * 김대호
 * listener 인터페이스
 * @param <T> 메세지 형식
 */
public interface MessageConsumer<T> {

    void consume(T message);
}
