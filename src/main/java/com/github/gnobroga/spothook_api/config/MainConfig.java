package com.github.gnobroga.spothook_api.config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MainConfig {
    
    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    ScheduledExecutorService scheduledExecutorService() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, Thread.ofVirtual().factory());
        return executor;
    }
}
