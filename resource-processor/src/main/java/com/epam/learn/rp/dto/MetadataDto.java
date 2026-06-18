package com.epam.learn.rp.dto;

public record MetadataDto(
    Integer id,
    String name, // dc:title
    String artist, // xmpDM:artist
    String album, // xmpDM:album
    String duration, // xmpDM:duration in mm:ss
    String year // xmpDM:releaseDate
) {
}
