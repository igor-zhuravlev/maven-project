package com.epam.learn.rs.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetadataDto {

    private Integer id;
    private String name; // dc:title
    private String artist; // xmpDM:artist
    private String album; // xmpDM:album
    private String duration; // xmpDM:duration in mm:ss
    private String year; // xmpDM:releaseDate

}
