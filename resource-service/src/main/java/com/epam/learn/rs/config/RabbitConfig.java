package com.epam.learn.rs.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String RESOURCES_EXCHANGE = "resources.exchange";
    public static final String RESOURCE_UPLOADED_ROUTING_KEY = "resources.resource.uploaded";

    @Bean
    public TopicExchange resourcesExchange() {
        return new TopicExchange(RESOURCES_EXCHANGE);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
