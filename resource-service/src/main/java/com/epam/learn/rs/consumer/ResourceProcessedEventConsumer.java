package com.epam.learn.rs.consumer;

import com.epam.learn.rs.config.RabbitConfig;
import com.epam.learn.rs.event.ResourceProcessedEvent;
import com.epam.learn.rs.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResourceProcessedEventConsumer {

    private final ResourceService resourceService;

    @RabbitListener(queues = RabbitConfig.RESOURCE_PROCESSED_QUEUE)
    public void consume(final ResourceProcessedEvent event) {
        log.info("Received resource processed event: resourceId={}", event.resourceId());
        resourceService.updateToProcessed(event.resourceId());
        log.info("Resource moved to PERMANENT state: resourceId={}", event.resourceId());
    }

}
