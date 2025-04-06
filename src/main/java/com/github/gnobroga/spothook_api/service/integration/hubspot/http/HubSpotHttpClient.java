package com.github.gnobroga.spothook_api.service.integration.hubspot.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gnobroga.spothook_api.dto.response.ErrorResponseDTO;
import com.github.gnobroga.spothook_api.service.exception.ApiException;
import com.github.gnobroga.spothook_api.service.exception.ApiExceptionType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubSpotHttpClient {

    private static final long REQUEST_TIMEOUT_SECONDS = 10;

    private final ObjectMapper objectMapper;

    private final RestClient restClient;

    private final HubspotRateLimitCache hubspotRateLimitCache;

    private final ScheduledExecutorService scheduledExecutorService;

    public <R> R getWithAccessTokenPathVariable(String url, String accessToken, Class<R> responseType) {
        try {
            return handleRequest(() -> restClient.get()
                            .uri(url + "/" + accessToken)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, getParsedErrorHandler()),
                    responseType
            ).get();
    
        } catch (InterruptedException | ExecutionException ex) {
            throw mapExecutionException(ex);
        }
    }

    public void updateRateLimitCacheFromHttpHeaders(HttpHeaders headers) {
        String max = headers.getFirst(HubspotRateLimitCache.RATE_LIMIT_MAX);
        String remaining = headers.getFirst(HubspotRateLimitCache.RATE_LIMIT_REMAINING);
        String interval = headers.getFirst(HubspotRateLimitCache.RATE_LIMIT_INTERVAL);
        hubspotRateLimitCache.updateFromHeaders(Optional.ofNullable(max), Optional.ofNullable(remaining), Optional.ofNullable(interval));
    }
    
    public <R> R postWithFormData(String url, MultiValueMap<String, String> formData, Class<R> responseType) {
        try {
            return handleRequest(() -> restClient.post()
                            .uri(url)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .body(formData)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, getParsedErrorHandler()),
                    responseType
            ).get();
    
        } catch (InterruptedException | ExecutionException ex) {
            throw mapExecutionException(ex);
        }
    }
    

    public <R> R postWithAccessToken(String url, String accessToken, Object body, Class<R> responseType) {
        try {
            return handleRequest(() -> restClient.post()
                            .uri(url)
                            .headers(headers -> headers.setBearerAuth(accessToken))
                            .body(body)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, getParsedErrorHandler()),
                    responseType
            ).get();
        } catch (InterruptedException | ExecutionException ex) {
            throw mapExecutionException(ex);
        }
    }

    private ApiException mapExecutionException(Exception ex) {
        if (ex instanceof InterruptedException) {
            return new ApiException(ApiExceptionType.REQUEST_INTERRUPTED);
        }
    
        if (ex instanceof ExecutionException executionException) {
            Throwable cause = executionException.getCause();
    
            if (cause instanceof ApiException apiEx) {
                return apiEx;
            }
    
            if (cause instanceof TimeoutException) {
                return new ApiException(ApiExceptionType.REQUEST_TIMEOUT);
            }
        }
    
        return new ApiException(ApiExceptionType.UNEXPECTED_REQUEST_ERROR);
    }
    
    
    private <R> CompletableFuture<R> handleRequest(Supplier<ResponseSpec> supplier, Class<R> responseType) {
        CompletableFuture<R> baseFuture = new CompletableFuture<>();
        CompletableFuture<R> future = baseFuture.orTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        
        try {
            if (hubspotRateLimitCache.shouldWait()) {
                long delay = hubspotRateLimitCache.getTimeInMsToWait();
                log.info("Rate limit threshold exceeded. Delaying request by {}ms...", delay);
    
                scheduledExecutorService.schedule(() -> {
                    try {
                        var delayedSpec = supplier.get();
                        var result = delayedSpec.toEntity(responseType);
                        updateRateLimitCacheFromHttpHeaders(result.getHeaders());
                        future.complete(result.getBody());
                    } catch (ApiException e) {
                        future.completeExceptionally(e);
                    }
                }, delay, TimeUnit.MILLISECONDS);
    
            } else {
                var result = supplier.get().toEntity(responseType);
                updateRateLimitCacheFromHttpHeaders(result.getHeaders());
                future.complete(result.getBody());
            }
    
        } catch (ApiException ex) {
            future.completeExceptionally(ex);
        } catch (RestClientException ex) {
            future.completeExceptionally(new ApiException(ApiExceptionType.UNEXPECTED_REQUEST_ERROR));
        }
    
        return future;
    }
    
    
    private ErrorHandler getParsedErrorHandler() {
        return (request, response) -> {
            parseErrorResponse(response.getBody()).ifPresent(error -> {
                int statusCode = HttpStatus.BAD_REQUEST.value();
                try {
                    statusCode = response.getStatusCode().value();
                } catch (IOException ex) {}

                throw new ApiException(ApiExceptionType.RESPONSE_BODY_ERROR, statusCode, error.getMessage());
            });
        };
    }

    public Optional<ErrorResponseDTO> parseErrorResponse(InputStream inputStream) {
        if (inputStream == null) return Optional.empty();

        try {
            ErrorResponseDTO response = objectMapper.readValue(inputStream, ErrorResponseDTO.class);
            return Optional.ofNullable(response);
        } catch (IOException e) {
            return Optional.empty();
        }
    }


}
