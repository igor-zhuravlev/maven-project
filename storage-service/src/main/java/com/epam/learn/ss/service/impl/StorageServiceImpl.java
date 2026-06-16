package com.epam.learn.ss.service.impl;

import com.epam.learn.ss.dto.CreateStorageRequestDto;
import com.epam.learn.ss.dto.CreateStorageResponseDto;
import com.epam.learn.ss.dto.StorageResponseDto;
import com.epam.learn.ss.entity.Storage;
import com.epam.learn.ss.exception.StorageAlreadyExistsException;
import com.epam.learn.ss.exception.StorageLocationAlreadyExistsException;
import com.epam.learn.ss.repository.StorageRepository;
import com.epam.learn.ss.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;

    @Transactional
    @Override
    public CreateStorageResponseDto create(final CreateStorageRequestDto dto) {
        if (storageRepository.existsByStorageType(dto.storageType())) {
            throw new StorageAlreadyExistsException(dto.storageType().name());
        }
        if (storageRepository.existsByBucketAndPath(dto.bucket(), dto.path())) {
            throw new StorageLocationAlreadyExistsException(dto.bucket(), dto.path());
        }

        final Storage storage = new Storage(
            null,
            dto.storageType(),
            dto.bucket(),
            dto.path()
        );

        final Storage saved = storageRepository.save(storage);
        return new CreateStorageResponseDto(saved.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<StorageResponseDto> findAll() {
        return storageRepository.findAll().stream()
            .map(storage -> new StorageResponseDto(
                storage.getId(),
                storage.getStorageType(),
                storage.getBucket(),
                storage.getPath()
            ))
            .toList();
    }

    @Transactional
    @Override
    public List<Integer> deleteAllByIds(final String ids) {
        final Set<Integer> idSet = Arrays.stream(ids.split(","))
            .map(Integer::valueOf)
            .collect(Collectors.toSet());

        final List<Storage> existing = storageRepository.findAllById(idSet);

        final List<Integer> deletedIds = existing.stream()
            .map(Storage::getId)
            .toList();

        storageRepository.deleteAll(existing);

        return deletedIds;
    }

}
