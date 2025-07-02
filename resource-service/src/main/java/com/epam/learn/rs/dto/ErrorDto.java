package com.epam.learn.rs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDto {

    private String errorMessage;
    private String errorCode;

}
