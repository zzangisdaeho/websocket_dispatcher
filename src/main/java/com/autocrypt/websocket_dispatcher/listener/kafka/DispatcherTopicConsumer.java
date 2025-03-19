package com.autocrypt.websocket_dispatcher.listener.kafka;

import com.autocrypt.websocket_dispatcher.dispatch.dto.DispatchMessage;
import com.autocrypt.websocket_dispatcher.dispatch.Dispatcher;
import com.autocrypt.websocket_dispatcher.listener.converter.BrokerMessageConverter;
import com.autocrypt.websocket_dispatcher.listener.converter.KafkaBrokerMessageConverter;
import com.autocrypt.websocket_dispatcher.listener.MessageConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * 김대호
 * 코드는 비효율적인지만 유지보수의 편의성을 위해
 * 토픽 하나당 리스너 1개, 해당 토픽에 맞는 컨버터를 등록해서 사용하도록 구성.
 * fail하는 경우에 대한 설정은 각 리스너에서 어노테이션으로 핸들링 가능하나 일단 KafkaListenerConfig에 전역설정 해놓음
 */
@Slf4j
public class DispatcherTopicConsumer implements MessageConsumer<ConsumerRecord<String, String>> {

    private final Dispatcher dispatcher;

    private final BrokerMessageConverter<ConsumerRecord<String, String>> kafkaDispatchMessageConverter = new KafkaBrokerMessageConverter();

    public DispatcherTopicConsumer(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @KafkaListener(id = "myId", topics = "dispatcher.topic")
    public void consume(ConsumerRecord<String, String> record) {
        log.info("📥 Received Kafka message: {}", record.value());
        DispatchMessage dispatchMessage = kafkaDispatchMessageConverter.convert(record);
        dispatcher.routeMessage(dispatchMessage);
    }
}
