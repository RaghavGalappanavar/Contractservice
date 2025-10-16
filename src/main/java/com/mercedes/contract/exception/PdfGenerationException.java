package com.mercedes.contract.exception;

/**
 * Exception thrown when PDF generation fails
 * Follows standard exception conventions
 */
public class PdfGenerationException extends RuntimeException {

    private final String contractId;

    public PdfGenerationException(String contractId, String message) {
        super(message);
        this.contractId = contractId;
    }

    public PdfGenerationException(String contractId, String message, Throwable cause) {
        super(message, cause);
        this.contractId = contractId;
    }

    public String getContractId() {
        return contractId;
    }
}
