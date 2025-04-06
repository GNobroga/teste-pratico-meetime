package com.github.gnobroga.spothook_api.service;

import com.github.gnobroga.spothook_api.dto.response.AuthUrlResponseDTO;
import com.github.gnobroga.spothook_api.dto.response.TokenResponseDTO;

public interface OAuthService {
    AuthUrlResponseDTO generateAuthUrl();
    TokenResponseDTO exchangeAuthorizationCode(String code);
    boolean validateAccessToken(String token);
}
