package com.github.gnobroga.spothook_api.service.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ApiExceptionType {
    
    MISSING_ACCESS_TOKEN(
        "Access token is required",
        "Include a valid access token in the Authorization header",
        HttpStatus.UNAUTHORIZED
    ),

    MISSING_REQUIRED_FIELD_TEMPLATE(
        "Required field(s) missing: %s",
        "Check the request body and ensure the following fields are filled: %s",
        HttpStatus.BAD_REQUEST
    ),

    EXTERNAL_API_ERROR(
        "Failed to process response from external API. HTTP status: %s",
        "Verify the external API endpoint, its availability, and check the response body for details.",
        HttpStatus.BAD_GATEWAY
    ),

    RESPONSE_BODY_ERROR(
    "Received error from external API: %s",
    "Check the external API error message and verify your request parameters",
        HttpStatus.BAD_REQUEST
    ),

    REQUEST_INTERRUPTED(
    "The request thread was interrupted",
        "Ensure no thread interruption is occurring during async operations",
        HttpStatus.INTERNAL_SERVER_ERROR
    ),

    REQUEST_TIMEOUT(
        "The request did not complete within the configured timeout of %s seconds",
        "Ensure the operation can complete within the timeout window or increase the timeout threshold if necessary",
        HttpStatus.GATEWAY_TIMEOUT
    ),

    UNEXPECTED_REQUEST_ERROR(
        "An unexpected error occurred during request execution",
        "Check the exception cause and stack trace for more information",
        HttpStatus.INTERNAL_SERVER_ERROR
    ),

    SIGNATURE_HEADER_MISSING(
    "Signature '%s' header is missing",
    "Ensure header is included in the webhook request",
    HttpStatus.UNAUTHORIZED
    ),

    INVALID_SIGNATURE(
    "Invalid signature '%s' in the request",
    "Ensure header matches the expected hash value",
    HttpStatus.UNAUTHORIZED
    ),


    ACCESS_TOKEN_MISSING(
        "Access token is missing", 
        "Ensure the 'Authorization' header is set with a valid Bearer token",
        HttpStatus.UNAUTHORIZED
    );

 
    private final String errorMessage;
    private final String resolutionHint;
    private final HttpStatus httpStatusCode;
}
