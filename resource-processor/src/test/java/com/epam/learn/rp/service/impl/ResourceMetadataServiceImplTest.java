package com.epam.learn.rp.service.impl;

import com.epam.learn.rp.client.ResourceServiceClient;
import com.epam.learn.rp.client.SongServiceClient;
import com.epam.learn.rp.dto.MetadataDto;
import com.epam.learn.rp.event.ResourceUploadedEvent;
import com.epam.learn.rp.mapper.ResourceMetadataMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceMetadataServiceImplTest {

    @Mock
    private ResourceServiceClient resourceServiceClient;

    @Mock
    private SongServiceClient songServiceClient;

    @Mock
    private ResourceMetadataMapper resourceMetadataMapper;

    @InjectMocks
    private ResourceMetadataServiceImpl resourceMetadataService;

    @Test
    void shouldDownloadAndSaveSongWhenResourceUploadedEventReceived() {
        final ResourceUploadedEvent event = new ResourceUploadedEvent(1);
        final byte[] data = "data".getBytes(StandardCharsets.UTF_8);
        final MetadataDto metadataDto = new MetadataDto(
            1, "Song", "Artist", "Album", "01:15", "2026");

        when(resourceServiceClient.downloadResource(1)).thenReturn(data);
        when(resourceMetadataMapper.mapToMetadataDto(1, data)).thenReturn(metadataDto);

        resourceMetadataService.handle(event);

        verify(resourceServiceClient, only()).downloadResource(1);
        verify(resourceMetadataMapper, only()).mapToMetadataDto(1, data);
        verify(songServiceClient, only()).createSong(metadataDto);
    }

}