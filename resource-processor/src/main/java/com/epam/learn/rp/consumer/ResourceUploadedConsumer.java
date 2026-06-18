package com.epam.learn.rp.consumer;

import com.epam.learn.rp.event.ResourceUploadedEvent;
import com.epam.learn.rp.service.ResourceMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResourceUploadedConsumer implements Consumer<ResourceUploadedEvent> {

    private final ResourceMetadataService resourceMetadataService;

    @Override
    public void accept(final ResourceUploadedEvent event) {
        log.info("Received event: {}", event);
        resourceMetadataService.handle(event);
    }

}
