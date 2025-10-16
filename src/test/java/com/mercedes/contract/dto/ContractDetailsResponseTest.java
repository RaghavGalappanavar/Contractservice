package com.mercedes.contract.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

/**
 * Unit tests for ContractDetailsResponse DTO
 * Tests all fields including JSONB data and timestamps
 */
class ContractDetailsResponseTest {

    private ContractDetailsResponse response;

    @BeforeEach
    void setUp() {
        response = new ContractDetailsResponse();
    }

    @Test
    void testDefaultConstructor() {
        ContractDetailsResponse detailsResponse = new ContractDetailsResponse();
        assertNotNull(detailsResponse);
        assertNull(detailsResponse.getContractId());
        assertNull(detailsResponse.getPurchaseRequestId());
        assertNull(detailsResponse.getDealId());
        assertNull(detailsResponse.getCustomerDetails());
        assertNull(detailsResponse.getFinanceDetails());
        assertNull(detailsResponse.getMassOrders());
        assertNull(detailsResponse.getPdfStorageLocation());
        assertNull(detailsResponse.getCreatedAt());
        assertNull(detailsResponse.getUpdatedAt());
    }

    @Test
    void testBasicFieldsSettersAndGetters() {
        String contractId = "CONTRACT-ABC12345";
        String purchaseRequestId = "PR-67890";
        String dealId = "DEAL-54321";
        String pdfLocation = "/contracts/CONTRACT-ABC12345.pdf";
        LocalDateTime now = LocalDateTime.now();

        response.setContractId(contractId);
        response.setPurchaseRequestId(purchaseRequestId);
        response.setDealId(dealId);
        response.setPdfStorageLocation(pdfLocation);
        response.setCreatedAt(now);
        response.setUpdatedAt(now);

        assertEquals(contractId, response.getContractId());
        assertEquals(purchaseRequestId, response.getPurchaseRequestId());
        assertEquals(dealId, response.getDealId());
        assertEquals(pdfLocation, response.getPdfStorageLocation());
        assertEquals(now, response.getCreatedAt());
        assertEquals(now, response.getUpdatedAt());
    }

    @Test
    void testJsonbFieldsSettersAndGetters() {
        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("firstName", "John");
        customerDetails.put("lastName", "Doe");
        customerDetails.put("email", "john.doe@example.com");
        customerDetails.put("phone", "+1-555-123-4567");

        Map<String, Object> financeDetails = new HashMap<>();
        financeDetails.put("loanAmount", 50000.00);
        financeDetails.put("downPayment", 10000.00);
        financeDetails.put("interestRate", 3.5);
        financeDetails.put("loanTermMonths", 60);

        Map<String, Object> massOrder = new HashMap<>();
        massOrder.put("quantity", 1);
        massOrder.put("unitPrice", 60000.00);
        massOrder.put("totalAmount", 60000.00);
        List<Map<String, Object>> massOrders = Arrays.asList(massOrder);

        response.setCustomerDetails(customerDetails);
        response.setFinanceDetails(financeDetails);
        response.setMassOrders(massOrders);

        assertEquals(customerDetails, response.getCustomerDetails());
        assertEquals(financeDetails, response.getFinanceDetails());
        assertEquals(massOrders, response.getMassOrders());

        // Test specific values
        assertEquals("John", response.getCustomerDetails().get("firstName"));
        assertEquals(50000.00, response.getFinanceDetails().get("loanAmount"));
        assertEquals(1, response.getMassOrders().get(0).get("quantity"));
    }

    @Test
    void testCompleteContractDetailsResponse() {
        // Setup complete response
        String contractId = "CONTRACT-FULL12345";
        String purchaseRequestId = "PR-FULL67890";
        String dealId = "DEAL-FULL54321";
        String pdfLocation = "/contracts/CONTRACT-FULL12345.pdf";
        LocalDateTime createdAt = LocalDateTime.of(2025, 10, 16, 10, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2025, 10, 16, 12, 30, 0);

        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("firstName", "Jane");
        customerDetails.put("lastName", "Smith");
        customerDetails.put("email", "jane.smith@example.com");
        customerDetails.put("phone", "+1-555-987-6543");
        customerDetails.put("address", "456 Oak Ave, City, State 67890");
        customerDetails.put("dateOfBirth", "1985-05-15");
        customerDetails.put("ssn", "***-**-1234");

        Map<String, Object> financeDetails = new HashMap<>();
        financeDetails.put("loanAmount", 75000.00);
        financeDetails.put("downPayment", 15000.00);
        financeDetails.put("interestRate", 2.9);
        financeDetails.put("loanTermMonths", 72);
        financeDetails.put("monthlyPayment", 1045.67);
        financeDetails.put("lenderName", "Mercedes-Benz Financial");
        financeDetails.put("creditScore", 750);

        Map<String, Object> massOrder = new HashMap<>();
        massOrder.put("quantity", 1);
        massOrder.put("unitPrice", 90000.00);
        massOrder.put("totalAmount", 90000.00);
        massOrder.put("discountApplied", 15000.00);
        massOrder.put("finalAmount", 75000.00);
        massOrder.put("taxAmount", 6750.00);
        List<Map<String, Object>> massOrders = Arrays.asList(massOrder);

        response.setContractId(contractId);
        response.setPurchaseRequestId(purchaseRequestId);
        response.setDealId(dealId);
        response.setCustomerDetails(customerDetails);
        response.setFinanceDetails(financeDetails);
        response.setMassOrders(massOrders);
        response.setPdfStorageLocation(pdfLocation);
        response.setCreatedAt(createdAt);
        response.setUpdatedAt(updatedAt);

        // Verify all fields
        assertEquals(contractId, response.getContractId());
        assertEquals(purchaseRequestId, response.getPurchaseRequestId());
        assertEquals(dealId, response.getDealId());
        assertEquals(pdfLocation, response.getPdfStorageLocation());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());

        // Verify JSONB fields
        assertNotNull(response.getCustomerDetails());
        assertEquals(7, response.getCustomerDetails().size());
        assertEquals("Jane", response.getCustomerDetails().get("firstName"));
        assertEquals("jane.smith@example.com", response.getCustomerDetails().get("email"));

        assertNotNull(response.getFinanceDetails());
        assertEquals(7, response.getFinanceDetails().size());
        assertEquals(75000.00, response.getFinanceDetails().get("loanAmount"));
        assertEquals(2.9, response.getFinanceDetails().get("interestRate"));

        assertNotNull(response.getMassOrders());
        assertEquals(1, response.getMassOrders().size());
        assertEquals(90000.00, response.getMassOrders().get(0).get("unitPrice"));
        assertEquals(75000.00, response.getMassOrders().get(0).get("finalAmount"));
    }

    @Test
    void testNullSafetyForAllFields() {
        response.setContractId(null);
        response.setPurchaseRequestId(null);
        response.setDealId(null);
        response.setCustomerDetails(null);
        response.setFinanceDetails(null);
        response.setMassOrders(null);
        response.setPdfStorageLocation(null);
        response.setCreatedAt(null);
        response.setUpdatedAt(null);

        assertNull(response.getContractId());
        assertNull(response.getPurchaseRequestId());
        assertNull(response.getDealId());
        assertNull(response.getCustomerDetails());
        assertNull(response.getFinanceDetails());
        assertNull(response.getMassOrders());
        assertNull(response.getPdfStorageLocation());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }

    @Test
    void testEmptyMapsForJsonbFields() {
        Map<String, Object> emptyMap = new HashMap<>();

        response.setCustomerDetails(emptyMap);
        response.setFinanceDetails(emptyMap);
        response.setMassOrders(Arrays.asList());

        assertNotNull(response.getCustomerDetails());
        assertNotNull(response.getFinanceDetails());
        assertNotNull(response.getMassOrders());

        assertTrue(response.getCustomerDetails().isEmpty());
        assertTrue(response.getFinanceDetails().isEmpty());
        assertTrue(response.getMassOrders().isEmpty());
    }

    @Test
    void testTimestampPrecision() {
        LocalDateTime preciseTime = LocalDateTime.of(2025, 10, 16, 12, 30, 45, 123456789);
        
        response.setCreatedAt(preciseTime);
        response.setUpdatedAt(preciseTime);

        assertEquals(preciseTime, response.getCreatedAt());
        assertEquals(preciseTime, response.getUpdatedAt());
        assertEquals(123456789, response.getCreatedAt().getNano());
        assertEquals(123456789, response.getUpdatedAt().getNano());
    }

    @Test
    void testTimestampOrdering() {
        LocalDateTime createdAt = LocalDateTime.of(2025, 10, 16, 10, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2025, 10, 16, 12, 0, 0);

        response.setCreatedAt(createdAt);
        response.setUpdatedAt(updatedAt);

        assertTrue(response.getUpdatedAt().isAfter(response.getCreatedAt()));
        assertEquals(2, response.getUpdatedAt().getHour() - response.getCreatedAt().getHour());
    }

    @Test
    void testNestedObjectsInJsonbFields() {
        Map<String, Object> customerDetails = new HashMap<>();
        Map<String, Object> address = new HashMap<>();
        address.put("street", "123 Main St");
        address.put("city", "Springfield");
        address.put("state", "IL");
        address.put("zipCode", "62701");
        
        customerDetails.put("name", "John Doe");
        customerDetails.put("address", address);

        response.setCustomerDetails(customerDetails);

        assertNotNull(response.getCustomerDetails());
        assertEquals("John Doe", response.getCustomerDetails().get("name"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> retrievedAddress = (Map<String, Object>) response.getCustomerDetails().get("address");
        assertNotNull(retrievedAddress);
        assertEquals("123 Main St", retrievedAddress.get("street"));
        assertEquals("Springfield", retrievedAddress.get("city"));
    }

    @Test
    void testDifferentDataTypesInJsonbFields() {
        Map<String, Object> financeDetails = new HashMap<>();
        financeDetails.put("loanAmount", 50000.00);        // Double
        financeDetails.put("loanTermMonths", 60);          // Integer
        financeDetails.put("isApproved", true);            // Boolean
        financeDetails.put("approvalDate", "2025-10-16");  // String
        financeDetails.put("notes", null);                 // Null

        response.setFinanceDetails(financeDetails);

        assertNotNull(response.getFinanceDetails());
        assertEquals(50000.00, response.getFinanceDetails().get("loanAmount"));
        assertEquals(60, response.getFinanceDetails().get("loanTermMonths"));
        assertEquals(true, response.getFinanceDetails().get("isApproved"));
        assertEquals("2025-10-16", response.getFinanceDetails().get("approvalDate"));
        assertNull(response.getFinanceDetails().get("notes"));
    }

    @Test
    void testMapModificationAfterSetting() {
        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("name", "John Doe");
        
        response.setCustomerDetails(customerDetails);
        
        // Modify original map
        customerDetails.put("age", 30);
        
        // Verify the response's map is also modified (reference equality)
        assertEquals(30, response.getCustomerDetails().get("age"));
        assertEquals(2, response.getCustomerDetails().size());
    }

    @Test
    void testPdfStorageLocationFormats() {
        String[] validPaths = {
            "/contracts/CONTRACT-ABC12345.pdf",
            "s3://bucket/contracts/CONTRACT-ABC12345.pdf",
            "https://storage.example.com/contracts/CONTRACT-ABC12345.pdf",
            "/local/storage/contracts/CONTRACT-ABC12345.pdf"
        };

        for (String path : validPaths) {
            response.setPdfStorageLocation(path);
            assertEquals(path, response.getPdfStorageLocation());
        }
    }
}
