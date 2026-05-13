package com.epam.learn.rs.service.impl;

import com.epam.learn.rs.client.SongServiceClient;
import com.epam.learn.rs.dto.DeleteResourceRequestDto;
import com.epam.learn.rs.dto.ResourceResponseDto;
import com.epam.learn.rs.entity.Resource;
import com.epam.learn.rs.exception.InvalidResourceIdException;
import com.epam.learn.rs.exception.ResourceNotFoundException;
import com.epam.learn.rs.publisher.ResourceEventPublisher;
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

    @Mock
    private ResourceS3Service resourceS3Service;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private ResourceEventPublisher resourceEventPublisher;

    @Mock
    private SongServiceClient songServiceClient;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    void shouldSaveResourceWhenFileExists() {
        final String key = "resources/1.mp3";
        final byte[] data = "data".getBytes(StandardCharsets.UTF_8);
        final Resource savedResource = new Resource(1, key);

        when(resourceS3Service.upload(data)).thenReturn(key);
        when(resourceRepository.save(any(Resource.class))).thenReturn(savedResource);

        ResourceResponseDto actual = resourceService.save(data);

        assertEquals(1, actual.id());

        verify(resourceS3Service, only()).upload(data);
        verify(resourceRepository, only()).save(argThat(resource ->
            resource.getId() == null && resource.getS3Key().equals(key)
        ));
        verify(resourceEventPublisher, only()).publishResourceUploaded(1);
    }

    @Test
    void shouldFindResourceWhenIdIsValid() {
        final String key = "resources/1.mp3";
        final byte[] data = "data".getBytes(StandardCharsets.UTF_8);
        final Resource savedResource = new Resource(1, key);

        when(resourceRepository.findById(1)).thenReturn(Optional.of(savedResource));
        when(resourceS3Service.download(key)).thenReturn(data);

        byte[] actual = resourceService.findById(1);

        assertArrayEquals(data, actual);

        verify(resourceRepository, only()).findById(1);
        verify(resourceS3Service, only()).download(key);
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
            new Resource(1, "resources/1.mp3"),
            new Resource(2, "resources/2.mp3")
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