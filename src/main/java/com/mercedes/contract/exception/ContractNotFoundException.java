package com.mercedes.contract.exception;

/**
 * Exception thrown when a contract is not found
 * Follows standard exception conventions
 */
public class ContractNotFoundException extends RuntimeException {

    private final String contractId;

    public ContractNotFoundException(String contractId) {
        super("Contract not found with ID: " + contractId);
        this.contractId = contractId;
    }

    public ContractNotFoundException(String contractId, String message) {
        super(message);
        this.contractId = contractId;
    }

    public ContractNotFoundException(String contractId, String message, Throwable cause) {
        super(message, cause);
        this.contractId = contractId;
    }

    public String getContractId() {
        return contractId;
    }
}
