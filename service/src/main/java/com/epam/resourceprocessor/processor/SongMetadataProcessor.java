package com.epam.resourceprocessor.processor;

import com.epam.songservice.dto.SongMetadataDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import static com.epam.resourceprocessor.processor.MetadataProperty.ALBUM;
import static com.epam.resourceprocessor.processor.MetadataProperty.ARTIST;
import static com.epam.resourceprocessor.processor.MetadataProperty.DURATION;
import static com.epam.resourceprocessor.processor.MetadataProperty.RELEASE_YEAR;
import static com.epam.resourceprocessor.processor.MetadataProperty.TITLE;


@Slf4j
@Component
public class SongMetadataProcessor {

    private static final String ZERO = "0";
    private static final String TIME_DELIMITER = ":";

    @SneakyThrows
    public SongMetadataDto processSongMetadata(ByteArrayResource resource) {
        val handler = new BodyContentHandler();
        val metadata = new Metadata();
        val parseContext = new ParseContext();

        val mp3Parser = new Mp3Parser();
        mp3Parser.parse(resource.getInputStream(), handler, metadata, parseContext);

        val stringDuration = metadata.get(DURATION.getName());
        val metadataDto = SongMetadataDto.builder()
                .album(metadata.get(ALBUM.getName()))
                .artist(metadata.get(ARTIST.getName()))
                .duration(parseSongDuration(stringDuration))
                .name(metadata.get(TITLE.getName()))
                .year(Integer.parseInt(metadata.get(RELEASE_YEAR.getName())))
                .resourceId(1L)
                .build();
        log.info(metadataDto.toString());
        return metadataDto;
    }

    private String parseSongDuration(String stringDuration) {
        val durationInSeconds = stringDuration.substring(0, stringDuration.indexOf('.'));
        val intSeconds = Integer.parseInt(durationInSeconds);
        var stringSeconds = String.valueOf(intSeconds % 60);
        if (stringSeconds.length() == 1) {
            stringSeconds = ZERO + stringSeconds;
        }
        var stringMinutes = String.valueOf(intSeconds / 60);
        if (stringMinutes.length() == 1) {
            stringMinutes = ZERO + stringMinutes;
        }
        return stringMinutes + TIME_DELIMITER + stringSeconds;
    }
}
