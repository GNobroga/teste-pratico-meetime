package com.github.gnobroga.spothook_api.service.integration.hubspot.impl;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.gnobroga.spothook_api.config.HubSpotAuthConfig;
import com.github.gnobroga.spothook_api.dto.response.AuthUrlResponseDTO;
import com.github.gnobroga.spothook_api.dto.response.TokenResponseDTO;
import com.github.gnobroga.spothook_api.service.OAuthService;
import com.github.gnobroga.spothook_api.service.integration.hubspot.http.HubSpotHttpClient;
import com.github.gnobroga.spothook_api.util.UrlUtils;
import com.github.gnobroga.spothook_api.util.ValueUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubSpotOAuthServiceImpl implements OAuthService {
    
    private final HubSpotAuthConfig hubSpotAuthConfig;
    private final HubSpotHttpClient hubSpotHttpClient;

    private static final String CLIENT_ID_KEY = "client_id";
    private static final String REDIRECT_URL_KEY = "redirect_uri";
    private static final String CLIENT_SECRET_KEY = "client_secret";
    private static final String CODE_KEY = "code";
    private static final String SCOPE_KEY = "scope";
    private static final String OPTIONAL_SCOPE_KEY = "optional_scope";
    private static final String GRANT_TYPE_KEY = "grant_type";


    @Override
    public AuthUrlResponseDTO generateAuthUrl() {
        log.info("Generating HubSpot OAuth authorization URL...");

        Map<String, String> params = Map.ofEntries(
            Map.entry(CLIENT_ID_KEY, ValueUtils.defaultIfNull(hubSpotAuthConfig.getClientId(), "")),
            Map.entry(REDIRECT_URL_KEY, ValueUtils.defaultIfNull(hubSpotAuthConfig.getRedirectUri(), "")),
            Map.entry(SCOPE_KEY,  ValueUtils.defaultIfNull(hubSpotAuthConfig.getScope(), "")),
            Map.entry(OPTIONAL_SCOPE_KEY,  ValueUtils.defaultIfNull(hubSpotAuthConfig.getOptionalScope(), ""))
        );

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(hubSpotAuthConfig.getAuthUrl());

        params.forEach((key, param) -> {
            if (StringUtils.hasText(param)) {
                uriBuilder.queryParam(key, UrlUtils.encode(param));
            }
        });

        String finalUrl = uriBuilder.build(true).toUriString();

        log.info("HubSpot auth URL generated");

        return new AuthUrlResponseDTO(finalUrl);
    }

    @Override 
    public TokenResponseDTO exchangeAuthorizationCode(String code) {
        log.info("Exchanging authorization code for access token");

        var formData = buildTokenExchangeParams(code);

        TokenResponseDTO response = hubSpotHttpClient.postWithFormData(
            hubSpotAuthConfig.getTokenUrl(),
            formData,
            TokenResponseDTO.class
        );

        log.info("Authorization code exchange successful");

        return response;
    }
    
    private MultiValueMap<String, String> buildTokenExchangeParams(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(GRANT_TYPE_KEY, hubSpotAuthConfig.getGrantType());
        formData.add(CLIENT_ID_KEY, hubSpotAuthConfig.getClientId());
        formData.add(CLIENT_SECRET_KEY, hubSpotAuthConfig.getClientSecret());
        formData.add(REDIRECT_URL_KEY, hubSpotAuthConfig.getRedirectUri());
        formData.add(CODE_KEY, code);
        return formData;
    }

    @Override
    public boolean validateAccessToken(String token) {
        log.info("Validating HubSpot access token");

        try {
            hubSpotHttpClient.getWithAccessTokenPathVariable(
                hubSpotAuthConfig.getValidateTokenUrl(),
                token,
                JsonNode.class
            );
            log.info("Access token is valid");
            return true;
        } catch (Exception e) {
            log.warn("Access token validation failed: {}", e.getMessage());
            return false;
        }
    }

}

