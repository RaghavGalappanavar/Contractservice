package com.mercedes.contract.service;

import com.mercedes.contract.dto.ContractDetailsResponse;
import com.mercedes.contract.dto.ContractRequest;
import com.mercedes.contract.dto.ContractResponse;
import com.mercedes.contract.entity.Contract;
import com.mercedes.contract.exception.ContractGenerationException;
import com.mercedes.contract.exception.ContractNotFoundException;
import com.mercedes.contract.repository.ContractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Contract Service containing pure business logic
 * Follows Controller → Service → Repository pattern
 * No validation or response construction (handled in controller)
 */
@Service
@Transactional
public class ContractService {

    private static final Logger logger = LoggerFactory.getLogger(ContractService.class);

    private final ContractRepository contractRepository;
    private final PdfGenerationService pdfGenerationService;
    private final EventPublishingService eventPublishingService;
    private final AuditService auditService;

    @Autowired
    public ContractService(ContractRepository contractRepository,
                          PdfGenerationService pdfGenerationService,
                          EventPublishingService eventPublishingService,
                          AuditService auditService) {
        this.contractRepository = contractRepository;
        this.pdfGenerationService = pdfGenerationService;
        this.eventPublishingService = eventPublishingService;
        this.auditService = auditService;
    }

    /**
     * Generate a new contract
     * Implements FR-01: Generate a New Contract via API
     */
    public ContractResponse generateContract(ContractRequest request) {
        logger.info("Starting contract generation for purchaseRequestId: {}", 
                   request.getPurchaseRequestId());

        try {
            // Check if contract already exists for this purchase request
            if (contractRepository.existsByPurchaseRequestId(request.getPurchaseRequestId())) {
                throw new ContractGenerationException(
                    request.getPurchaseRequestId(),
                    "Contract already exists for this purchase request"
                );
            }

            // Generate unique contract ID
            String contractId = generateContractId();

            // Create contract entity
            Contract contract = new Contract(
                contractId,
                request.getPurchaseRequestId(),
                request.getDealId(),
                request.getDealData().getCustomer(),
                request.getDealData().getCustomerFinanceDetails(),
                request.getDealData().getMassOrders()
            );

            // Save contract to database
            Contract savedContract = contractRepository.save(contract);
            logger.info("Contract saved to database with ID: {}", contractId);

            // Generate PDF document
            String pdfLocation = pdfGenerationService.generatePdf(savedContract);
            savedContract.setPdfStorageLocation(pdfLocation);
            contractRepository.save(savedContract);

            // Publish CONTRACT_CREATED event
            eventPublishingService.publishContractCreatedEvent(savedContract);

            // Audit logging
            auditService.logContractCreated(contractId, request.getPurchaseRequestId(), request.getDealId());

            logger.info("Contract generation completed successfully for contractId: {}", contractId);

            // Return response
            return new ContractResponse(
                contractId,
                pdfLocation,
                "SIGNED",
                LocalDateTime.now()
            );

        } catch (Exception e) {
            auditService.logContractCreationFailed(
                request.getPurchaseRequestId(),
                request.getDealId(),
                e.getMessage()
            );
            
            if (e instanceof ContractGenerationException) {
                throw e;
            }
            
            throw new ContractGenerationException(
                request.getPurchaseRequestId(),
                "Failed to generate contract: " + e.getMessage(),
                e
            );
        }
    }

    /**
     * Retrieve contract details by ID
     * Implements FR-02: Retrieve Contract Details
     */
    @Transactional(readOnly = true)
    public ContractDetailsResponse getContractById(String contractId) {
        logger.info("Retrieving contract details for contractId: {}", contractId);

        try {
            Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ContractNotFoundException(contractId));

            auditService.logContractRetrieved(contractId);

            return new ContractDetailsResponse(
                contract.getContractId(),
                contract.getPurchaseRequestId(),
                contract.getDealId(),
                contract.getCustomerDetails(),
                contract.getFinanceDetails(),
                contract.getMassOrders(),
                contract.getPdfStorageLocation(),
                contract.getCreatedAt(),
                contract.getUpdatedAt()
            );

        } catch (ContractNotFoundException e) {
            auditService.logContractRetrievalFailed(contractId, "Contract not found");
            throw e;
        } catch (Exception e) {
            auditService.logContractRetrievalFailed(contractId, e.getMessage());
            throw new RuntimeException("Failed to retrieve contract: " + e.getMessage(), e);
        }
    }

    /**
     * Get contract PDF file path
     * Implements FR-03: Retrieve Contract PDF
     */
    @Transactional(readOnly = true)
    public String getContractPdfLocation(String contractId) {
        logger.info("Retrieving PDF location for contractId: {}", contractId);

        Contract contract = contractRepository.findById(contractId)
            .orElseThrow(() -> new ContractNotFoundException(contractId));

        if (contract.getPdfStorageLocation() == null) {
            throw new ContractNotFoundException(contractId, "PDF not found for contract");
        }

        return contract.getPdfStorageLocation();
    }

    /**
     * Generate unique contract ID in format CONTRACT-XXXXXXXX
     */
    private String generateContractId() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return "CONTRACT-" + uuid.substring(0, 8);
    }
}
