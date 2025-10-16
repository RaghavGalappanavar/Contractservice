package com.mercedes.contract.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for contract details response
 * Used for GET /contracts/{contractId} endpoint
 */
public class ContractDetailsResponse {

    @NotBlank
    private String contractId;

    @NotBlank
    private String purchaseRequestId;

    @NotBlank
    private String dealId;

    @NotNull
    private Map<String, Object> customerDetails;

    @NotNull
    private Map<String, Object> financeDetails;

    @NotNull
    private List<Map<String, Object>> massOrders;

    private String pdfStorageLocation;

    @NotNull
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Default constructor
    public ContractDetailsResponse() {
    }

    // Constructor with all fields
    public ContractDetailsResponse(String contractId, String purchaseRequestId, String dealId,
                                  Map<String, Object> customerDetails, Map<String, Object> financeDetails,
                                  List<Map<String, Object>> massOrders, String pdfStorageLocation,
                                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.contractId = contractId;
        this.purchaseRequestId = purchaseRequestId;
        this.dealId = dealId;
        this.customerDetails = customerDetails;
        this.financeDetails = financeDetails;
        this.massOrders = massOrders;
        this.pdfStorageLocation = pdfStorageLocation;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Explicit getters and setters
    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getPurchaseRequestId() {
        return purchaseRequestId;
    }

    public void setPurchaseRequestId(String purchaseRequestId) {
        this.purchaseRequestId = purchaseRequestId;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public Map<String, Object> getCustomerDetails() {
        return customerDetails;
    }

    public void setCustomerDetails(Map<String, Object> customerDetails) {
        this.customerDetails = customerDetails;
    }

    public Map<String, Object> getFinanceDetails() {
        return financeDetails;
    }

    public void setFinanceDetails(Map<String, Object> financeDetails) {
        this.financeDetails = financeDetails;
    }

    public List<Map<String, Object>> getMassOrders() {
        return massOrders;
    }

    public void setMassOrders(List<Map<String, Object>> massOrders) {
        this.massOrders = massOrders;
    }

    public String getPdfStorageLocation() {
        return pdfStorageLocation;
    }

    public void setPdfStorageLocation(String pdfStorageLocation) {
        this.pdfStorageLocation = pdfStorageLocation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
