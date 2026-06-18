package com.epam.learn.rp.service.impl;

import com.epam.learn.rp.client.ResourceServiceClient;
import com.epam.learn.rp.client.SongServiceClient;
import com.epam.learn.rp.dto.MetadataDto;
import com.epam.learn.rp.event.ResourceUploadedEvent;
import com.epam.learn.rp.mapper.ResourceMetadataMapper;
import com.epam.learn.rp.publisher.ResourceProcessedEventPublisher;
import com.epam.learn.rp.service.ResourceMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceMetadataServiceImpl implements ResourceMetadataService {

    private final ResourceServiceClient resourceServiceClient;
    private final SongServiceClient songServiceClient;
    private final ResourceMetadataMapper resourceMetadataMapper;
    private final ResourceProcessedEventPublisher resourceProcessedEventPublisher;

    @Override
    public void handle(final ResourceUploadedEvent event) {
        log.info("Started processing uploaded resource :: resourceId={}", event.resourceId());
        final byte[] audio = resourceServiceClient.downloadResource(event.resourceId());
        final MetadataDto metadataDto = resourceMetadataMapper.mapToMetadataDto(event.resourceId(), audio);
        songServiceClient.createSong(metadataDto);
        log.info("Created song metadata :: resourceId={}", event.resourceId());
        resourceProcessedEventPublisher.publish(event.resourceId());
        log.info("Published resource processed event :: resourceId={}", event.resourceId());
    }

}
