package com.epam.learn.ss.service;

import com.epam.learn.ss.dto.CreateStorageRequestDto;
import com.epam.learn.ss.dto.CreateStorageResponseDto;
import com.epam.learn.ss.dto.StorageResponseDto;

import java.util.List;

public interface StorageService {
    CreateStorageResponseDto create(CreateStorageRequestDto dto);
    List<StorageResponseDto> findAll();
    List<Integer> deleteAllByIds(String ids);
}
