package com.autocrypt.websocket_dispatcher.dispatch.handler;

import com.autocrypt.websocket_dispatcher.dispatch.dto.DispatchMessage;

public interface DispatchHandler {

    DispatchMessage.DispatchType handleFor();

    void handle(DispatchMessage dispatchMessage);
}