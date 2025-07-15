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
            String mmss = "%02d:%02d".formatted(totalSeconds / 60, totalSeconds % 60);

            return new MetadataDto(
                id,
                metadata.get("dc:title"),
                metadata.get("xmpDM:artist"),
                mmss,
                metadata.get("xmpDM:album"),
                metadata.get("xmpDM:releaseDate")
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract metadata", e);
        }
    }

}
