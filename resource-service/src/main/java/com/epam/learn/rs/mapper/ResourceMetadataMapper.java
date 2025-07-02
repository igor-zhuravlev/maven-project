package com.epam.learn.rs.mapper;

import com.epam.learn.rs.dto.MetadataDto;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.springframework.stereotype.Component;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
public class ResourceMetadataMapper {

    public MetadataDto mapToMetadataDto(Integer id, byte[] data) {
        try (InputStream bis = new ByteArrayInputStream(data)) {
            Metadata metadata = new Metadata();
            new Mp3Parser().parse(bis, new DefaultHandler(), metadata, new ParseContext());

            String duration = metadata.get("xmpDM:duration");
            double durationInSec = Double.parseDouble(duration);
            long totalSeconds = Math.round(durationInSec);
            String mmss = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);

            return MetadataDto.builder()
                .id(id)
                .name(metadata.get("dc:title"))
                .artist(metadata.get("xmpDM:artist"))
                .duration(mmss)
                .album(metadata.get("xmpDM:album"))
                .year(metadata.get("xmpDM:releaseDate"))
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract metadata", e);
        }
    }

}
