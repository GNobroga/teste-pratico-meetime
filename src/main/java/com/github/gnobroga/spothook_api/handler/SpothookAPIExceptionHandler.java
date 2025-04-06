package com.github.gnobroga.spothook_api.handler;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.github.gnobroga.spothook_api.dto.response.ApiResponse;
import com.github.gnobroga.spothook_api.dto.response.ApiResponse.ApiResponseStatus;
import com.github.gnobroga.spothook_api.service.exception.ApiException;

@RestControllerAdvice
public class SpothookAPIExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String UNEXPECTED_ERROR =
        "An unexpected error occurred. Please contact support.";
    
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        return ResponseEntity
            .status(ex.getHttpStatusCode())
            .body(new ApiResponse<Void>(ApiResponseStatus.ERROR, ex.getMessage(), ex.getResolutionHint(), HttpStatus.valueOf(ex.getHttpStatusCode())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        return ResponseEntity
            .internalServerError()
            .body(new ApiResponse<Void>(ApiResponseStatus.ERROR, UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errorMessages = ex.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList();
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(ApiResponseStatus.INVALID.getValue());
        apiResponse.setMessages(errorMessages);
        apiResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(apiResponse);
    }

}
