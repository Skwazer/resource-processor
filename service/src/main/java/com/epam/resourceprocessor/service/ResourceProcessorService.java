package com.epam.resourceprocessor.service;

import com.epam.resourceprocessor.processor.SongMetadataProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceProcessorService {

    private final RestTemplate restTemplate;
    private final SongMetadataProcessor songMetadataProcessor;
    private final SongMetadataService songMetadataService;

    @Value("${resource-service.name}")
    private String serviceName;

    public void processResource(int id) {
        ResponseEntity<ByteArrayResource> resourceBytes =
                restTemplate.getForEntity("http://{serviceName}/resources/{id}", ByteArrayResource.class, serviceName, id);
        val metadata = songMetadataProcessor.processSongMetadata(resourceBytes.getBody());
        metadata.setResourceId((long) id);
        val storedMetadata = songMetadataService.storeSongMetadata(metadata);
        log.info("Metadata of " + metadata.getName() + " song saved with id = " + storedMetadata.getId());
    }

    public void deleteResources(List<Integer> ids) {
        songMetadataService.deleteSongMetadataByResourceIds(ids);
        log.info("Metadata for songs with resource ids =  " + ids + " has been deleted successfully");
    }
}
