package com.mercedes.contract.exception;

/**
 * Exception thrown when contract generation fails
 * Follows standard exception conventions
 */
public class ContractGenerationException extends RuntimeException {

    private final String purchaseRequestId;

    public ContractGenerationException(String purchaseRequestId, String message) {
        super(message);
        this.purchaseRequestId = purchaseRequestId;
    }

    public ContractGenerationException(String purchaseRequestId, String message, Throwable cause) {
        super(message, cause);
        this.purchaseRequestId = purchaseRequestId;
    }

    public String getPurchaseRequestId() {
        return purchaseRequestId;
    }
}
