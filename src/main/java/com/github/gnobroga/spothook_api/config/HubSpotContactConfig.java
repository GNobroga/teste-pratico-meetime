package com.github.gnobroga.spothook_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "hubspot.contact", ignoreUnknownFields = true)
@Data
public class HubSpotContactConfig {
    private String apiUrl;
}
