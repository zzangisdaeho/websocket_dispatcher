package com.autocrypt.websocket_dispatcher.dispatch.repository;

/**
 * 김대호
 * connection 정보 보관용 인터페이스
 */
public interface SessionRepository {

    //유저가 어떤 host에 붙어있는지 조회
    String get(String userId);
}
