package com.autocrypt.websocket_dispatcher.listener.converter;

import com.autocrypt.websocket_dispatcher.dispatch.dto.DispatchMessage;

/**
 * 김대호
 * broker로 부터 수신한 메세지를 의도한 DispatchMessage 형식으로 바꿔주는 컨버터
 * 각 리스너마다 필요에 의해 추가 구현하여 의도한 로직으로 만들어 줄 수 있음.
 * @param <T>
 */
public interface BrokerMessageConverter<T> {

    DispatchMessage convert(T message);
}
