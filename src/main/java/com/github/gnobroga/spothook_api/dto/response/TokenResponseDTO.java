package com.github.gnobroga.spothook_api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponseDTO(
    @JsonProperty("refresh_token")
    String refreshToken,
    
    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("expires_in")
    Long expiresIn
) {}
