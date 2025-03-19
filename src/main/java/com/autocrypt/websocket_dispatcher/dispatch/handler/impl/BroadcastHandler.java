package com.autocrypt.websocket_dispatcher.dispatch.handler.impl;

import com.autocrypt.websocket_dispatcher.channel.PublishChannel;
import com.autocrypt.websocket_dispatcher.dispatch.dto.DispatchMessage;
import com.autocrypt.websocket_dispatcher.dispatch.handler.AbstractDispatchHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 김대호
 * DispatchMessage type BROADCAST 처리 핸들러
 */
@Slf4j
public class BroadcastHandler extends AbstractDispatchHandler {


    public BroadcastHandler(PublishChannel publishChannel) {
        super(publishChannel);
    }

    @Override
    public DispatchMessage.DispatchType handleFor() {
        return DispatchMessage.DispatchType.BROADCAST;
    }

    @Override
    public void handle(DispatchMessage dispatchMessage) {
        this.publish(dispatchMessage);
    }
}