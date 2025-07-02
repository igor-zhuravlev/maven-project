package com.epam.learn.rs.dto;

import com.epam.learn.rs.validation.DeleteResource;
import lombok.Data;

@Data
public class DeleteResourceRequestDto {

    @DeleteResource
    private String id;

}
