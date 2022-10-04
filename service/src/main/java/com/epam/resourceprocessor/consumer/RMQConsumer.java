package com.epam.resourceprocessor.consumer;

import com.epam.resourceprocessor.producer.RMQPublisher;
import com.epam.resourceprocessor.service.ResourceProcessorService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author www.epam.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RMQConsumer {

    @Value("${spring.rabbitmq.retry.count}")
    private int rmqRetryAttempts;
    private static final String RETRY_ATTEMPT_HEADER = "retry-attempt";

    private final ObjectMapper objectMapper;
    private final ResourceProcessorService processorService;
    private final RMQPublisher publisher;

    @RabbitListener(queues = {"${spring.rabbitmq.queue.create}"})
    public void receiveCreationEvents(@Payload String id,
                                      @Header(value = RETRY_ATTEMPT_HEADER,
                                              required = false,
                                              defaultValue = "1") String attemptHeader) {
        try {
            log.info("Consumed message from CREATE queue: " + id + " , attempt=" + attemptHeader);
            val resourceId = Integer.parseInt(id);
            processorService.processResource(resourceId);
        } catch (Throwable ex) {
            log.error("Error in processing message: " + id);
            var currentAttempt = getCurrentAttempt(attemptHeader);
            if(currentAttempt <= rmqRetryAttempts) {
                log.info("Republishing of message : " + id);
                publisher.publishCreateEvent(id, Map.of(RETRY_ATTEMPT_HEADER, String.valueOf(++currentAttempt)));
            } else {
                log.info("Publish to DEAD queue message : " + id);
                publisher.publishDeadCreateEvent(id);
            }
        }
    }

    @RabbitListener(queues = {"${spring.rabbitmq.queue.delete}"})
    public void receiveDeleteEvents(@Payload String idListMessage,
                                    @Header(name = RETRY_ATTEMPT_HEADER,
                                            required = false,
                                            defaultValue = "1") String attemptHeader) {
        try {
            log.info("Consumed message from DELETE queue: " + idListMessage + " , attempt=" + attemptHeader);
            val idList = objectMapper.readValue(idListMessage, new TypeReference<List<Integer>>() {
            });
            processorService.deleteResources(idList);
        } catch (Throwable ex) {
            log.error("Error in processing message: " + idListMessage);
            var currentAttempt = getCurrentAttempt(attemptHeader);
            if(currentAttempt <= rmqRetryAttempts) {
                publisher.publishCreateEvent(idListMessage, Map.of(RETRY_ATTEMPT_HEADER, String.valueOf(++currentAttempt)));
            } else {
                log.info("Publish to DEAD queue message : " + idListMessage);
                publisher.publishDeadDeleteEvent(idListMessage);
            }
        }
    }

    private int getCurrentAttempt(String attemptHeader) {
        int attempt = 1;
        if(attemptHeader != null) {
            attempt = Integer.parseInt(attemptHeader);
        }
        return attempt;
    }
}
