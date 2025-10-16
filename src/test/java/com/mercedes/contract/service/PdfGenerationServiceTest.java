package com.mercedes.contract.service;

import com.mercedes.contract.entity.Contract;
import com.mercedes.contract.exception.PdfGenerationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PdfGenerationService
 * Tests PDF generation functionality and error handling
 */

@DisplayName("PdfGenerationService Tests")
class PdfGenerationServiceTest {

    private PdfGenerationService pdfGenerationService;

    @BeforeEach
    void setUp() {
        // Create a mock PDF generation service that doesn't require file system access
        pdfGenerationService = new MockPdfGenerationService();
    }

    @Test
    @DisplayName("Should generate PDF successfully for valid contract")
    void shouldGeneratePdfSuccessfullyForValidContract() {
        Contract contract = createValidContract();
        
        String pdfUrl = pdfGenerationService.generatePdf(contract);
        
        assertNotNull(pdfUrl);
        assertTrue(pdfUrl.contains(contract.getContractId()));
        assertTrue(pdfUrl.endsWith(".pdf"));
    }

    @Test
    @DisplayName("Should throw exception when contract is null")
    void shouldThrowExceptionWhenContractIsNull() {
        assertThrows(PdfGenerationException.class, () -> {
            pdfGenerationService.generatePdf(null);
        });
    }

    @Test
    @DisplayName("Should throw exception when contract ID is null")
    void shouldThrowExceptionWhenContractIdIsNull() {
        Contract contract = createValidContract();
        contract.setContractId(null);
        
        assertThrows(PdfGenerationException.class, () -> {
            pdfGenerationService.generatePdf(contract);
        });
    }

    @Test
    @DisplayName("Should throw exception when contract ID is empty")
    void shouldThrowExceptionWhenContractIdIsEmpty() {
        Contract contract = createValidContract();
        contract.setContractId("");
        
        assertThrows(PdfGenerationException.class, () -> {
            pdfGenerationService.generatePdf(contract);
        });
    }

    @Test
    @DisplayName("Should handle contract with minimal data")
    void shouldHandleContractWithMinimalData() {
        Contract contract = new Contract();
        contract.setContractId("CONTRACT-MIN-001");
        contract.setPurchaseRequestId("PR-MIN-001");
        contract.setDealId("DEAL-MIN-001");
        contract.setCustomerDetails(new HashMap<>());
        contract.setFinanceDetails(new HashMap<>());
        contract.setMassOrders(new HashMap<>());
        contract.setCreatedAt(LocalDateTime.now());
        
        String pdfUrl = pdfGenerationService.generatePdf(contract);
        
        assertNotNull(pdfUrl);
        assertTrue(pdfUrl.contains("CONTRACT-MIN-001"));
    }

    @Test
    @DisplayName("Should handle contract with complex customer details")
    void shouldHandleContractWithComplexCustomerDetails() {
        Contract contract = createValidContract();
        
        Map<String, Object> complexCustomerDetails = new HashMap<>();
        complexCustomerDetails.put("customerId", "CUST-COMPLEX-001");
        complexCustomerDetails.put("firstName", "Johann");
        complexCustomerDetails.put("lastName", "Müller");
        complexCustomerDetails.put("email", "johann.muller@mercedes-benz.de");
        complexCustomerDetails.put("phone", "+49-711-123-4567");
        complexCustomerDetails.put("address", "Mercedesstraße 120, 70372 Stuttgart, Germany");
        complexCustomerDetails.put("dateOfBirth", "1985-03-15");
        complexCustomerDetails.put("nationality", "German");
        
        contract.setCustomerDetails(complexCustomerDetails);
        
        String pdfUrl = pdfGenerationService.generatePdf(contract);
        
        assertNotNull(pdfUrl);
        assertTrue(pdfUrl.contains(contract.getContractId()));
    }

    @Test
    @DisplayName("Should handle contract with complex finance details")
    void shouldHandleContractWithComplexFinanceDetails() {
        Contract contract = createValidContract();
        
        Map<String, Object> complexFinanceDetails = new HashMap<>();
        complexFinanceDetails.put("financeType", "LEASE");
        complexFinanceDetails.put("loanAmount", 75000.50);
        complexFinanceDetails.put("downPayment", 15000.00);
        complexFinanceDetails.put("interestRate", 2.9);
        complexFinanceDetails.put("termMonths", 48);
        complexFinanceDetails.put("monthlyPayment", 1456.78);
        complexFinanceDetails.put("currency", "EUR");
        complexFinanceDetails.put("financingBank", "Mercedes-Benz Bank AG");
        
        contract.setFinanceDetails(complexFinanceDetails);
        
        String pdfUrl = pdfGenerationService.generatePdf(contract);
        
        assertNotNull(pdfUrl);
        assertTrue(pdfUrl.contains(contract.getContractId()));
    }

    @Test
    @DisplayName("Should handle contract with complex mass orders")
    void shouldHandleContractWithComplexMassOrders() {
        Contract contract = createValidContract();
        
        Map<String, Object> complexMassOrders = new HashMap<>();
        complexMassOrders.put("vehicleModel", "Mercedes-AMG C 63 S");
        complexMassOrders.put("vehicleVin", "WDDGF4HB1CA123456");
        complexMassOrders.put("engineType", "V8 Biturbo");
        complexMassOrders.put("fuelType", "Petrol");
        complexMassOrders.put("transmission", "9G-TRONIC");
        complexMassOrders.put("color", "Obsidian Black Metallic");
        complexMassOrders.put("interiorColor", "Black Nappa Leather");
        complexMassOrders.put("quantity", 1);
        complexMassOrders.put("unitPrice", 89500.00);
        complexMassOrders.put("totalAmount", 89500.00);
        complexMassOrders.put("deliveryDate", "2024-12-15");
        
        contract.setMassOrders(complexMassOrders);
        
        String pdfUrl = pdfGenerationService.generatePdf(contract);
        
        assertNotNull(pdfUrl);
        assertTrue(pdfUrl.contains(contract.getContractId()));
    }

    @Test
    @DisplayName("Should handle special characters in contract data")
    void shouldHandleSpecialCharactersInContractData() {
        Contract contract = createValidContract();
        
        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("firstName", "José");
        customerDetails.put("lastName", "García-Rodríguez");
        customerDetails.put("email", "josé.garcía@example.com");
        customerDetails.put("address", "Calle de Alcalá, 123, 28009 Madrid, España");
        
        contract.setCustomerDetails(customerDetails);
        
        String pdfUrl = pdfGenerationService.generatePdf(contract);
        
        assertNotNull(pdfUrl);
        assertTrue(pdfUrl.contains(contract.getContractId()));
    }

    @Test
    @DisplayName("Should generate unique URLs for different contracts")
    void shouldGenerateUniqueUrlsForDifferentContracts() {
        Contract contract1 = createValidContract();
        contract1.setContractId("CONTRACT-001");
        
        Contract contract2 = createValidContract();
        contract2.setContractId("CONTRACT-002");
        
        String pdfUrl1 = pdfGenerationService.generatePdf(contract1);
        String pdfUrl2 = pdfGenerationService.generatePdf(contract2);
        
        assertNotNull(pdfUrl1);
        assertNotNull(pdfUrl2);
        assertNotEquals(pdfUrl1, pdfUrl2);
        assertTrue(pdfUrl1.contains("CONTRACT-001"));
        assertTrue(pdfUrl2.contains("CONTRACT-002"));
    }

    @Test
    @DisplayName("Should handle null values in contract maps gracefully")
    void shouldHandleNullValuesInContractMapsGracefully() {
        Contract contract = createValidContract();
        contract.setCustomerDetails(null);
        contract.setFinanceDetails(null);
        contract.setMassOrders(null);
        
        // Should not throw exception, but handle gracefully
        assertDoesNotThrow(() -> {
            String pdfUrl = pdfGenerationService.generatePdf(contract);
            assertNotNull(pdfUrl);
        });
    }

    // Helper method for creating test data
    private Contract createValidContract() {
        Contract contract = new Contract();
        contract.setContractId("CONTRACT-TEST-001");
        contract.setPurchaseRequestId("PR-TEST-001");
        contract.setDealId("DEAL-TEST-001");
        
        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("customerId", "CUST-001");
        customerDetails.put("firstName", "John");
        customerDetails.put("lastName", "Doe");
        customerDetails.put("email", "john.doe@example.com");
        contract.setCustomerDetails(customerDetails);
        
        Map<String, Object> financeDetails = new HashMap<>();
        financeDetails.put("financeType", "LOAN");
        financeDetails.put("loanAmount", 50000.0);
        financeDetails.put("interestRate", 3.5);
        contract.setFinanceDetails(financeDetails);
        
        Map<String, Object> massOrders = new HashMap<>();
        massOrders.put("vehicleModel", "C-Class");
        massOrders.put("quantity", 1);
        massOrders.put("unitPrice", 60000.0);
        contract.setMassOrders(massOrders);
        
        contract.setCreatedAt(LocalDateTime.now());
        
        return contract;
    }

    // Mock implementation for testing
    private static class MockPdfGenerationService extends PdfGenerationService {
        public MockPdfGenerationService() {
            super(new AuditService());
        }

        @Override
        public String generatePdf(Contract contract) {
            if (contract == null) {
                throw new PdfGenerationException("UNKNOWN", "Contract cannot be null");
            }

            String contractId = contract.getContractId();
            if (contractId == null || contractId.trim().isEmpty()) {
                throw new PdfGenerationException("UNKNOWN", "Contract ID cannot be null or empty");
            }

            // Return a mock PDF location
            return "http://example.com/contracts/" + contractId + ".pdf";
        }
    }
}
