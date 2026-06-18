package com.epam.learn.rs.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String RESOURCES_EXCHANGE = "resources.exchange";
    public static final String RESOURCE_UPLOADED_ROUTING_KEY = "resources.resource.uploaded";

    public static final String RESOURCE_PROCESSED_QUEUE = "resources.resource.processed.queue";
    public static final String RESOURCE_PROCESSED_ROUTING_KEY = "resources.resource.processed";

    @Bean
    public TopicExchange resourcesExchange() {
        return new TopicExchange(RESOURCES_EXCHANGE);
    }

    @Bean
    public Queue resourceProcessedQueue() {
        return QueueBuilder
            .durable(RESOURCE_PROCESSED_QUEUE)
            .build();
    }

    @Bean
    public Binding resourceProcessedBinding(TopicExchange resourcesExchange, Queue resourceProcessedQueue) {
        return BindingBuilder
            .bind(resourceProcessedQueue)
            .to(resourcesExchange)
            .with(RESOURCE_PROCESSED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
