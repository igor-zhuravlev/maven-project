package com.epam.learn.ss.mapper;

import com.epam.learn.ss.dto.SongDto;
import com.epam.learn.ss.entity.Song;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SongMapper {

    Song toEntity(SongDto dto);
    SongDto toDto(Song entity);

}
