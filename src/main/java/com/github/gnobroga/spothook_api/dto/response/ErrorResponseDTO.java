package com.github.gnobroga.spothook_api.dto.response;

import lombok.Data;

@Data
public class ErrorResponseDTO {
    private String status;
    private String message;
    private String correlationId;
}
