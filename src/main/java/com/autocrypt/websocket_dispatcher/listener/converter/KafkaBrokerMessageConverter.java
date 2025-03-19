package com.autocrypt.websocket_dispatcher.listener.converter;

import com.autocrypt.websocket_dispatcher.dispatch.dto.DispatchMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;

import java.util.UUID;

/**
 * 김대호
 * 아래와 같은 header기반으로 돌아감
 * {
 *   "target":"daeho",
 *   "targetGroup": "g1",
 *   "type":"broadcast"
 * }
 * trxId가 없을경우 (있어야 한다. 없으면 사고다. 해당 컨버터는 샘플같은거라 넣어주도록 한다.) 앞선 컨텍스트가 끊겨 새로운 uuid발급하도록 해놓음
 * event값은 topic으로 넣어줌.
 */
@Slf4j
public class KafkaBrokerMessageConverter implements BrokerMessageConverter<ConsumerRecord<String, String>> {

    private String extractHeader(Headers headers, String key) {
        if (headers.lastHeader(key) != null) {
            return new String(headers.lastHeader(key).value());
        }
        return null;
    }

    @Override
    public DispatchMessage convert(ConsumerRecord<String, String> record) {
        // ConsumerRecord 데이터를 DispatchMessage로 변환하는 로직 구현
        return DispatchMessage.builder()
                .trxId(record.key() != null? record.key() : UUID.randomUUID().toString())
                .target(extractHeader(record.headers(), "target"))
                .targetGroup(extractHeader(record.headers(), "targetGroup"))
                .body(record.value())
                .type(DispatchMessage.DispatchType.fromHeader(extractHeader(record.headers(), "type")))
                // 내부 이벤트 토픽명으로 정함
                .event(record.topic())
                .build();
    }

}
