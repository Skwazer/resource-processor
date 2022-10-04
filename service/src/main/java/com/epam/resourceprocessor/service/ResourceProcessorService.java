package com.epam.resourceprocessor.service;

import com.epam.resourceprocessor.processor.SongMetadataProcessor;
import com.epam.resourceservice.client.ResourceServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author www.epam.com
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceProcessorService {

    private final ResourceServiceClient resourceServiceClient;
    private final SongMetadataProcessor songMetadataProcessor;
    private final SongMetadataService songMetadataService;

    public void processResource(int id) {
        val resourceBytes = resourceServiceClient.findResourceById(id);
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
