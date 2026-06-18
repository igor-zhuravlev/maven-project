package com.epam.learn.rp.publisher;

import com.epam.learn.rp.event.ResourceProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResourceProcessedEventPublisher {

    private static final String BINDING_NAME = "resourceProcessedEventProducer-out-0";

    private final StreamBridge streamBridge;

    public void publish(final Integer resourceId) {
        log.info("Publishing resource processed event: resourceId={}", resourceId);
        boolean sent = streamBridge.send(BINDING_NAME, new ResourceProcessedEvent(resourceId));
        if (sent) {
            log.info("Resource processed event published: resourceId={}", resourceId);
        } else {
            log.warn("Resource processed event was not published: resourceId={}", resourceId);
        }
    }

}
