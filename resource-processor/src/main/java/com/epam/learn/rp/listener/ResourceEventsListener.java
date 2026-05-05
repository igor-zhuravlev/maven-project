package com.epam.learn.rp.listener;

import com.epam.learn.rp.config.RabbitConfig;
import com.epam.learn.rp.event.ResourceUploadedEvent;
import com.epam.learn.rp.service.ResourceMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResourceEventsListener {

    private final ResourceMetadataService resourceMetadataService;

    @RabbitListener(queues = RabbitConfig.RESOURCE_UPLOADED_QUEUE)
    public void onResourceUploaded(final ResourceUploadedEvent event) {
        log.info("Received event: {}", event);
        resourceMetadataService.handle(event);
    }

}
