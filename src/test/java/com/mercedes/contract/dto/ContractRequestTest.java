package com.mercedes.contract.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.Arrays;

/**
 * Unit tests for ContractRequest DTO
 * Tests validation, getters, setters, and nested DealData functionality
 */
@DisplayName("ContractRequest DTO Tests")
class ContractRequestTest {

    private Validator validator;
    private ContractRequest contractRequest;
    private ContractRequest.DealData dealData;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        contractRequest = new ContractRequest();
        dealData = new ContractRequest.DealData();
    }

    @Test
    @DisplayName("Should create ContractRequest with default constructor")
    void shouldCreateContractRequestWithDefaultConstructor() {
        ContractRequest request = new ContractRequest();
        assertNotNull(request);
        assertNull(request.getPurchaseRequestId());
        assertNull(request.getDealId());
        assertNull(request.getDealData());
    }

    @Test
    @DisplayName("Should create ContractRequest with parameterized constructor")
    void shouldCreateContractRequestWithParameterizedConstructor() {
        String purchaseRequestId = "PR-12345";
        String dealId = "DEAL-67890";
        ContractRequest.DealData dealData = createSampleDealData();

        ContractRequest request = new ContractRequest(purchaseRequestId, dealId, dealData);

        assertNotNull(request);
        assertEquals(purchaseRequestId, request.getPurchaseRequestId());
        assertEquals(dealId, request.getDealId());
        assertEquals(dealData, request.getDealData());
    }

    @Test
    @DisplayName("Should set and get purchase request ID")
    void shouldSetAndGetPurchaseRequestId() {
        String purchaseRequestId = "PR-12345";

        contractRequest.setPurchaseRequestId(purchaseRequestId);

        assertEquals(purchaseRequestId, contractRequest.getPurchaseRequestId());
    }

    @Test
    @DisplayName("Should set and get deal ID")
    void shouldSetAndGetDealId() {
        String dealId = "DEAL-67890";

        contractRequest.setDealId(dealId);

        assertEquals(dealId, contractRequest.getDealId());
    }

    @Test
    @DisplayName("Should set and get deal data")
    void shouldSetAndGetDealData() {
        ContractRequest.DealData dealData = createSampleDealData();

        contractRequest.setDealData(dealData);

        assertEquals(dealData, contractRequest.getDealData());
    }

    @Test
    @DisplayName("Should validate successfully with valid data")
    void shouldValidateSuccessfullyWithValidData() {
        ContractRequest request = createValidContractRequest();

        Set<ConstraintViolation<ContractRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Should fail validation when purchase request ID is null")
    void shouldFailValidationWhenPurchaseRequestIdIsNull() {
        ContractRequest request = createValidContractRequest();
        request.setPurchaseRequestId(null);

        Set<ConstraintViolation<ContractRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Purchase request ID is required")));
    }

    @Test
    @DisplayName("Should fail validation when purchase request ID is blank")
    void shouldFailValidationWhenPurchaseRequestIdIsBlank() {
        ContractRequest request = createValidContractRequest();
        request.setPurchaseRequestId("");

        Set<ConstraintViolation<ContractRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Purchase request ID is required")));
    }

    @Test
    @DisplayName("Should fail validation when purchase request ID exceeds max length")
    void shouldFailValidationWhenPurchaseRequestIdExceedsMaxLength() {
        ContractRequest request = createValidContractRequest();
        request.setPurchaseRequestId("A".repeat(101)); // Exceeds 100 character limit

        Set<ConstraintViolation<ContractRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Purchase request ID must not exceed 100 characters")));
    }

    @Test
    @DisplayName("Should fail validation when deal ID is null")
    void shouldFailValidationWhenDealIdIsNull() {
        ContractRequest request = createValidContractRequest();
        request.setDealId(null);

        Set<ConstraintViolation<ContractRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Deal ID is required")));
    }

    @Test
    @DisplayName("Should fail validation when deal ID is blank")
    void shouldFailValidationWhenDealIdIsBlank() {
        ContractRequest request = createValidContractRequest();
        request.setDealId("");

        Set<ConstraintViolation<ContractRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Deal ID is required")));
    }

    @Test
    @DisplayName("Should fail validation when deal ID exceeds max length")
    void shouldFailValidationWhenDealIdExceedsMaxLength() {
        ContractRequest request = createValidContractRequest();
        request.setDealId("A".repeat(101)); // Exceeds 100 character limit

        Set<ConstraintViolation<ContractRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Deal ID must not exceed 100 characters")));
    }

    @Test
    @DisplayName("Should fail validation when deal data is null")
    void shouldFailValidationWhenDealDataIsNull() {
        ContractRequest request = createValidContractRequest();
        request.setDealData(null);

        Set<ConstraintViolation<ContractRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Deal data is required")));
    }

    // DealData Tests
    @Test
    @DisplayName("Should create DealData with default constructor")
    void shouldCreateDealDataWithDefaultConstructor() {
        ContractRequest.DealData data = new ContractRequest.DealData();
        assertNotNull(data);
        assertNull(data.getDealId());
        assertNull(data.getCustomer());
        assertNull(data.getCustomerFinanceDetails());
        assertNull(data.getRetailerInfo());
        assertNull(data.getMassOrders());
    }

    @Test
    @DisplayName("Should create DealData with parameterized constructor")
    void shouldCreateDealDataWithParameterizedConstructor() {
        String dealId = "DEAL-67890";
        Map<String, Object> customer = createSampleCustomer();
        Map<String, Object> customerFinanceDetails = createSampleCustomerFinanceDetails();
        Map<String, Object> retailerInfo = createSampleRetailerInfo();
        List<Map<String, Object>> massOrders = createSampleMassOrders();

        ContractRequest.DealData data = new ContractRequest.DealData(dealId, customer,
                                                                     customerFinanceDetails,
                                                                     retailerInfo, massOrders);

        assertNotNull(data);
        assertEquals(dealId, data.getDealId());
        assertEquals(customer, data.getCustomer());
        assertEquals(customerFinanceDetails, data.getCustomerFinanceDetails());
        assertEquals(retailerInfo, data.getRetailerInfo());
        assertEquals(massOrders, data.getMassOrders());
    }

    @Test
    @DisplayName("Should set and get DealData properties")
    void shouldSetAndGetDealDataProperties() {
        String dealId = "DEAL-67890";
        Map<String, Object> customer = createSampleCustomer();
        Map<String, Object> customerFinanceDetails = createSampleCustomerFinanceDetails();
        Map<String, Object> retailerInfo = createSampleRetailerInfo();
        List<Map<String, Object>> massOrders = createSampleMassOrders();

        dealData.setDealId(dealId);
        dealData.setCustomer(customer);
        dealData.setCustomerFinanceDetails(customerFinanceDetails);
        dealData.setRetailerInfo(retailerInfo);
        dealData.setMassOrders(massOrders);

        assertEquals(dealId, dealData.getDealId());
        assertEquals(customer, dealData.getCustomer());
        assertEquals(customerFinanceDetails, dealData.getCustomerFinanceDetails());
        assertEquals(retailerInfo, dealData.getRetailerInfo());
        assertEquals(massOrders, dealData.getMassOrders());
    }

    @Test
    @DisplayName("Should validate DealData with nested validation")
    void shouldValidateDealDataWithNestedValidation() {
        ContractRequest request = createValidContractRequest();

        // Test nested validation by setting invalid deal data
        request.getDealData().setDealId(null);

        Set<ConstraintViolation<ContractRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Deal ID in deal data is required")));
    }

    // Helper methods for creating test data
    private ContractRequest createValidContractRequest() {
        return new ContractRequest(
                "PR-12345",
                "DEAL-67890",
                createSampleDealData()
        );
    }

    private ContractRequest.DealData createSampleDealData() {
        return new ContractRequest.DealData(
                "DEAL-67890",
                createSampleCustomer(),
                createSampleCustomerFinanceDetails(),
                createSampleRetailerInfo(),
                createSampleMassOrders()
        );
    }

    private Map<String, Object> createSampleCustomer() {
        Map<String, Object> customer = new HashMap<>();
        customer.put("customerId", "CUST-001");
        customer.put("firstName", "John");
        customer.put("lastName", "Doe");
        customer.put("email", "john.doe@example.com");
        customer.put("phone", "+1-555-123-4567");
        return customer;
    }

    private Map<String, Object> createSampleCustomerFinanceDetails() {
        Map<String, Object> financeDetails = new HashMap<>();
        financeDetails.put("financeType", "LOAN");
        financeDetails.put("loanAmount", 50000.0);
        financeDetails.put("downPayment", 10000.0);
        financeDetails.put("interestRate", 3.5);
        financeDetails.put("termMonths", 60);
        return financeDetails;
    }

    private Map<String, Object> createSampleRetailerInfo() {
        Map<String, Object> retailerInfo = new HashMap<>();
        retailerInfo.put("dealerCode", "MB-DEALER-001");
        retailerInfo.put("dealerName", "Mercedes-Benz Downtown");
        retailerInfo.put("salesPersonId", "SP-001");
        retailerInfo.put("salesPersonName", "Jane Smith");
        return retailerInfo;
    }

    private List<Map<String, Object>> createSampleMassOrders() {
        Map<String, Object> massOrder = new HashMap<>();
        massOrder.put("vehicleModel", "C-Class");
        massOrder.put("vehicleVin", "VIN123456789");
        massOrder.put("quantity", 1);
        massOrder.put("unitPrice", 60000.0);
        massOrder.put("totalAmount", 60000.0);
        return Arrays.asList(massOrder);
    }
}
