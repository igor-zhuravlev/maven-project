package com.epam.learn.rs.publisher;

import com.epam.learn.rs.config.RabbitConfig;
import com.epam.learn.rs.event.ResourceUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResourceUploadedEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishResourceUploaded(final Integer resourceId) {
        log.info("Publishing resource uploaded event: resourceId={}", resourceId);
        rabbitTemplate.convertAndSend(
            RabbitConfig.RESOURCES_EXCHANGE,
            RabbitConfig.RESOURCE_UPLOADED_ROUTING_KEY,
            new ResourceUploadedEvent(resourceId)
        );
        log.info("Resource uploaded event published: resourceId={}", resourceId);
    }

}
