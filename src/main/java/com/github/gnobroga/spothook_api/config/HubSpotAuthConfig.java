package com.github.gnobroga.spothook_api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "hubspot.oauth", ignoreUnknownFields = true)
@Data
public class HubSpotAuthConfig {
    private String authUrl;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scope;
    private String grantType;
    private String tokenUrl;
    private String validateTokenUrl;
    private String optionalScope;
}
