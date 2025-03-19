package com.autocrypt.websocket_dispatcher.config.listener;

import com.autocrypt.websocket_dispatcher.dispatch.Dispatcher;
import com.autocrypt.websocket_dispatcher.listener.MessageConsumer;
import com.autocrypt.websocket_dispatcher.listener.kafka.DispatcherTopicConsumer;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * 김대호
 * listener 등록
 * kafka consumer 구현체 등록
 */
@Configuration
@Profile("kafka")
@Slf4j
public class KafkaListenerConfig {

    @Bean
    public MessageConsumer<?> messageConsumer(Dispatcher dispatcher) {
        return new DispatcherTopicConsumer(dispatcher);
    }

    /**
     * Kafka consumer error handler
     * 최대 2번 재실행 후 (backoff 1초)
     * 최종 실패시 DLQ로 보냄. (원 토픽에 suffix로 -dlt가 붙은 토픽으로 전송)
     */
    @Bean
    public CommonErrorHandler errorHandler(KafkaOperations<Object, Object> template) {
        return new DefaultErrorHandler(
                (record, exception) -> logErrorAndSendToDLQ(record, exception, template),
                //1초 간격으로 2번 더 재시도
                new FixedBackOff(1000L, 2)
        ){
            {
                addNotRetryableExceptions(ConstraintViolationException.class);
            }
        };
    }

    /**
     * 김대호
     * 에러 핸들러 내부 동작로직
     * @param record
     * @param exception
     * @param template
     */
    private void logErrorAndSendToDLQ(ConsumerRecord<?, ?> record, Exception exception, KafkaOperations<Object, Object> template) {
        log.error("🚨 Kafka message failed after retries: Topic={}, Partition={}, Offset={}, Key={}, Value={}",
                record.topic(), record.partition(), record.offset(), record.key(), record.value(), exception);

        // DLQ 전송
        new DeadLetterPublishingRecoverer(template).accept(record, exception);
    }

//    @Bean
//    public CommonErrorHandler errorHandler(KafkaOperations<Object, Object> template) {
//        return new DefaultErrorHandler(
//                new DeadLetterPublishingRecoverer(template,
//                        (consumerRecord, e) -> new TopicPartition("my-custom-dlt", consumerRecord.partition())
//                ),
//                new FixedBackOff(1000L, 2));
//    }
}
