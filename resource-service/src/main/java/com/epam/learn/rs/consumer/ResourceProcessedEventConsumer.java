package com.epam.learn.rs.consumer;

import com.epam.learn.rs.config.RabbitConfig;
import com.epam.learn.rs.event.ResourceProcessedEvent;
import com.epam.learn.rs.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceProcessedEventConsumer {

    private final ResourceService resourceService;

    @RabbitListener(queues = RabbitConfig.RESOURCE_PROCESSED_QUEUE)
    public void consume(final ResourceProcessedEvent event) {
        resourceService.updateToProcessed(event.resourceId());
    }

}
