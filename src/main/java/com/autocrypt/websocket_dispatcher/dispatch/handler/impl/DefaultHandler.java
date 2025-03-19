package com.autocrypt.websocket_dispatcher.dispatch.handler.impl;

import com.autocrypt.websocket_dispatcher.dispatch.dto.DispatchMessage;
import com.autocrypt.websocket_dispatcher.dispatch.handler.DispatchHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 김대호
 * 적합한 handler를 못찾았을때 실행되는 handler
 */
@Slf4j
public class DefaultHandler implements DispatchHandler {
    @Override
    public DispatchMessage.DispatchType handleFor() {
        return null;
    }

    @Override
    public void handle(DispatchMessage dispatchMessage) {
        log.info("I'm Default Handler You may fucked ^^: {}", dispatchMessage);
    }
}
