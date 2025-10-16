package com.mercedes.contract.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Contract entity representing the contract data model
 * Follows JPA conventions with explicit getters/setters
 */
@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @Column(name = "contract_id", length = 50)
    @NotBlank
    @Size(max = 50)
    private String contractId;

    @Column(name = "purchase_request_id", length = 100, nullable = false)
    @NotBlank
    @Size(max = 100)
    private String purchaseRequestId;

    @Column(name = "deal_id", length = 100, nullable = false)
    @NotBlank
    @Size(max = 100)
    private String dealId;

    @Column(name = "customer_details")
    @JdbcTypeCode(SqlTypes.JSON)
    @NotNull
    private Map<String, Object> customerDetails;

    @Column(name = "finance_details")
    @JdbcTypeCode(SqlTypes.JSON)
    @NotNull
    private Map<String, Object> financeDetails;

    @Column(name = "mass_orders")
    @JdbcTypeCode(SqlTypes.JSON)
    @NotNull
    private List<Map<String, Object>> massOrders;

    @Column(name = "pdf_storage_location", length = 500)
    @Size(max = 500)
    private String pdfStorageLocation;

    @Column(name = "created_at", nullable = false)
    @NotNull
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public Contract() {
    }

    // Constructor with required fields
    public Contract(String contractId, String purchaseRequestId, String dealId,
                   Map<String, Object> customerDetails, Map<String, Object> financeDetails,
                   List<Map<String, Object>> massOrders) {
        this.contractId = contractId;
        this.purchaseRequestId = purchaseRequestId;
        this.dealId = dealId;
        this.customerDetails = customerDetails;
        this.financeDetails = financeDetails;
        this.massOrders = massOrders;
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Explicit getters and setters (no Lombok as per guidelines)
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
