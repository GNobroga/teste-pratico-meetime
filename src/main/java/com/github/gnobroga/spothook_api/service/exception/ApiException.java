package com.github.gnobroga.spothook_api.service.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    
    private String resolutionHint;
    private int httpStatusCode;

    public ApiException(ApiExceptionType type, Integer httpStatusCode, Object ...args) {
        super(type.getErrorMessage().formatted(args));
        this.resolutionHint = type.getResolutionHint();
        this.httpStatusCode = httpStatusCode != null ? httpStatusCode : type.getHttpStatusCode().value();
    }

    public ApiException(ApiExceptionType type, Object ...args) {
        this(type, null, args);
    }
}
