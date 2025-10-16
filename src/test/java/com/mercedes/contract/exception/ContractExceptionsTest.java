package com.mercedes.contract.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for all custom exception classes
 * Tests exception creation, inheritance, and message handling
 */
class ContractExceptionsTest {

    @Test
    void testContractNotFoundExceptionWithContractId() {
        String contractId = "CONTRACT-123";
        ContractNotFoundException exception = new ContractNotFoundException(contractId);

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(contractId));
        assertEquals(contractId, exception.getContractId());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testContractNotFoundExceptionWithContractIdAndMessage() {
        String contractId = "CONTRACT-123";
        String message = "Custom not found message";
        ContractNotFoundException exception = new ContractNotFoundException(contractId, message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(contractId, exception.getContractId());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testContractNotFoundExceptionWithContractIdMessageAndCause() {
        String contractId = "CONTRACT-123";
        String message = "Contract not found";
        Throwable cause = new IllegalArgumentException("Invalid contract ID");
        ContractNotFoundException exception = new ContractNotFoundException(contractId, message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(contractId, exception.getContractId());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testContractGenerationExceptionWithPurchaseRequestIdAndMessage() {
        String purchaseRequestId = "PR-456";
        String message = "Failed to generate contract";
        ContractGenerationException exception = new ContractGenerationException(purchaseRequestId, message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(purchaseRequestId, exception.getPurchaseRequestId());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testContractGenerationExceptionWithPurchaseRequestIdMessageAndCause() {
        String purchaseRequestId = "PR-456";
        String message = "Failed to generate contract";
        Throwable cause = new IllegalStateException("Invalid state");
        ContractGenerationException exception = new ContractGenerationException(purchaseRequestId, message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(purchaseRequestId, exception.getPurchaseRequestId());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testPdfGenerationExceptionWithContractIdAndMessage() {
        String contractId = "CONTRACT-123";
        String message = "PDF generation failed";
        PdfGenerationException exception = new PdfGenerationException(contractId, message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(contractId, exception.getContractId());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testPdfGenerationExceptionWithContractIdMessageAndCause() {
        String contractId = "CONTRACT-123";
        String message = "PDF generation failed";
        Throwable cause = new RuntimeException("Template not found");
        PdfGenerationException exception = new PdfGenerationException(contractId, message, cause);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(contractId, exception.getContractId());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testExceptionHierarchy() {
        ContractNotFoundException notFound = new ContractNotFoundException("CONTRACT-123");
        ContractGenerationException generation = new ContractGenerationException("PR-456", "Generation failed");
        PdfGenerationException pdfGeneration = new PdfGenerationException("CONTRACT-123", "PDF failed");

        assertTrue(notFound instanceof RuntimeException);
        assertTrue(generation instanceof RuntimeException);
        assertTrue(pdfGeneration instanceof RuntimeException);

        assertTrue(notFound instanceof Exception);
        assertTrue(generation instanceof Exception);
        assertTrue(pdfGeneration instanceof Exception);
    }

    @Test
    void testNullSafety() {
        // Test that exceptions handle null values gracefully
        assertDoesNotThrow(() -> new ContractNotFoundException(null));
        assertDoesNotThrow(() -> new ContractNotFoundException(null, "message"));
        assertDoesNotThrow(() -> new ContractNotFoundException(null, "message", null));

        assertDoesNotThrow(() -> new ContractGenerationException(null, "message"));
        assertDoesNotThrow(() -> new ContractGenerationException(null, "message", null));

        assertDoesNotThrow(() -> new PdfGenerationException(null, "message"));
        assertDoesNotThrow(() -> new PdfGenerationException(null, "message", null));
    }
}
