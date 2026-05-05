package com.epam.learn.rp.config;

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
    public static final String RESOURCE_UPLOADED_QUEUE = "resources.resource-uploaded.queue";
    public static final String RESOURCE_UPLOADED_ROUTING_KEY = "resources.resource.uploaded";

    @Bean
    public TopicExchange resourcesExchange() {
        return new TopicExchange(RESOURCES_EXCHANGE);
    }

    @Bean
    public Queue resourceUploadedQueue() {
        return QueueBuilder.durable(RESOURCE_UPLOADED_QUEUE).build();
    }

    @Bean
    public Binding resourceUploadedBinding(Queue resourceUploadedQueue, TopicExchange resourcesExchange) {
        return BindingBuilder
            .bind(resourceUploadedQueue)
            .to(resourcesExchange)
            .with(RESOURCE_UPLOADED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
