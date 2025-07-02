package com.epam.learn.rs.service;

import com.epam.learn.rs.dto.DeleteResourceRequestDto;
import com.epam.learn.rs.dto.ResourceResponseDto;

import java.util.List;

public interface ResourceService {
    ResourceResponseDto save(byte[] data);
    byte[] findById(Integer id);
    List<Integer> deleteAllByIds(DeleteResourceRequestDto dto);
}
