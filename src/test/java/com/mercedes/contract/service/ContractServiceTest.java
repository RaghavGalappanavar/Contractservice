package com.mercedes.contract.service;

import com.mercedes.contract.dto.ContractDetailsResponse;
import com.mercedes.contract.dto.ContractRequest;
import com.mercedes.contract.dto.ContractResponse;
import com.mercedes.contract.entity.Contract;
import com.mercedes.contract.exception.ContractGenerationException;
import com.mercedes.contract.exception.ContractNotFoundException;
import com.mercedes.contract.repository.ContractRepository;

import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

import java.util.function.Function;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ContractService
 * Tests contract creation, retrieval, and business logic
 */

@DisplayName("ContractService Tests")
class ContractServiceTest {

    private ContractService contractService;
    private ContractRepository contractRepository;
    private PdfGenerationService pdfGenerationService;
    private EventPublishingService eventPublishingService;
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        // Create mock implementations for testing
        contractRepository = new MockContractRepository();
        pdfGenerationService = new MockPdfGenerationService();
        eventPublishingService = new MockEventPublishingService();
        auditService = new MockAuditService();

        // Pre-populate repository with test data
        Contract testContract = new Contract();
        testContract.setContractId("CONTRACT-123");
        testContract.setPurchaseRequestId("PR-12345");
        testContract.setDealId("DEAL-67890");
        testContract.setCustomerDetails(createValidCustomerDetails());
        testContract.setFinanceDetails(createValidFinanceDetails());
        testContract.setMassOrders(createValidMassOrders());
        testContract.setCreatedAt(LocalDateTime.now());
        contractRepository.save(testContract);

        contractService = new ContractService(contractRepository, pdfGenerationService,
                                            eventPublishingService, auditService);
    }

    @Test
    @DisplayName("Should create contract successfully")
    void shouldCreateContractSuccessfully() {
        ContractRequest request = createValidContractRequest();
        // Use a different purchaseRequestId to avoid conflict with pre-populated data
        request.setPurchaseRequestId("PR-NEW-12345");

        ContractResponse response = contractService.generateContract(request);

        assertNotNull(response);
        assertNotNull(response.getContractId());
        assertTrue(response.getContractId().startsWith("CONTRACT-"));
        assertNotNull(response.getContractUrl());
        assertEquals("SIGNED", response.getContractStatus());
        assertNotNull(response.getSignedAt());
    }

    @Test
    @DisplayName("Should throw exception when contract generation fails")
    void shouldThrowExceptionWhenContractGenerationFails() {
        ContractRequest request = createInvalidContractRequest();
        
        assertThrows(ContractGenerationException.class, () -> {
            contractService.generateContract(request);
        });
    }

    @Test
    @DisplayName("Should get contract details successfully")
    void shouldGetContractDetailsSuccessfully() {
        String contractId = "CONTRACT-123";
        
        ContractDetailsResponse response = contractService.getContractById(contractId);
        
        assertNotNull(response);
        assertEquals(contractId, response.getContractId());
        assertNotNull(response.getPurchaseRequestId());
        assertNotNull(response.getDealId());
        assertNotNull(response.getCustomerDetails());
        assertNotNull(response.getFinanceDetails());
        assertNotNull(response.getMassOrders());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    @DisplayName("Should throw exception when contract not found")
    void shouldThrowExceptionWhenContractNotFound() {
        String contractId = "NON_EXISTENT_CONTRACT";
        
        assertThrows(ContractNotFoundException.class, () -> {
            contractService.getContractById(contractId);
        });
    }

    @Test
    @DisplayName("Should handle null contract request")
    void shouldHandleNullContractRequest() {
        assertThrows(NullPointerException.class, () -> {
            contractService.generateContract(null);
        });
    }

    @Test
    @DisplayName("Should handle null contract ID")
    void shouldHandleNullContractId() {
        assertThrows(ContractNotFoundException.class, () -> {
            contractService.getContractById(null);
        });
    }

    @Test
    @DisplayName("Should handle empty contract ID")
    void shouldHandleEmptyContractId() {
        assertThrows(ContractNotFoundException.class, () -> {
            contractService.getContractById("");
        });
    }

    // Helper methods for creating test data
    private ContractRequest createValidContractRequest() {
        ContractRequest.DealData dealData = new ContractRequest.DealData(
                "DEAL-67890",
                createSampleCustomer(),
                createSampleCustomerFinanceDetails(),
                createSampleRetailerInfo(),
                createSampleMassOrders()
        );
        
        return new ContractRequest("PR-12345", "DEAL-67890", dealData);
    }

    private ContractRequest createInvalidContractRequest() {
        // Create request that will cause generation to fail
        ContractRequest.DealData dealData = new ContractRequest.DealData(
                "INVALID_DEAL",
                null, // This will cause failure
                null,
                null,
                null
        );
        
        return new ContractRequest("INVALID_PR", "INVALID_DEAL", dealData);
    }

    private Map<String, Object> createSampleCustomer() {
        Map<String, Object> customer = new HashMap<>();
        customer.put("customerId", "CUST-001");
        customer.put("firstName", "John");
        customer.put("lastName", "Doe");
        customer.put("email", "john.doe@example.com");
        return customer;
    }

    private Map<String, Object> createSampleCustomerFinanceDetails() {
        Map<String, Object> financeDetails = new HashMap<>();
        financeDetails.put("financeType", "LOAN");
        financeDetails.put("loanAmount", 50000.0);
        financeDetails.put("interestRate", 3.5);
        return financeDetails;
    }

    private Map<String, Object> createSampleRetailerInfo() {
        Map<String, Object> retailerInfo = new HashMap<>();
        retailerInfo.put("dealerCode", "MB-DEALER-001");
        retailerInfo.put("salesPersonId", "SP-001");
        return retailerInfo;
    }

    private Map<String, Object> createSampleMassOrders() {
        Map<String, Object> massOrders = new HashMap<>();
        massOrders.put("vehicleModel", "C-Class");
        massOrders.put("quantity", 1);
        massOrders.put("unitPrice", 60000.0);
        return massOrders;
    }

    // Mock implementations for testing
    private static class MockContractRepository implements ContractRepository {
        private Map<String, Contract> contracts = new HashMap<>();

        @Override
        public Contract save(Contract contract) {
            if (contract.getContractId() == null) {
                contract.setContractId("CONTRACT-123");
            }
            contract.setCreatedAt(LocalDateTime.now());
            contracts.put(contract.getContractId(), contract);
            return contract;
        }

        @Override
        public Optional<Contract> findById(String contractId) {
            return Optional.ofNullable(contracts.get(contractId));
        }

        @Override
        public Optional<Contract> findByPurchaseRequestId(String purchaseRequestId) {
            return contracts.values().stream()
                .filter(c -> purchaseRequestId.equals(c.getPurchaseRequestId()))
                .findFirst();
        }

        @Override
        public Optional<Contract> findByDealId(String dealId) {
            return contracts.values().stream()
                .filter(c -> dealId.equals(c.getDealId()))
                .findFirst();
        }

        @Override
        public boolean existsByPurchaseRequestId(String purchaseRequestId) {
            return contracts.values().stream()
                .anyMatch(c -> purchaseRequestId.equals(c.getPurchaseRequestId()));
        }

        @Override
        public boolean existsByDealId(String dealId) {
            return contracts.values().stream()
                .anyMatch(c -> dealId.equals(c.getDealId()));
        }

        @Override
        public Optional<Contract> findContractsWithPdfLocation() {
            return contracts.values().stream()
                .filter(c -> c.getPdfStorageLocation() != null)
                .findFirst();
        }

        // Required JpaRepository methods
        @Override
        public void flush() {}

        @Override
        public <S extends Contract> S saveAndFlush(S entity) { return (S) save(entity); }

        @Override
        public <S extends Contract> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }

        @Override
        public void deleteAllInBatch(Iterable<Contract> entities) {}

        @Override
        public void deleteAllByIdInBatch(Iterable<String> strings) {}

        @Override
        public void deleteAllInBatch() {}

        @Override
        public Contract getOne(String s) { return null; }

        @Override
        public Contract getById(String s) { return findById(s).orElse(null); }

        @Override
        public Contract getReferenceById(String s) { return getById(s); }

        @Override
        public <S extends Contract> Optional<S> findOne(Example<S> example) { return Optional.empty(); }

        @Override
        public <S extends Contract> List<S> findAll(Example<S> example) { return null; }

        @Override
        public <S extends Contract> List<S> findAll(Example<S> example, Sort sort) { return null; }

        @Override
        public <S extends Contract> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }

        @Override
        public <S extends Contract> long count(Example<S> example) { return 0; }

        @Override
        public <S extends Contract> boolean exists(Example<S> example) { return false; }

        @Override
        public <S extends Contract, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) { return null; }

        @Override
        public <S extends Contract> List<S> saveAll(Iterable<S> entities) { return null; }

        @Override
        public List<Contract> findAll() { return new ArrayList<>(contracts.values()); }

        @Override
        public List<Contract> findAllById(Iterable<String> strings) { return null; }

        @Override
        public long count() { return contracts.size(); }

        @Override
        public void deleteById(String s) { contracts.remove(s); }

        @Override
        public void delete(Contract entity) { contracts.remove(entity.getContractId()); }

        @Override
        public void deleteAllById(Iterable<? extends String> strings) {}

        @Override
        public void deleteAll(Iterable<? extends Contract> entities) {}

        @Override
        public void deleteAll() { contracts.clear(); }

        @Override
        public List<Contract> findAll(Sort sort) { return findAll(); }

        @Override
        public Page<Contract> findAll(Pageable pageable) { return null; }

        @Override
        public boolean existsById(String s) { return contracts.containsKey(s); }
    }

    private static class MockPdfGenerationService extends PdfGenerationService {
        public MockPdfGenerationService() {
            super(new MockAuditService());
        }

        @Override
        public String generatePdf(Contract contract) {
            if (contract.getDealId().equals("INVALID_DEAL")) {
                throw new RuntimeException("PDF generation failed");
            }
            return "http://example.com/contracts/" + contract.getContractId() + ".pdf";
        }
    }

    private static class MockEventPublishingService extends EventPublishingService {
        public MockEventPublishingService() {
            super(null, new MockAuditService());
        }

        @Override
        public void publishContractCreatedEvent(Contract contract) {
            // Mock implementation - do nothing
        }
    }

    private static class MockAuditService extends AuditService {
        @Override
        public void logContractCreated(String contractId, String purchaseRequestId, String dealId) {
            // Mock implementation - do nothing
        }

        @Override
        public void logContractCreationFailed(String purchaseRequestId, String dealId, String reason) {
            // Mock implementation - do nothing
        }

        @Override
        public void logContractRetrieved(String contractId) {
            // Mock implementation - do nothing
        }

        @Override
        public void logContractRetrievalFailed(String contractId, String reason) {
            // Mock implementation - do nothing
        }
    }

    // Helper methods for creating test data
    private Map<String, Object> createValidCustomerDetails() {
        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("firstName", "John");
        customerDetails.put("lastName", "Doe");
        customerDetails.put("email", "john.doe@example.com");
        customerDetails.put("phone", "+1234567890");
        customerDetails.put("address", "123 Main St, City, State 12345");
        return customerDetails;
    }

    private Map<String, Object> createValidFinanceDetails() {
        Map<String, Object> financeDetails = new HashMap<>();
        financeDetails.put("loanAmount", 50000.0);
        financeDetails.put("interestRate", 3.5);
        financeDetails.put("loanTerm", 60);
        financeDetails.put("monthlyPayment", 908.33);
        financeDetails.put("downPayment", 10000.0);
        return financeDetails;
    }

    private Map<String, Object> createValidMassOrders() {
        Map<String, Object> massOrders = new HashMap<>();
        massOrders.put("vehicleModel", "Mercedes-Benz C-Class");
        massOrders.put("quantity", 1);
        massOrders.put("unitPrice", 60000.0);
        massOrders.put("totalPrice", 60000.0);
        massOrders.put("deliveryDate", "2024-12-31");
        return massOrders;
    }
}
