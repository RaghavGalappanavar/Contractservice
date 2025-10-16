package com.mercedes.contract.exception;

import com.mercedes.contract.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

/**
 * Global exception handler for centralized error handling
 * Follows @ControllerAdvice pattern as per common guidelines
 * Masks sensitive details and includes traceId automatically
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ContractNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleContractNotFoundException(ContractNotFoundException ex) {
        String traceId = MDC.get("traceId");
        
        logger.error("Contract not found - contractId: {}", maskSensitiveData(ex.getContractId()), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "CONTRACT_NOT_FOUND",
            "Contract not found",
            traceId
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ContractGenerationException.class)
    public ResponseEntity<ErrorResponse> handleContractGenerationException(ContractGenerationException ex) {
        String traceId = MDC.get("traceId");
        
        logger.error("Contract generation failed - purchaseRequestId: {}", 
                    maskSensitiveData(ex.getPurchaseRequestId()), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "CONTRACT_GENERATION_FAILED",
            "Failed to generate contract",
            traceId
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<ErrorResponse> handlePdfGenerationException(PdfGenerationException ex) {
        String traceId = MDC.get("traceId");
        
        logger.error("PDF generation failed - contractId: {}", 
                    maskSensitiveData(ex.getContractId()), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "PDF_GENERATION_FAILED",
            "Failed to generate PDF document",
            traceId
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String traceId = MDC.get("traceId");
        
        String validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        
        logger.error("Validation failed: {}", validationErrors, ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_FAILED",
            "Invalid request: " + validationErrors,
            traceId
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        String traceId = MDC.get("traceId");
        
        logger.error("Unexpected error occurred", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred",
            traceId
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Mask sensitive data for logging
     * Follows data privacy guidelines
     */
    private String maskSensitiveData(String data) {
        if (data == null || data.length() <= 4) {
            return "****";
        }
        return data.substring(0, 4) + "****";
    }
}
