package com.mercedes.contract.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * Unit tests for ErrorResponse DTO
 * Tests error handling response structure and data integrity
 */
class ErrorResponseTest {

    private ErrorResponse errorResponse;

    @BeforeEach
    void setUp() {
        errorResponse = new ErrorResponse();
    }

    @Test
    void testDefaultConstructor() {
        ErrorResponse response = new ErrorResponse();
        assertNotNull(response);
        assertNull(response.getErrorCode());
        assertNull(response.getMessage());
        assertNotNull(response.getTimestamp()); // Timestamp is automatically set in constructor
        assertNull(response.getTraceId());
    }

    @Test
    void testSettersAndGetters() {
        String errorCode = "CONTRACT_NOT_FOUND";
        String message = "Contract not found";
        LocalDateTime timestamp = LocalDateTime.now();
        String traceId = "550e8400-e29b-41d4-a716-446655440000";

        errorResponse.setErrorCode(errorCode);
        errorResponse.setMessage(message);
        errorResponse.setTimestamp(timestamp);
        errorResponse.setTraceId(traceId);

        assertEquals(errorCode, errorResponse.getErrorCode());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(traceId, errorResponse.getTraceId());
    }

    @Test
    void testStandardErrorCodes() {
        String[] standardErrorCodes = {
            "CONTRACT_NOT_FOUND",
            "CONTRACT_GENERATION_FAILED",
            "PDF_GENERATION_FAILED",
            "VALIDATION_FAILED",
            "INTERNAL_SERVER_ERROR",
            "DUPLICATE_CONTRACT",
            "INVALID_REQUEST_DATA"
        };

        for (String errorCode : standardErrorCodes) {
            errorResponse.setErrorCode(errorCode);
            assertEquals(errorCode, errorResponse.getErrorCode());
        }
    }

    @Test
    void testErrorMessages() {
        String[] errorMessages = {
            "Contract not found",
            "Failed to generate contract",
            "PDF generation failed",
            "Request validation failed",
            "Internal server error occurred",
            "Contract already exists for this purchase request",
            "Invalid request data provided"
        };

        for (String message : errorMessages) {
            errorResponse.setMessage(message);
            assertEquals(message, errorResponse.getMessage());
        }
    }

    @Test
    void testTimestampHandling() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minusHours(1);
        LocalDateTime future = now.plusHours(1);

        // Test current timestamp
        errorResponse.setTimestamp(now);
        assertEquals(now, errorResponse.getTimestamp());

        // Test past timestamp
        errorResponse.setTimestamp(past);
        assertEquals(past, errorResponse.getTimestamp());
        assertTrue(errorResponse.getTimestamp().isBefore(now));

        // Test future timestamp
        errorResponse.setTimestamp(future);
        assertEquals(future, errorResponse.getTimestamp());
        assertTrue(errorResponse.getTimestamp().isAfter(now));
    }

    @Test
    void testTraceIdFormats() {
        String[] validTraceIds = {
            "550e8400-e29b-41d4-a716-446655440000",
            "123e4567-e89b-12d3-a456-426614174000",
            "trace-id-12345",
            "simple-trace-id",
            "TRACE123456789",
            "trace_id_with_underscores"
        };

        for (String traceId : validTraceIds) {
            errorResponse.setTraceId(traceId);
            assertEquals(traceId, errorResponse.getTraceId());
        }
    }

    @Test
    void testCompleteErrorResponse() {
        String errorCode = "CONTRACT_GENERATION_FAILED";
        String message = "Failed to generate contract due to invalid customer data";
        LocalDateTime timestamp = LocalDateTime.of(2025, 10, 16, 12, 30, 45);
        String traceId = "550e8400-e29b-41d4-a716-446655440000";

        errorResponse.setErrorCode(errorCode);
        errorResponse.setMessage(message);
        errorResponse.setTimestamp(timestamp);
        errorResponse.setTraceId(traceId);

        assertEquals(errorCode, errorResponse.getErrorCode());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(traceId, errorResponse.getTraceId());

        // Verify data consistency
        assertTrue(errorResponse.getErrorCode().contains("FAILED"));
        assertTrue(errorResponse.getMessage().contains("Failed"));
        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTraceId().contains("-"));
    }

    @Test
    void testNullSafety() {
        errorResponse.setErrorCode(null);
        errorResponse.setMessage(null);
        errorResponse.setTimestamp(null);
        errorResponse.setTraceId(null);

        assertNull(errorResponse.getErrorCode());
        assertNull(errorResponse.getMessage());
        assertNull(errorResponse.getTimestamp());
        assertNull(errorResponse.getTraceId());
    }

    @Test
    void testEmptyStrings() {
        errorResponse.setErrorCode("");
        errorResponse.setMessage("");
        errorResponse.setTraceId("");

        assertEquals("", errorResponse.getErrorCode());
        assertEquals("", errorResponse.getMessage());
        assertEquals("", errorResponse.getTraceId());
    }

    @Test
    void testLongErrorMessages() {
        String longMessage = "This is a very long error message that contains detailed information about what went wrong during the contract generation process. It includes multiple sentences and provides comprehensive details about the error condition that occurred in the system.";
        
        errorResponse.setMessage(longMessage);
        assertEquals(longMessage, errorResponse.getMessage());
        assertTrue(errorResponse.getMessage().length() > 100);
    }

    @Test
    void testSpecialCharactersInMessage() {
        String messageWithSpecialChars = "Error: Contract generation failed! @#$%^&*()_+-={}[]|\\:;\"'<>?,./";
        
        errorResponse.setMessage(messageWithSpecialChars);
        assertEquals(messageWithSpecialChars, errorResponse.getMessage());
        assertTrue(errorResponse.getMessage().contains("@#$%"));
    }

    @Test
    void testTimestampPrecision() {
        LocalDateTime preciseTime = LocalDateTime.of(2025, 10, 16, 12, 30, 45, 123456789);
        
        errorResponse.setTimestamp(preciseTime);
        assertEquals(preciseTime, errorResponse.getTimestamp());
        assertEquals(123456789, errorResponse.getTimestamp().getNano());
    }

    @Test
    void testErrorCodeCaseSensitivity() {
        errorResponse.setErrorCode("contract_not_found");
        assertEquals("contract_not_found", errorResponse.getErrorCode());
        
        errorResponse.setErrorCode("CONTRACT_NOT_FOUND");
        assertEquals("CONTRACT_NOT_FOUND", errorResponse.getErrorCode());
        
        errorResponse.setErrorCode("Contract_Not_Found");
        assertEquals("Contract_Not_Found", errorResponse.getErrorCode());
    }

    @Test
    void testFieldIndependence() {
        // Test that setting one field doesn't affect others
        errorResponse.setErrorCode("TEST_ERROR");
        assertNull(errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp()); // Timestamp is automatically set in constructor
        assertNull(errorResponse.getTraceId());

        errorResponse.setMessage("Test message");
        assertEquals("TEST_ERROR", errorResponse.getErrorCode());
        assertNotNull(errorResponse.getTimestamp()); // Timestamp is automatically set in constructor
        assertNull(errorResponse.getTraceId());

        LocalDateTime now = LocalDateTime.now();
        errorResponse.setTimestamp(now);
        assertEquals("TEST_ERROR", errorResponse.getErrorCode());
        assertEquals("Test message", errorResponse.getMessage());
        assertNull(errorResponse.getTraceId());

        errorResponse.setTraceId("test-trace-id");
        assertEquals("TEST_ERROR", errorResponse.getErrorCode());
        assertEquals("Test message", errorResponse.getMessage());
        assertEquals(now, errorResponse.getTimestamp());
        assertEquals("test-trace-id", errorResponse.getTraceId());
    }

    @Test
    void testValidationErrorResponse() {
        errorResponse.setErrorCode("VALIDATION_FAILED");
        errorResponse.setMessage("Request validation failed: purchaseRequestId is required");
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setTraceId("validation-trace-123");

        assertEquals("VALIDATION_FAILED", errorResponse.getErrorCode());
        assertTrue(errorResponse.getMessage().contains("validation"));
        assertTrue(errorResponse.getMessage().contains("required"));
        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTraceId().startsWith("validation"));
    }

    @Test
    void testInternalServerErrorResponse() {
        errorResponse.setErrorCode("INTERNAL_SERVER_ERROR");
        errorResponse.setMessage("An unexpected error occurred");
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setTraceId("internal-error-456");

        assertEquals("INTERNAL_SERVER_ERROR", errorResponse.getErrorCode());
        assertEquals("An unexpected error occurred", errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTraceId().contains("internal"));
    }

    @Test
    void testNotFoundErrorResponse() {
        errorResponse.setErrorCode("CONTRACT_NOT_FOUND");
        errorResponse.setMessage("Contract with ID CONTRACT-ABC12345 not found");
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setTraceId("not-found-789");

        assertEquals("CONTRACT_NOT_FOUND", errorResponse.getErrorCode());
        assertTrue(errorResponse.getMessage().contains("CONTRACT-ABC12345"));
        assertTrue(errorResponse.getMessage().contains("not found"));
        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTraceId().contains("not-found"));
    }
}
