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
import com.epam.learn.rs.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceS3Service resourceS3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceUploadedEventPublisher resourceUploadedEventPublisher;
    private final StorageServiceClient storageServiceClient;
    private final SongServiceClient songServiceClient;

    @Transactional
    @Override
    public ResourceResponseDto save(byte[] data) {
        StorageDto staging = storageServiceClient.getStorage(StorageType.STAGING);
        String key = resourceS3Service.upload(data, staging.bucket(), staging.path());
        Resource resource = new Resource(null, StorageType.STAGING, staging.bucket(), key);
        Resource saved = resourceRepository.save(resource);
        resourceUploadedEventPublisher.publishResourceUploaded(saved.getId());
        return new ResourceResponseDto(saved.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] findById(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidResourceIdException(id);
        }
        Resource resource = resourceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));
        return resourceS3Service.download(resource.getBucket(), resource.getS3Key());
    }

    @Transactional
    @Override
    public List<Integer> deleteAllByIds(DeleteResourceRequestDto dto) {
        Set<Integer> ids = Arrays.stream(dto.id().split(","))
            .map(Integer::valueOf)
            .collect(Collectors.toSet());
        List<Resource> existingResources = resourceRepository.findAllById(ids);

        songServiceClient.deleteSongsByIds(dto.id());
        resourceS3Service.delete(existingResources);

        List<Integer> existingIds = existingResources.stream()
            .map(Resource::getId)
            .toList();
        resourceRepository.deleteAllById(existingIds);

        return existingIds;
    }

    @Transactional
    @Override
    public void updateToProcessed(Integer resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
            .orElseThrow(() -> new ResourceNotFoundException(resourceId));

        if (resource.getStorageType() == StorageType.PERMANENT) {
            return;
        }

        StorageDto permanent = storageServiceClient.getStorage(StorageType.PERMANENT);
        String key = resourceS3Service.move(resource, permanent.bucket(), permanent.path());

        resource.setStorageType(StorageType.PERMANENT);
        resource.setBucket(permanent.bucket());
        resource.setS3Key(key);

        resourceRepository.save(resource);
    }

}
