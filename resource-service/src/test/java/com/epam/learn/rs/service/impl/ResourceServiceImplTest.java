package com.epam.learn.rs.service.impl;

import com.epam.learn.rs.client.SongServiceClient;
import com.epam.learn.rs.client.StorageServiceClient;
import com.epam.learn.rs.dto.DeleteResourceRequestDto;
import com.epam.learn.rs.dto.ResourceResponseDto;
import com.epam.learn.rs.dto.StorageDto;
import com.epam.learn.rs.entity.Resource;
import com.epam.learn.rs.entity.StorageType;
import com.epam.learn.rs.exception.InvalidResourceIdException;
import com.epam.learn.rs.exception.ResourceNotFoundException;
import com.epam.learn.rs.publisher.ResourceUploadedEventPublisher;
import com.epam.learn.rs.repository.ResourceRepository;
import com.epam.learn.rs.service.ResourceS3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    private static final String STAGING_BUCKET = "resources-staging";
    private static final String PERMANENT_BUCKET = "resources-permanent";
    private static final String PATH = "/files";

    @Mock
    private ResourceS3Service resourceS3Service;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceUploadedEventPublisher resourceUploadedEventPublisher;

    @Mock
    private StorageServiceClient storageServiceClient;

    @Mock
    private SongServiceClient songServiceClient;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    void shouldSaveResourceWhenFileExists() {
        final String key = PATH + "/1.mp3";
        final byte[] data = "data".getBytes(StandardCharsets.UTF_8);
        final StorageDto stagingStorage = new StorageDto(
            1,
            StorageType.STAGING,
            STAGING_BUCKET,
            PATH
        );
        final Resource savedResource = new Resource(
            1,
            StorageType.STAGING,
            STAGING_BUCKET,
            key
        );

        when(storageServiceClient.getStorage(StorageType.STAGING)).thenReturn(stagingStorage);
        when(resourceS3Service.upload(data, STAGING_BUCKET, PATH)).thenReturn(key);
        when(resourceRepository.save(any(Resource.class))).thenReturn(savedResource);

        ResourceResponseDto actual = resourceService.save(data);

        assertEquals(1, actual.id());

        verify(storageServiceClient, only()).getStorage(StorageType.STAGING);
        verify(resourceS3Service, only()).upload(data, STAGING_BUCKET, PATH);
        verify(resourceRepository, only()).save(argThat(resource ->
            resource.getId() == null
                && resource.getStorageType() == StorageType.STAGING
                && resource.getBucket().equals(STAGING_BUCKET)
                && resource.getS3Key().equals(key)
        ));
        verify(resourceUploadedEventPublisher, only()).publishResourceUploaded(1);
    }

    @Test
    void shouldFindResourceWhenIdIsValid() {
        final String key = PATH + "/1.mp3";
        final byte[] data = "data".getBytes(StandardCharsets.UTF_8);
        final Resource savedResource = new Resource(
            1,
            StorageType.PERMANENT,
            PERMANENT_BUCKET,
            key
        );

        when(resourceRepository.findById(1)).thenReturn(Optional.of(savedResource));
        when(resourceS3Service.download(PERMANENT_BUCKET, key)).thenReturn(data);

        byte[] actual = resourceService.findById(1);

        assertArrayEquals(data, actual);

        verify(resourceRepository, only()).findById(1);
        verify(resourceS3Service, only()).download(PERMANENT_BUCKET, key);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = {0, -1})
    void shouldThrowInvalidResourceIdExceptionWhenIdIsInvalid(final Integer id) {
        assertThrows(InvalidResourceIdException.class, () -> resourceService.findById(id));

        verifyNoInteractions(resourceRepository);
        verifyNoInteractions(resourceS3Service);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenResourceDoesNotExist() {
        when(resourceRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resourceService.findById(1));

        verify(resourceRepository, only()).findById(1);
        verifyNoInteractions(resourceS3Service);
    }

    @Test
    void shouldDeleteResourcesAndRelatedSongsWhenResourcesExist() {
        final DeleteResourceRequestDto dto = new DeleteResourceRequestDto("1,2");
        final List<Resource> existingResources = List.of(
            new Resource(1, StorageType.PERMANENT, PERMANENT_BUCKET, PATH + "/1.mp3"),
            new Resource(2, StorageType.PERMANENT, PERMANENT_BUCKET, PATH + "/2.mp3")
        );

        when(resourceRepository.findAllById(Set.of(1, 2))).thenReturn(existingResources);

        List<Integer> actual = resourceService.deleteAllByIds(dto);

        assertEquals(List.of(1, 2), actual);

        verify(resourceRepository, times(1)).findAllById(Set.of(1, 2));
        verify(songServiceClient, only()).deleteSongsByIds("1,2");
        verify(resourceS3Service, only()).delete(existingResources);
        verify(resourceRepository, times(1)).deleteAllById(List.of(1, 2));
    }

}