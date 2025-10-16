package com.mercedes.contract.repository;

import com.mercedes.contract.entity.Contract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ContractRepository
 * Tests repository methods with mock implementation
 * No Spring context - pure unit tests with mock repository
 */
class ContractRepositoryTest {

    private MockContractRepository contractRepository;

    @BeforeEach
    void setUp() {
        contractRepository = new MockContractRepository();
    }

    // ========== Unit Tests for save() method ==========

    @Test
    @DisplayName("Should save contract successfully")
    void shouldSaveContractSuccessfully() {
        Contract contract = createValidContract();
        
        Contract savedContract = contractRepository.save(contract);
        
        assertNotNull(savedContract);
        assertEquals(contract.getContractId(), savedContract.getContractId());
        assertEquals(contract.getPurchaseRequestId(), savedContract.getPurchaseRequestId());
        assertEquals(contract.getDealId(), savedContract.getDealId());
        assertNotNull(savedContract.getCreatedAt());
    }

    @Test
    @DisplayName("Should update existing contract")
    void shouldUpdateExistingContract() {
        Contract contract = createValidContract();
        contractRepository.save(contract);
        
        // Update the contract
        contract.setPdfStorageLocation("/updated/path/contract.pdf");
        Contract updatedContract = contractRepository.save(contract);
        
        assertNotNull(updatedContract);
        assertEquals("/updated/path/contract.pdf", updatedContract.getPdfStorageLocation());
    }

    // ========== Unit Tests for findById() method ==========

    @Test
    @DisplayName("Should find contract by ID when exists")
    void shouldFindContractByIdWhenExists() {
        Contract contract = createValidContract();
        contractRepository.save(contract);
        
        Optional<Contract> found = contractRepository.findById(contract.getContractId());
        
        assertTrue(found.isPresent());
        assertEquals(contract.getContractId(), found.get().getContractId());
        assertEquals(contract.getPurchaseRequestId(), found.get().getPurchaseRequestId());
    }

    @Test
    @DisplayName("Should return empty when contract ID does not exist")
    void shouldReturnEmptyWhenContractIdDoesNotExist() {
        Optional<Contract> found = contractRepository.findById("NON_EXISTENT_ID");
        
        assertFalse(found.isPresent());
    }

    // ========== Unit Tests for findByPurchaseRequestId() method ==========

    @Test
    @DisplayName("Should find contract by purchase request ID when exists")
    void shouldFindContractByPurchaseRequestIdWhenExists() {
        Contract contract = createValidContract();
        contractRepository.save(contract);
        
        Optional<Contract> found = contractRepository.findByPurchaseRequestId(contract.getPurchaseRequestId());
        
        assertTrue(found.isPresent());
        assertEquals(contract.getContractId(), found.get().getContractId());
        assertEquals(contract.getPurchaseRequestId(), found.get().getPurchaseRequestId());
    }

    @Test
    @DisplayName("Should return empty when purchase request ID does not exist")
    void shouldReturnEmptyWhenPurchaseRequestIdDoesNotExist() {
        Optional<Contract> found = contractRepository.findByPurchaseRequestId("NON_EXISTENT_PR");
        
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should handle null purchase request ID")
    void shouldHandleNullPurchaseRequestId() {
        Optional<Contract> found = contractRepository.findByPurchaseRequestId(null);
        
        assertFalse(found.isPresent());
    }

    // ========== Unit Tests for findByDealId() method ==========

    @Test
    @DisplayName("Should find contract by deal ID when exists")
    void shouldFindContractByDealIdWhenExists() {
        Contract contract = createValidContract();
        contractRepository.save(contract);
        
        Optional<Contract> found = contractRepository.findByDealId(contract.getDealId());
        
        assertTrue(found.isPresent());
        assertEquals(contract.getContractId(), found.get().getContractId());
        assertEquals(contract.getDealId(), found.get().getDealId());
    }

    @Test
    @DisplayName("Should return empty when deal ID does not exist")
    void shouldReturnEmptyWhenDealIdDoesNotExist() {
        Optional<Contract> found = contractRepository.findByDealId("NON_EXISTENT_DEAL");
        
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should handle null deal ID")
    void shouldHandleNullDealId() {
        Optional<Contract> found = contractRepository.findByDealId(null);
        
        assertFalse(found.isPresent());
    }

    // ========== Unit Tests for existsByPurchaseRequestId() method ==========

    @Test
    @DisplayName("Should return true when contract exists for purchase request ID")
    void shouldReturnTrueWhenContractExistsForPurchaseRequestId() {
        Contract contract = createValidContract();
        contractRepository.save(contract);
        
        boolean exists = contractRepository.existsByPurchaseRequestId(contract.getPurchaseRequestId());
        
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when contract does not exist for purchase request ID")
    void shouldReturnFalseWhenContractDoesNotExistForPurchaseRequestId() {
        boolean exists = contractRepository.existsByPurchaseRequestId("NON_EXISTENT_PR");
        
        assertFalse(exists);
    }

    @Test
    @DisplayName("Should return false for null purchase request ID")
    void shouldReturnFalseForNullPurchaseRequestId() {
        boolean exists = contractRepository.existsByPurchaseRequestId(null);
        
        assertFalse(exists);
    }

    // ========== Unit Tests for existsByDealId() method ==========

    @Test
    @DisplayName("Should return true when contract exists for deal ID")
    void shouldReturnTrueWhenContractExistsForDealId() {
        Contract contract = createValidContract();
        contractRepository.save(contract);
        
        boolean exists = contractRepository.existsByDealId(contract.getDealId());
        
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when contract does not exist for deal ID")
    void shouldReturnFalseWhenContractDoesNotExistForDealId() {
        boolean exists = contractRepository.existsByDealId("NON_EXISTENT_DEAL");
        
        assertFalse(exists);
    }

    @Test
    @DisplayName("Should return false for null deal ID")
    void shouldReturnFalseForNullDealId() {
        boolean exists = contractRepository.existsByDealId(null);
        
        assertFalse(exists);
    }

    // ========== Unit Tests for findContractsWithPdfLocation() method ==========

    @Test
    @DisplayName("Should find contract with PDF location when exists")
    void shouldFindContractWithPdfLocationWhenExists() {
        Contract contract = createValidContract();
        contract.setPdfStorageLocation("/path/to/contract.pdf");
        contractRepository.save(contract);
        
        Optional<Contract> found = contractRepository.findContractsWithPdfLocation();
        
        assertTrue(found.isPresent());
        assertEquals(contract.getContractId(), found.get().getContractId());
        assertNotNull(found.get().getPdfStorageLocation());
    }

    @Test
    @DisplayName("Should return empty when no contracts have PDF location")
    void shouldReturnEmptyWhenNoContractsHavePdfLocation() {
        Contract contract = createValidContract();
        contract.setPdfStorageLocation(null);
        contractRepository.save(contract);
        
        Optional<Contract> found = contractRepository.findContractsWithPdfLocation();
        
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should return empty when no contracts exist")
    void shouldReturnEmptyWhenNoContractsExist() {
        Optional<Contract> found = contractRepository.findContractsWithPdfLocation();
        
        assertFalse(found.isPresent());
    }

    // ========== Unit Tests for findAll() method ==========

    @Test
    @DisplayName("Should return all contracts")
    void shouldReturnAllContracts() {
        Contract contract1 = createValidContract();
        Contract contract2 = createValidContract();
        contract2.setContractId("CONTRACT-67890");
        contract2.setPurchaseRequestId("PR-67890");
        contract2.setDealId("DEAL-67890");
        
        contractRepository.save(contract1);
        contractRepository.save(contract2);
        
        List<Contract> allContracts = contractRepository.findAll();
        
        assertEquals(2, allContracts.size());
    }

    @Test
    @DisplayName("Should return empty list when no contracts exist")
    void shouldReturnEmptyListWhenNoContractsExist() {
        List<Contract> allContracts = contractRepository.findAll();
        
        assertTrue(allContracts.isEmpty());
    }

    // ========== Unit Tests for deleteById() method ==========

    @Test
    @DisplayName("Should delete contract by ID")
    void shouldDeleteContractById() {
        Contract contract = createValidContract();
        contractRepository.save(contract);
        
        contractRepository.deleteById(contract.getContractId());
        
        Optional<Contract> found = contractRepository.findById(contract.getContractId());
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should handle delete of non-existent contract")
    void shouldHandleDeleteOfNonExistentContract() {
        // Should not throw exception
        assertDoesNotThrow(() -> {
            contractRepository.deleteById("NON_EXISTENT_ID");
        });
    }

    // ========== Unit Tests for count() method ==========

    @Test
    @DisplayName("Should return correct count of contracts")
    void shouldReturnCorrectCountOfContracts() {
        assertEquals(0, contractRepository.count());
        
        Contract contract1 = createValidContract();
        contractRepository.save(contract1);
        assertEquals(1, contractRepository.count());
        
        Contract contract2 = createValidContract();
        contract2.setContractId("CONTRACT-67890");
        contract2.setPurchaseRequestId("PR-67890");
        contract2.setDealId("DEAL-67890");
        contractRepository.save(contract2);
        assertEquals(2, contractRepository.count());
    }

    // ========== Helper Methods ==========

    private Contract createValidContract() {
        Contract contract = new Contract();
        contract.setContractId("CONTRACT-12345");
        contract.setPurchaseRequestId("PR-12345");
        contract.setDealId("DEAL-67890");
        contract.setCustomerDetails(createValidCustomerDetails());
        contract.setFinanceDetails(createValidFinanceDetails());
        contract.setMassOrders(createValidMassOrders());
        contract.setCreatedAt(LocalDateTime.now());
        return contract;
    }

    private Map<String, Object> createValidCustomerDetails() {
        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("firstName", "John");
        customerDetails.put("lastName", "Doe");
        customerDetails.put("email", "john.doe@example.com");
        return customerDetails;
    }

    private Map<String, Object> createValidFinanceDetails() {
        Map<String, Object> financeDetails = new HashMap<>();
        financeDetails.put("loanAmount", 50000.0);
        financeDetails.put("interestRate", 3.5);
        return financeDetails;
    }

    private List<Map<String, Object>> createValidMassOrders() {
        Map<String, Object> massOrder = new HashMap<>();
        massOrder.put("vehicleModel", "Mercedes-Benz C-Class");
        massOrder.put("quantity", 1);
        return Arrays.asList(massOrder);
    }

    // ========== Mock Repository Implementation ==========

    private static class MockContractRepository implements ContractRepository {
        private final Map<String, Contract> contracts = new HashMap<>();

        @Override
        public Contract save(Contract contract) {
            if (contract.getCreatedAt() == null) {
                contract.setCreatedAt(LocalDateTime.now());
            }
            contracts.put(contract.getContractId(), contract);
            return contract;
        }

        @Override
        public Optional<Contract> findById(String id) {
            return Optional.ofNullable(contracts.get(id));
        }

        @Override
        public Optional<Contract> findByPurchaseRequestId(String purchaseRequestId) {
            if (purchaseRequestId == null) {
                return Optional.empty();
            }
            return contracts.values().stream()
                    .filter(c -> purchaseRequestId.equals(c.getPurchaseRequestId()))
                    .findFirst();
        }

        @Override
        public Optional<Contract> findByDealId(String dealId) {
            if (dealId == null) {
                return Optional.empty();
            }
            return contracts.values().stream()
                    .filter(c -> dealId.equals(c.getDealId()))
                    .findFirst();
        }

        @Override
        public boolean existsByPurchaseRequestId(String purchaseRequestId) {
            if (purchaseRequestId == null) {
                return false;
            }
            return contracts.values().stream()
                    .anyMatch(c -> purchaseRequestId.equals(c.getPurchaseRequestId()));
        }

        @Override
        public boolean existsByDealId(String dealId) {
            if (dealId == null) {
                return false;
            }
            return contracts.values().stream()
                    .anyMatch(c -> dealId.equals(c.getDealId()));
        }

        @Override
        public Optional<Contract> findContractsWithPdfLocation() {
            return contracts.values().stream()
                    .filter(c -> c.getPdfStorageLocation() != null)
                    .findFirst();
        }

        @Override
        public List<Contract> findAll() {
            return new ArrayList<>(contracts.values());
        }

        @Override
        public void deleteById(String id) {
            contracts.remove(id);
        }

        @Override
        public long count() {
            return contracts.size();
        }

        // Other JpaRepository methods - not used in tests
        @Override
        public boolean existsById(String id) {
            return contracts.containsKey(id);
        }

        @Override
        public List<Contract> findAllById(Iterable<String> ids) {
            List<Contract> result = new ArrayList<>();
            for (String id : ids) {
                Contract contract = contracts.get(id);
                if (contract != null) {
                    result.add(contract);
                }
            }
            return result;
        }

        @Override
        public <S extends Contract> List<S> saveAll(Iterable<S> entities) {
            List<S> result = new ArrayList<>();
            for (S entity : entities) {
                result.add((S) save(entity));
            }
            return result;
        }

        @Override
        public void delete(Contract entity) {
            contracts.remove(entity.getContractId());
        }

        @Override
        public void deleteAllById(Iterable<? extends String> ids) {
            for (String id : ids) {
                contracts.remove(id);
            }
        }

        @Override
        public void deleteAll(Iterable<? extends Contract> entities) {
            for (Contract entity : entities) {
                contracts.remove(entity.getContractId());
            }
        }

        @Override
        public void deleteAll() {
            contracts.clear();
        }

        @Override
        public void flush() {
            // Mock implementation - do nothing
        }

        @Override
        public <S extends Contract> S saveAndFlush(S entity) {
            return (S) save(entity);
        }

        @Override
        public <S extends Contract> List<S> saveAllAndFlush(Iterable<S> entities) {
            return saveAll(entities);
        }

        @Override
        public void deleteAllInBatch(Iterable<Contract> entities) {
            deleteAll(entities);
        }

        @Override
        public void deleteAllByIdInBatch(Iterable<String> ids) {
            deleteAllById(ids);
        }

        @Override
        public void deleteAllInBatch() {
            deleteAll();
        }

        @Override
        public Contract getOne(String id) {
            return contracts.get(id);
        }

        @Override
        public Contract getById(String id) {
            return contracts.get(id);
        }

        @Override
        public Contract getReferenceById(String id) {
            return contracts.get(id);
        }

        @Override
        public <S extends Contract> Optional<S> findOne(org.springframework.data.domain.Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends Contract> List<S> findAll(org.springframework.data.domain.Example<S> example) {
            return new ArrayList<>();
        }

        @Override
        public <S extends Contract> List<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Sort sort) {
            return new ArrayList<>();
        }

        @Override
        public <S extends Contract> org.springframework.data.domain.Page<S> findAll(org.springframework.data.domain.Example<S> example, org.springframework.data.domain.Pageable pageable) {
            return org.springframework.data.domain.Page.empty();
        }

        @Override
        public <S extends Contract> long count(org.springframework.data.domain.Example<S> example) {
            return 0;
        }

        @Override
        public <S extends Contract> boolean exists(org.springframework.data.domain.Example<S> example) {
            return false;
        }

        @Override
        public <S extends Contract, R> R findBy(org.springframework.data.domain.Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
            return null;
        }

        @Override
        public List<Contract> findAll(org.springframework.data.domain.Sort sort) {
            return findAll();
        }

        @Override
        public org.springframework.data.domain.Page<Contract> findAll(org.springframework.data.domain.Pageable pageable) {
            return org.springframework.data.domain.Page.empty();
        }
    }
}
