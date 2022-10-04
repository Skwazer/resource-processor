package com.epam.resourceprocessor.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author www.epam.com
 */
@Configuration
public class RMQConfig {
    @Value("${spring.rabbitmq.queue.create}")
    private String createQueue;

    @Value("${spring.rabbitmq.queue.delete}")
    private String deleteQueue;

    @Value("${spring.rabbitmq.queue.create_dead}")
    private String createDeadQueue;

    @Value("${spring.rabbitmq.queue.delete_dead}")
    private String deleteDeadQueue;

    @Bean
    public Queue createQueue() {
        return new Queue(createQueue, true);
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue(deleteQueue, true);
    }

    @Bean
    public Queue createDeadQueue() {
        return new Queue(createDeadQueue, true);
    }

    @Bean
    public Queue deleteDeadQueue() {
        return new Queue(deleteDeadQueue, true);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new SimpleMessageConverter();
    }
}
