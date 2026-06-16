package com.epam.learn.rp.service.impl;

import com.epam.learn.rp.client.ResourceServiceClient;
import com.epam.learn.rp.client.SongServiceClient;
import com.epam.learn.rp.dto.MetadataDto;
import com.epam.learn.rp.event.ResourceUploadedEvent;
import com.epam.learn.rp.mapper.ResourceMetadataMapper;
import com.epam.learn.rp.publisher.ResourceProcessedEventPublisher;
import com.epam.learn.rp.service.ResourceMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceMetadataServiceImpl implements ResourceMetadataService {

    private final ResourceServiceClient resourceServiceClient;
    private final SongServiceClient songServiceClient;
    private final ResourceMetadataMapper resourceMetadataMapper;
    private final ResourceProcessedEventPublisher resourceProcessedEventPublisher;

    @Override
    public void handle(final ResourceUploadedEvent event) {
        final byte[] audio = resourceServiceClient.downloadResource(event.resourceId());
        final MetadataDto metadataDto = resourceMetadataMapper.mapToMetadataDto(event.resourceId(), audio);
        songServiceClient.createSong(metadataDto);
        resourceProcessedEventPublisher.publish(event.resourceId());
    }

}
