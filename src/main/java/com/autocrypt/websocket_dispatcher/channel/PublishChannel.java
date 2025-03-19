package com.autocrypt.websocket_dispatcher.channel;

import com.autocrypt.websocket_dispatcher.channel.dto.ChannelRequest;

/**
 * 김대호
 * channel publish interface
 */
public interface PublishChannel {

    String WEBSOCKET_CHANNEL = "websocket_channel";

    default void publish(ChannelRequest dto){
        if (!dto.isBroadcast()) {
            publishUnicast(dto.getChannel(), dto);
        } else {
            publishBroadcast(dto);
        }

    }

    void publishUnicast(String channelName, ChannelRequest dto);

    void publishBroadcast(ChannelRequest dto);
}
