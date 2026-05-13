package com.epam.learn.rs.publisher;

import com.epam.learn.rs.config.RabbitConfig;
import com.epam.learn.rs.event.ResourceUploadedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishResourceUploaded(final Integer resourceId) {
        rabbitTemplate.convertAndSend(
            RabbitConfig.RESOURCES_EXCHANGE,
            RabbitConfig.RESOURCE_UPLOADED_ROUTING_KEY,
            new ResourceUploadedEvent(resourceId)
        );
    }

}
