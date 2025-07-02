package com.epam.learn.ss.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SongDto {

    @NotNull
    private Integer id;

    @NotBlank(message = "Song name is required")
    private String name;

    @NotBlank
    private String artist;

    @NotBlank
    private String album;

    @Pattern(
        regexp = "^[0-5][0-9]:[0-5][0-9]$",
        message = "Duration must be in mm:ss format with leading zeros"
    )
    private String duration;

    @Pattern(
        regexp = "^(19\\d{2}|20\\d{2})$",
        message = "Year must be between 1900 and 2099"
    )
    private String year;

}
