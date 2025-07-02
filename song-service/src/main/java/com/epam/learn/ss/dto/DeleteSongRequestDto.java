package com.epam.learn.ss.dto;

import com.epam.learn.ss.validation.DeleteResource;
import lombok.Data;

@Data
public class DeleteSongRequestDto {

    @DeleteResource
    private String id;

}
