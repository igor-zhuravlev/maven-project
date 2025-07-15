package com.epam.learn.rs.dto;

public record MetadataDto(
    Integer id,
    String name, // dc:title
    String artist, // xmpDM:artist
    String duration, // xmpDM:duration in mm:ss
    String album, // xmpDM:album
    String year // xmpDM:releaseDate
) {
}
