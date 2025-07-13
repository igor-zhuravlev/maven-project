package com.epam.learn.rs.service.impl;

import com.epam.learn.rs.dto.DeleteResourceRequestDto;
import com.epam.learn.rs.dto.MetadataDto;
import com.epam.learn.rs.dto.ResourceResponseDto;
import com.epam.learn.rs.entity.Resource;
import com.epam.learn.rs.exception.InvalidResourceIdException;
import com.epam.learn.rs.exception.ResourceNotFoundException;
import com.epam.learn.rs.exception.SongServiceException;
import com.epam.learn.rs.mapper.ResourceMetadataMapper;
import com.epam.learn.rs.repository.ResourceRepository;
import com.epam.learn.rs.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMetadataMapper resourceMetadataMapper;
    private final RestClient restClient;

    @Transactional
    @Override
    public ResourceResponseDto save(byte[] data) {
        Resource resource = new Resource(null, data);
        Resource saved = resourceRepository.save(resource);

        MetadataDto metadataDto = resourceMetadataMapper.mapToMetadataDto(saved.getId(), data);
        ResponseEntity<String> response = restClient.post()
            .uri("/song-service/songs")
            .body(metadataDto)
            .retrieve()
            .toEntity(String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new SongServiceException(response.getBody());
        }

        return new ResourceResponseDto(saved.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] findById(Integer id) {
        if (id == null || id <= 0) {
            throw new InvalidResourceIdException(id);
        }
        return resourceRepository.findById(id)
            .map(Resource::getData)
            .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Transactional
    @Override
    public List<Integer> deleteAllByIds(DeleteResourceRequestDto dto) {
        Set<Integer> ids = Arrays.stream(dto.getId().split(","))
            .map(Integer::valueOf)
            .collect(Collectors.toSet());
        List<Integer> existingIds = resourceRepository.findAllById(ids).stream()
            .map(Resource::getId)
            .toList();

        ResponseEntity<String> response = restClient.delete()
            .uri(uriBuilder -> uriBuilder
                .path("/song-service/songs")
                .queryParam("id", dto.getId())
                .build())
            .retrieve()
            .toEntity(String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new SongServiceException(response.getBody());
        }

        resourceRepository.deleteAllById(existingIds);
        return existingIds;
    }

}
