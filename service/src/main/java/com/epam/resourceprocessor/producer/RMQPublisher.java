package com.epam.resourceprocessor.producer;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author www.epam.com
 */
@Component
@RequiredArgsConstructor
public class RMQPublisher {

    private final Queue createQueue;
    private final Queue deleteQueue;
    private final Queue createDeadQueue;
    private final Queue deleteDeadQueue;
    private final RabbitTemplate rabbitTemplate;
    private final MessageConverter messageConverter;

    public void publishCreateEvent(String message, Map<String, String> headers) {
        rabbitTemplate.convertAndSend(createQueue.getName(), buildMessage(message, headers, createQueue.getName()));
    }

    public void publishDeleteEvent(String message,  Map<String, String> headers) {
        rabbitTemplate.send(buildMessage(message, headers, deleteQueue.getName()));
    }

    public void publishDeadCreateEvent(String message) {
        rabbitTemplate.convertAndSend(createDeadQueue.getName(), message);
    }

    public void publishDeadDeleteEvent(String message) {
        rabbitTemplate.convertAndSend(deleteDeadQueue.getName(), message);
    }

    private Message buildMessage(String message, Map<String, String> headers, String queue) {
        val messageProperties = new MessageProperties();
        messageProperties.setConsumerQueue(queue);
        headers.forEach(messageProperties::setHeader);
        return messageConverter.toMessage(message, messageProperties);
    }



}
