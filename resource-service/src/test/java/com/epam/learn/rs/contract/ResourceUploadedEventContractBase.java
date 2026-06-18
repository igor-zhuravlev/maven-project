package com.epam.learn.rs.contract;

import com.epam.learn.rs.config.RabbitConfig;
import com.epam.learn.rs.event.ResourceUploadedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierSender;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;

@SpringBootTest(
    classes = {
        ResourceUploadedEventContractBase.Config.class
    },
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@AutoConfigureMessageVerifier
public abstract class ResourceUploadedEventContractBase {

    @Autowired
    private MessageVerifierSender<Message<?>> messageVerifierSender;

    public void publishResourceUploaded() {
        Message<ResourceUploadedEvent> message = MessageBuilder
            .withPayload(new ResourceUploadedEvent(1))
            .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
            .build();

        messageVerifierSender.send(message, RabbitConfig.RESOURCES_EXCHANGE);
    }

    @Configuration
    static class Config {

        @Bean(name = RabbitConfig.RESOURCES_EXCHANGE)
        PollableChannel resourcesExchange() {
            return new QueueChannel();
        }

    }

}
