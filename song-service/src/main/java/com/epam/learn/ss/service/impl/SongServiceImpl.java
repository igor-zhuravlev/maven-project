package com.epam.learn.ss.service.impl;

import com.epam.learn.ss.dto.DeleteSongRequestDto;
import com.epam.learn.ss.dto.SongDto;
import com.epam.learn.ss.entity.Song;
import com.epam.learn.ss.exception.DeleteInvalidCsvException;
import com.epam.learn.ss.exception.SongAlreadyExistsException;
import com.epam.learn.ss.exception.SongNotFoundException;
import com.epam.learn.ss.mapper.SongMapper;
import com.epam.learn.ss.repository.SongRepository;
import com.epam.learn.ss.service.SongService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final Validator validator;

    @Transactional
    @Override
    public SongDto save(SongDto dto) {
        if (songRepository.existsById(dto.getId())) {
            throw new SongAlreadyExistsException(dto.getId());
        }
        Song song = songRepository.save(songMapper.toEntity(dto));
        return SongDto.builder()
            .id(song.getId())
            .build();
    }

    @Transactional(readOnly = true)
    @Override
    public SongDto findById(Integer id) {
        return songRepository.findById(id)
            .map(songMapper::toDto)
            .orElseThrow(() -> new SongNotFoundException(id));
    }

    @Transactional
    @Override
    public List<Integer> deleteAllByIds(DeleteSongRequestDto dto) {
        Set<ConstraintViolation<DeleteSongRequestDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new DeleteInvalidCsvException(violations);
        }
        Set<Integer> ids = Arrays.stream(dto.getId().split(","))
            .map(Integer::valueOf)
            .collect(Collectors.toSet());
        List<Integer> existingIds = songRepository.findAllById(ids).stream()
            .map(Song::getId)
            .toList();
        songRepository.deleteAllById(existingIds);
        return existingIds;
    }

}
