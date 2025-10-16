package com.mercedes.contract.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Unit tests for Contract entity
 * Tests JPA entity functionality, JSONB fields, and data integrity
 */
class ContractTest {

    private Contract contract;

    @BeforeEach
    void setUp() {
        contract = new Contract();
    }

    @Test
    void testDefaultConstructor() {
        Contract newContract = new Contract();
        assertNotNull(newContract);
        assertNull(newContract.getContractId());
        assertNull(newContract.getPurchaseRequestId());
        assertNull(newContract.getDealId());
        assertNull(newContract.getCustomerDetails());
        assertNull(newContract.getFinanceDetails());
        assertNull(newContract.getMassOrders());
        assertNull(newContract.getPdfStorageLocation());
        assertNull(newContract.getCreatedAt());
        assertNull(newContract.getUpdatedAt());
    }

    @Test
    void testBasicFieldsSettersAndGetters() {
        String contractId = "CONTRACT-ABC12345";
        String purchaseRequestId = "PR-67890";
        String dealId = "DEAL-54321";
        String pdfLocation = "/contracts/CONTRACT-ABC12345.pdf";

        contract.setContractId(contractId);
        contract.setPurchaseRequestId(purchaseRequestId);
        contract.setDealId(dealId);
        contract.setPdfStorageLocation(pdfLocation);

        assertEquals(contractId, contract.getContractId());
        assertEquals(purchaseRequestId, contract.getPurchaseRequestId());
        assertEquals(dealId, contract.getDealId());
        assertEquals(pdfLocation, contract.getPdfStorageLocation());
    }

    @Test
    void testTimestampFields() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdAt = now.minusHours(1);
        LocalDateTime updatedAt = now;

        contract.setCreatedAt(createdAt);
        contract.setUpdatedAt(updatedAt);

        assertEquals(createdAt, contract.getCreatedAt());
        assertEquals(updatedAt, contract.getUpdatedAt());
        assertTrue(contract.getUpdatedAt().isAfter(contract.getCreatedAt()));
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

        Map<String, Object> massOrders = new HashMap<>();
        massOrders.put("quantity", 1);
        massOrders.put("unitPrice", 60000.00);
        massOrders.put("totalAmount", 60000.00);

        contract.setCustomerDetails(customerDetails);
        contract.setFinanceDetails(financeDetails);
        contract.setMassOrders(massOrders);

        assertEquals(customerDetails, contract.getCustomerDetails());
        assertEquals(financeDetails, contract.getFinanceDetails());
        assertEquals(massOrders, contract.getMassOrders());

        // Test specific values
        assertEquals("John", contract.getCustomerDetails().get("firstName"));
        assertEquals(50000.00, contract.getFinanceDetails().get("loanAmount"));
        assertEquals(1, contract.getMassOrders().get("quantity"));
    }

    @Test
    void testCompleteContractEntity() {
        // Setup complete contract
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

        Map<String, Object> massOrders = new HashMap<>();
        massOrders.put("quantity", 1);
        massOrders.put("unitPrice", 90000.00);
        massOrders.put("totalAmount", 90000.00);
        massOrders.put("discountApplied", 15000.00);
        massOrders.put("finalAmount", 75000.00);
        massOrders.put("taxAmount", 6750.00);

        contract.setContractId(contractId);
        contract.setPurchaseRequestId(purchaseRequestId);
        contract.setDealId(dealId);
        contract.setCustomerDetails(customerDetails);
        contract.setFinanceDetails(financeDetails);
        contract.setMassOrders(massOrders);
        contract.setPdfStorageLocation(pdfLocation);
        contract.setCreatedAt(createdAt);
        contract.setUpdatedAt(updatedAt);

        // Verify all fields
        assertEquals(contractId, contract.getContractId());
        assertEquals(purchaseRequestId, contract.getPurchaseRequestId());
        assertEquals(dealId, contract.getDealId());
        assertEquals(pdfLocation, contract.getPdfStorageLocation());
        assertEquals(createdAt, contract.getCreatedAt());
        assertEquals(updatedAt, contract.getUpdatedAt());

        // Verify JSONB fields
        assertNotNull(contract.getCustomerDetails());
        assertEquals(7, contract.getCustomerDetails().size());
        assertEquals("Jane", contract.getCustomerDetails().get("firstName"));
        assertEquals("jane.smith@example.com", contract.getCustomerDetails().get("email"));

        assertNotNull(contract.getFinanceDetails());
        assertEquals(7, contract.getFinanceDetails().size());
        assertEquals(75000.00, contract.getFinanceDetails().get("loanAmount"));
        assertEquals(2.9, contract.getFinanceDetails().get("interestRate"));

        assertNotNull(contract.getMassOrders());
        assertEquals(6, contract.getMassOrders().size());
        assertEquals(90000.00, contract.getMassOrders().get("unitPrice"));
        assertEquals(75000.00, contract.getMassOrders().get("finalAmount"));
    }

    @Test
    void testContractIdFormat() {
        String validContractId = "CONTRACT-ABC12345";
        contract.setContractId(validContractId);
        
        assertEquals(validContractId, contract.getContractId());
        assertTrue(contract.getContractId().startsWith("CONTRACT-"));
        assertEquals(17, contract.getContractId().length()); // CONTRACT- + 8 chars
    }

    @Test
    void testPurchaseRequestIdFormat() {
        String validPurchaseRequestId = "PR-123456789";
        contract.setPurchaseRequestId(validPurchaseRequestId);
        
        assertEquals(validPurchaseRequestId, contract.getPurchaseRequestId());
        assertTrue(contract.getPurchaseRequestId().startsWith("PR-"));
    }

    @Test
    void testDealIdFormat() {
        String validDealId = "DEAL-987654321";
        contract.setDealId(validDealId);
        
        assertEquals(validDealId, contract.getDealId());
        assertTrue(contract.getDealId().startsWith("DEAL-"));
    }

    @Test
    void testNullSafetyForAllFields() {
        contract.setContractId(null);
        contract.setPurchaseRequestId(null);
        contract.setDealId(null);
        contract.setCustomerDetails(null);
        contract.setFinanceDetails(null);
        contract.setMassOrders(null);
        contract.setPdfStorageLocation(null);
        contract.setCreatedAt(null);
        contract.setUpdatedAt(null);

        assertNull(contract.getContractId());
        assertNull(contract.getPurchaseRequestId());
        assertNull(contract.getDealId());
        assertNull(contract.getCustomerDetails());
        assertNull(contract.getFinanceDetails());
        assertNull(contract.getMassOrders());
        assertNull(contract.getPdfStorageLocation());
        assertNull(contract.getCreatedAt());
        assertNull(contract.getUpdatedAt());
    }

    @Test
    void testEmptyMapsForJsonbFields() {
        Map<String, Object> emptyMap = new HashMap<>();
        
        contract.setCustomerDetails(emptyMap);
        contract.setFinanceDetails(emptyMap);
        contract.setMassOrders(emptyMap);

        assertNotNull(contract.getCustomerDetails());
        assertNotNull(contract.getFinanceDetails());
        assertNotNull(contract.getMassOrders());
        
        assertTrue(contract.getCustomerDetails().isEmpty());
        assertTrue(contract.getFinanceDetails().isEmpty());
        assertTrue(contract.getMassOrders().isEmpty());
    }

    @Test
    void testTimestampPrecision() {
        LocalDateTime preciseTime = LocalDateTime.of(2025, 10, 16, 12, 30, 45, 123456789);
        
        contract.setCreatedAt(preciseTime);
        contract.setUpdatedAt(preciseTime);

        assertEquals(preciseTime, contract.getCreatedAt());
        assertEquals(preciseTime, contract.getUpdatedAt());
        assertEquals(123456789, contract.getCreatedAt().getNano());
        assertEquals(123456789, contract.getUpdatedAt().getNano());
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

        contract.setCustomerDetails(customerDetails);

        assertNotNull(contract.getCustomerDetails());
        assertEquals("John Doe", contract.getCustomerDetails().get("name"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> retrievedAddress = (Map<String, Object>) contract.getCustomerDetails().get("address");
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

        contract.setFinanceDetails(financeDetails);

        assertNotNull(contract.getFinanceDetails());
        assertEquals(50000.00, contract.getFinanceDetails().get("loanAmount"));
        assertEquals(60, contract.getFinanceDetails().get("loanTermMonths"));
        assertEquals(true, contract.getFinanceDetails().get("isApproved"));
        assertEquals("2025-10-16", contract.getFinanceDetails().get("approvalDate"));
        assertNull(contract.getFinanceDetails().get("notes"));
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
            contract.setPdfStorageLocation(path);
            assertEquals(path, contract.getPdfStorageLocation());
        }
    }

    @Test
    void testMapModificationAfterSetting() {
        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("name", "John Doe");
        
        contract.setCustomerDetails(customerDetails);
        
        // Modify original map
        customerDetails.put("age", 30);
        
        // Verify the contract's map is also modified (reference equality)
        assertEquals(30, contract.getCustomerDetails().get("age"));
        assertEquals(2, contract.getCustomerDetails().size());
    }
}
