package com.epam.resourceprocessor.service;

import com.epam.resourceprocessor.exception.SongMetadataServiceException;
import com.epam.resourceprocessor.producer.RMQPublisher;
import com.epam.songservice.client.SongServiceClient;
import com.epam.songservice.dto.SongMetadataDto;
import com.epam.songservice.dto.SongMetadataIdDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class SongMetadataService {

    private final SongServiceClient songServiceClient;

    @Retryable(value = SongMetadataServiceException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    public SongMetadataIdDto storeSongMetadata(SongMetadataDto songMetadataDto) {
        try {
            log.info("Trying to send request to song-service...");
            SongMetadataIdDto songMetadataIdDto = songServiceClient.storeSongMetadata(songMetadataDto);
            log.info("Success");
            return songMetadataIdDto;
        } catch (Exception ex) {
            log.warn("Song-service is not available...");
            throw new SongMetadataServiceException("Error while calling song service", ex);
        }
    }

    @Retryable(value = {SongMetadataServiceException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void deleteSongMetadataByResourceIds(List<Integer> ids) {
        try {
            songServiceClient.removeMetadataByResourceIds(ids);
        } catch (Exception ex) {
            throw new SongMetadataServiceException("Error while calling song service", ex);
        }
    }
}
