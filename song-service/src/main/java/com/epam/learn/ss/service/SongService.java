package com.epam.learn.ss.service;

import com.epam.learn.ss.dto.DeleteSongRequestDto;
import com.epam.learn.ss.dto.SongDto;

import java.util.List;

public interface SongService {
    SongDto save(SongDto dto);
    SongDto findById(Integer id);
    List<Integer> deleteAllByIds(DeleteSongRequestDto dto);
}
