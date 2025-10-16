package com.mercedes.contract.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Standard error response DTO
 * Follows common guidelines error format: errorCode, message, timestamp, traceId
 */
public class ErrorResponse {

    @NotBlank
    private String errorCode;

    @NotBlank
    private String message;

    @NotNull
    private LocalDateTime timestamp;

    private String traceId;

    // Default constructor
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor with required fields
    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor with all fields
    public ErrorResponse(String errorCode, String message, String traceId) {
        this.errorCode = errorCode;
        this.message = message;
        this.traceId = traceId;
        this.timestamp = LocalDateTime.now();
    }

    // Explicit getters and setters
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
