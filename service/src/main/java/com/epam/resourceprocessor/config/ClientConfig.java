package com.epam.resourceprocessor.config;

import com.epam.resourceservice.client.ResourceServiceClient;
import com.epam.resourceservice.client.ResourceServiceClientImpl;
import com.epam.songservice.client.SongServiceClient;
import com.epam.songservice.client.SongServiceClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;


@Configuration
@Import({com.epam.resourceservice.config.ClientConfig.class, com.epam.songservice.config.ClientConfig.class})
public class ClientConfig {

    @Bean
    public ResourceServiceClient resourceServiceClient(RestTemplate resourceServiceRestTemplate) {
        return new ResourceServiceClientImpl(resourceServiceRestTemplate);
    }

    @Bean
    public SongServiceClient songServiceClient(RestTemplate songServiceRestTemplate) {
        return new SongServiceClientImpl(songServiceRestTemplate);
    }
}
