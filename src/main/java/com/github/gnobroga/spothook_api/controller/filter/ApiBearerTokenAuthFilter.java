package com.github.gnobroga.spothook_api.controller.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gnobroga.spothook_api.dto.response.ApiResponse;
import com.github.gnobroga.spothook_api.dto.response.ApiResponse.ApiResponseStatus;
import com.github.gnobroga.spothook_api.service.OAuthService;
import com.github.gnobroga.spothook_api.service.exception.ApiException;
import com.github.gnobroga.spothook_api.service.exception.ApiExceptionType;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApiBearerTokenAuthFilter extends OncePerRequestFilter {

    private final OAuthService oAuthService;

    private final ObjectMapper objectMapper;

    private static final String REQUEST_ACCESS_TOKEN_ATTRIBUTE = "accessToken";
    private static final String UNAUTHORIZED_ACCESS = "Unauthorized access";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            String accessToken = extractTokenFromHeader(authorizationHeader)
                .orElseThrow(() -> new ApiException(ApiExceptionType.ACCESS_TOKEN_MISSING));
        
            if (oAuthService.validateAccessToken(accessToken)) {
                request.setAttribute(REQUEST_ACCESS_TOKEN_ATTRIBUTE, accessToken);
                filterChain.doFilter(request, response);
            } else {
                writeErrorResponse(HttpStatus.UNAUTHORIZED.value(), UNAUTHORIZED_ACCESS, response);
            }
        } catch (ApiException ex) {
            writeErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), response);
        }
        
    }

    private void writeErrorResponse(int statusCode, String errorMessage, HttpServletResponse response) throws IOException {
        ApiResponse<Void> apiResponse = new ApiResponse<>(ApiResponseStatus.ERROR, errorMessage, HttpStatus.valueOf(statusCode));
        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    private Optional<String> extractTokenFromHeader(String value) {
        final String BEARER_PREFIX = "Bearer ";
        return Optional.ofNullable(value)
            .filter(header -> header.startsWith(BEARER_PREFIX))
            .map(header -> header.substring(BEARER_PREFIX.length()));
    }
    
    
}
