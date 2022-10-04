package com.epam.resourceprocessor.service;

import com.epam.resourceprocessor.exception.SongMetadataServiceException;
import com.epam.songservice.client.SongServiceClient;
import com.epam.songservice.dto.SongMetadataDto;
import com.epam.songservice.dto.SongMetadataIdDto;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author www.epam.com
 */
@Service
@RequiredArgsConstructor
public class SongMetadataService {

    private final SongServiceClient songServiceClient;

    @Retryable(value = {SongMetadataServiceException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public SongMetadataIdDto storeSongMetadata(SongMetadataDto songMetadataDto) {
        try {
            return songServiceClient.storeSongMetadata(songMetadataDto);
        } catch (Exception ex) {
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
