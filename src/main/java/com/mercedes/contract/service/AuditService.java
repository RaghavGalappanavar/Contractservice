package com.mercedes.contract.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Dedicated Audit Service for structured logging
 * Separate from business logic as per common guidelines
 * Automatically captures traceId from MDC
 */
@Service
public class AuditService {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Log contract creation audit event
     */
    public void logContractCreated(String contractId, String purchaseRequestId, String dealId) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String traceId = MDC.get("traceId");
        
        auditLogger.info("CONTRACT_CREATED | timestamp={} | contractId={} | purchaseRequestId={} | dealId={} | traceId={} | status=SUCCESS",
            timestamp,
            maskSensitiveData(contractId),
            maskSensitiveData(purchaseRequestId),
            maskSensitiveData(dealId),
            traceId
        );
    }

    /**
     * Log contract creation failure audit event
     */
    public void logContractCreationFailed(String purchaseRequestId, String dealId, String reason) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String traceId = MDC.get("traceId");
        
        auditLogger.error("CONTRACT_CREATION_FAILED | timestamp={} | purchaseRequestId={} | dealId={} | reason={} | traceId={} | status=FAILURE",
            timestamp,
            maskSensitiveData(purchaseRequestId),
            maskSensitiveData(dealId),
            reason,
            traceId
        );
    }

    /**
     * Log contract retrieval audit event
     */
    public void logContractRetrieved(String contractId) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String traceId = MDC.get("traceId");
        
        auditLogger.info("CONTRACT_RETRIEVED | timestamp={} | contractId={} | traceId={} | status=SUCCESS",
            timestamp,
            maskSensitiveData(contractId),
            traceId
        );
    }

    /**
     * Log contract retrieval failure audit event
     */
    public void logContractRetrievalFailed(String contractId, String reason) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String traceId = MDC.get("traceId");
        
        auditLogger.warn("CONTRACT_RETRIEVAL_FAILED | timestamp={} | contractId={} | reason={} | traceId={} | status=FAILURE",
            timestamp,
            maskSensitiveData(contractId),
            reason,
            traceId
        );
    }

    /**
     * Log PDF generation audit event
     */
    public void logPdfGenerated(String contractId, String storageLocation) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String traceId = MDC.get("traceId");
        
        auditLogger.info("PDF_GENERATED | timestamp={} | contractId={} | storageLocation={} | traceId={} | status=SUCCESS",
            timestamp,
            maskSensitiveData(contractId),
            maskSensitiveData(storageLocation),
            traceId
        );
    }

    /**
     * Log PDF generation failure audit event
     */
    public void logPdfGenerationFailed(String contractId, String reason) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String traceId = MDC.get("traceId");
        
        auditLogger.error("PDF_GENERATION_FAILED | timestamp={} | contractId={} | reason={} | traceId={} | status=FAILURE",
            timestamp,
            maskSensitiveData(contractId),
            reason,
            traceId
        );
    }

    /**
     * Log event publishing audit event
     */
    public void logEventPublished(String eventType, String contractId, String topic) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String traceId = MDC.get("traceId");
        
        auditLogger.info("EVENT_PUBLISHED | timestamp={} | eventType={} | contractId={} | topic={} | traceId={} | status=SUCCESS",
            timestamp,
            eventType,
            maskSensitiveData(contractId),
            topic,
            traceId
        );
    }

    /**
     * Log event publishing failure audit event
     */
    public void logEventPublishingFailed(String eventType, String contractId, String topic, String reason) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String traceId = MDC.get("traceId");
        
        auditLogger.error("EVENT_PUBLISHING_FAILED | timestamp={} | eventType={} | contractId={} | topic={} | reason={} | traceId={} | status=FAILURE",
            timestamp,
            eventType,
            maskSensitiveData(contractId),
            topic,
            reason,
            traceId
        );
    }

    /**
     * Log retry attempt audit event
     */
    public void logRetryAttempt(String operation, String identifier, int attemptNumber, int maxAttempts) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String traceId = MDC.get("traceId");
        
        auditLogger.warn("RETRY_ATTEMPT | timestamp={} | operation={} | identifier={} | attempt={}/{} | traceId={} | status=RETRY",
            timestamp,
            operation,
            maskSensitiveData(identifier),
            attemptNumber,
            maxAttempts,
            traceId
        );
    }

    /**
     * Mask sensitive data for audit logging
     * Follows data privacy guidelines
     */
    private String maskSensitiveData(String data) {
        if (data == null || data.length() <= 4) {
            return "****";
        }
        return data.substring(0, 4) + "****";
    }
}
