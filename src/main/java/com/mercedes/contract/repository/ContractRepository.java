package com.mercedes.contract.repository;

import com.mercedes.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Contract entity
 * Follows Spring Data JPA conventions
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {

    /**
     * Find contract by purchase request ID
     */
    Optional<Contract> findByPurchaseRequestId(String purchaseRequestId);

    /**
     * Find contract by deal ID
     */
    Optional<Contract> findByDealId(String dealId);

    /**
     * Check if contract exists for purchase request
     */
    boolean existsByPurchaseRequestId(String purchaseRequestId);

    /**
     * Check if contract exists for deal
     */
    boolean existsByDealId(String dealId);

    /**
     * Custom query to find contracts with PDF storage location
     */
    @Query("SELECT c FROM Contract c WHERE c.pdfStorageLocation IS NOT NULL")
    Optional<Contract> findContractsWithPdfLocation();
}
