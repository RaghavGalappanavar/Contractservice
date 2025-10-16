package com.mercedes.contract.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO for contract creation response
 * Follows the business requirements response format
 */
public class ContractResponse {

    @NotBlank
    private String contractId;

    @NotBlank
    private String contractUrl;

    @NotBlank
    private String contractStatus;

    @NotNull
    private LocalDateTime signedAt;

    // Default constructor
    public ContractResponse() {
    }

    // Constructor with required fields
    public ContractResponse(String contractId, String contractUrl, String contractStatus, LocalDateTime signedAt) {
        this.contractId = contractId;
        this.contractUrl = contractUrl;
        this.contractStatus = contractStatus;
        this.signedAt = signedAt;
    }

    // Explicit getters and setters
    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractUrl() {
        return contractUrl;
    }

    public void setContractUrl(String contractUrl) {
        this.contractUrl = contractUrl;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public LocalDateTime getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(LocalDateTime signedAt) {
        this.signedAt = signedAt;
    }
}
