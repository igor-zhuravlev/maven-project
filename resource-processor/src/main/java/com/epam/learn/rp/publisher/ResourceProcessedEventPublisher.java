package com.epam.learn.rp.publisher;

import com.epam.learn.rp.event.ResourceProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceProcessedEventPublisher {

    private static final String BINDING_NAME = "resourceProcessedEventProducer-out-0";

    private final StreamBridge streamBridge;

    public void publish(final Integer resourceId) {
        streamBridge.send(BINDING_NAME, new ResourceProcessedEvent(resourceId));
    }

}
