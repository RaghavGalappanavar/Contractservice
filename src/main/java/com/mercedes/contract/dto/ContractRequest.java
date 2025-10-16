package com.mercedes.contract.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * DTO for contract creation request
 * Follows validation conventions with explicit getters/setters
 */
public class ContractRequest {

    @NotBlank(message = "Purchase request ID is required")
    @Size(max = 100, message = "Purchase request ID must not exceed 100 characters")
    private String purchaseRequestId;

    @NotBlank(message = "Deal ID is required")
    @Size(max = 100, message = "Deal ID must not exceed 100 characters")
    private String dealId;

    @NotNull(message = "Deal data is required")
    @Valid
    private DealData dealData;

    // Default constructor
    public ContractRequest() {
    }

    // Constructor with required fields
    public ContractRequest(String purchaseRequestId, String dealId, DealData dealData) {
        this.purchaseRequestId = purchaseRequestId;
        this.dealId = dealId;
        this.dealData = dealData;
    }

    // Explicit getters and setters
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

    public DealData getDealData() {
        return dealData;
    }

    public void setDealData(DealData dealData) {
        this.dealData = dealData;
    }

    /**
     * Nested DealData class representing the deal information
     */
    public static class DealData {

        @NotBlank(message = "Deal ID in deal data is required")
        private String dealId;

        @NotNull(message = "Customer details are required")
        private Map<String, Object> customer;

        @NotNull(message = "Customer finance details are required")
        private Map<String, Object> customerFinanceDetails;

        @NotNull(message = "Retailer info is required")
        private Map<String, Object> retailerInfo;

        @NotNull(message = "Mass orders are required")
        private Map<String, Object> massOrders;

        // Default constructor
        public DealData() {
        }

        // Constructor with required fields
        public DealData(String dealId, Map<String, Object> customer,
                       Map<String, Object> customerFinanceDetails,
                       Map<String, Object> retailerInfo,
                       Map<String, Object> massOrders) {
            this.dealId = dealId;
            this.customer = customer;
            this.customerFinanceDetails = customerFinanceDetails;
            this.retailerInfo = retailerInfo;
            this.massOrders = massOrders;
        }

        // Explicit getters and setters
        public String getDealId() {
            return dealId;
        }

        public void setDealId(String dealId) {
            this.dealId = dealId;
        }

        public Map<String, Object> getCustomer() {
            return customer;
        }

        public void setCustomer(Map<String, Object> customer) {
            this.customer = customer;
        }

        public Map<String, Object> getCustomerFinanceDetails() {
            return customerFinanceDetails;
        }

        public void setCustomerFinanceDetails(Map<String, Object> customerFinanceDetails) {
            this.customerFinanceDetails = customerFinanceDetails;
        }

        public Map<String, Object> getRetailerInfo() {
            return retailerInfo;
        }

        public void setRetailerInfo(Map<String, Object> retailerInfo) {
            this.retailerInfo = retailerInfo;
        }

        public Map<String, Object> getMassOrders() {
            return massOrders;
        }

        public void setMassOrders(Map<String, Object> massOrders) {
            this.massOrders = massOrders;
        }
    }
}
