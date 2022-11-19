package com.epam.resourceprocessor.consumer;

import com.epam.resourceprocessor.producer.RMQPublisher;
import com.epam.resourceprocessor.service.ResourceProcessorService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class RMQConsumer {

    private final ObjectMapper objectMapper;
    private final ResourceProcessorService processorService;
    private final RMQPublisher publisher;

    @RabbitListener(queues = {"${spring.rabbitmq.queue.create}"})
    public void receiveCreationEvents(@Payload String id) {
        log.info("Consumed message from CREATE queue: " + id);
        val resourceId = Integer.parseInt(id);
        try {
            processorService.processResource(resourceId);
        } catch (Throwable ex) {
            log.error("Error in processing message for resource id: " + id, ex);
            log.info("Publish to DEAD queue message for resource id: " + id);
            publisher.publishDeadCreateEvent(id);
        }
    }

    @RabbitListener(queues = {"${spring.rabbitmq.queue.delete}"})
    public void receiveDeleteEvents(@Payload String idListMessage) {
        log.info("Consumed message from DELETE queue: " + idListMessage);
        List<Integer> idList = null;
        try {
            idList = objectMapper.readValue(idListMessage, new TypeReference<List<Integer>>() {
            });
            processorService.deleteResources(idList);
        } catch (Throwable ex) {
            log.error("Error in processing message: " + idListMessage);
            log.info("Publish to DEAD queue message: " + idListMessage);
            publisher.publishDeadDeleteEvent(idListMessage);
        }
    }

}
