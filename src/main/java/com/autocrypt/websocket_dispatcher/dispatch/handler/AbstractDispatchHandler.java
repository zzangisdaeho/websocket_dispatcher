package com.autocrypt.websocket_dispatcher.dispatch.handler;

import com.autocrypt.websocket_dispatcher.channel.PublishChannel;
import com.autocrypt.websocket_dispatcher.dispatch.dto.DispatchMessage;

public abstract class AbstractDispatchHandler implements DispatchHandler {

    protected final PublishChannel publishChannel;

    protected AbstractDispatchHandler(PublishChannel publishChannel) {
        this.publishChannel = publishChannel;
    }

    protected void publish(DispatchMessage dispatchMessage){
        publishChannel.publish(dispatchMessage.generateChannelRequest());
    }
}
