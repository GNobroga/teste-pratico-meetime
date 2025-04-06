package com.github.gnobroga.spothook_api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private String status; 
    private String message;
    private List<String> messages;
    private String resolutionHint;
    private int statusCode;
    private T data;

    public ApiResponse(ApiResponseStatus Status, String message, String resolutionHint, HttpStatus statusCode, T data) {
        this.status = Status.getValue(); 
        this.message = message;
        this.statusCode = statusCode.value();
        this.data = data;
        this.resolutionHint = resolutionHint;
    }

    public ApiResponse(ApiResponseStatus Status, String message,  String resolutionHint, HttpStatus statusCode) {
        this(Status, message, resolutionHint, statusCode, null);
    }

    public ApiResponse(ApiResponseStatus Status, String message,HttpStatus statusCode) {
        this(Status, message, null, statusCode, null);
    }

    public ApiResponse(ApiResponseStatus Status, T data) {
        this(Status, null, null, HttpStatus.OK, data);
    }

    public ApiResponse(T data) {
        this(ApiResponseStatus.SUCCESS, null, null, HttpStatus.OK, data);
    }

    @RequiredArgsConstructor
    @Getter
    public enum ApiResponseStatus {
        SUCCESS("success"),
        INVALID("invalid"),
        ERROR("error");
        private final String value;
    }
}
