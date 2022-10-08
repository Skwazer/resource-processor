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


@Component
@RequiredArgsConstructor
public class RMQPublisher {

    private final Queue createQueue;
    private final Queue deleteQueue;
    private final Queue createDeadQueue;
    private final Queue deleteDeadQueue;
    private final RabbitTemplate rabbitTemplate;
    private final MessageConverter messageConverter;

    public void publishCreateEvent(String message) {
        rabbitTemplate.convertAndSend(createQueue.getName(), buildMessage(message, createQueue.getName()));
    }

    public void publishDeleteEvent(String message) {
        rabbitTemplate.send(buildMessage(message, deleteQueue.getName()));
    }

    public void publishDeadCreateEvent(String message) {
        rabbitTemplate.convertAndSend(createDeadQueue.getName(), message);
    }

    public void publishDeadDeleteEvent(String message) {
        rabbitTemplate.convertAndSend(deleteDeadQueue.getName(), message);
    }

    private Message buildMessage(String message, String queue) {
        val messageProperties = new MessageProperties();
        messageProperties.setConsumerQueue(queue);
        return messageConverter.toMessage(message, messageProperties);
    }



}
