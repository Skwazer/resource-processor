package com.epam.resourceprocessor.service;

import com.epam.resourceprocessor.exception.SongMetadataServiceException;
import com.epam.songservice.dto.SongMetadataDto;
import com.epam.songservice.dto.SongMetadataIdDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class SongMetadataService {

    private final RestTemplate restTemplate;

    @Value("${song-service.name}")
    private String serviceName;

    @Retryable(value = SongMetadataServiceException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100))
    public SongMetadataIdDto storeSongMetadata(SongMetadataDto songMetadataDto) {
        try {
            log.info("Trying to send request to song-service...");
            val request = new HttpEntity<>(songMetadataDto);
            SongMetadataIdDto songMetadataIdDto =
                    restTemplate.postForEntity("http://{serviceName}/songs", request, SongMetadataIdDto.class, serviceName).getBody();
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
            restTemplate.delete("http://{serviceName}/songs/{resourceId}", serviceName, ids);
        } catch (Exception ex) {
            throw new SongMetadataServiceException("Error while calling song service", ex);
        }
    }
}
