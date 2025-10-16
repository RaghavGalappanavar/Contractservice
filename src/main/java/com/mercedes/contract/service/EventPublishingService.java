package com.mercedes.contract.service;

import com.mercedes.contract.entity.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Event Publishing Service for Kafka events
 * Implements FR-04: Publish Contract Creation Event
 */
@Service
public class EventPublishingService {

    private static final Logger logger = LoggerFactory.getLogger(EventPublishingService.class);

    @Value("${contract.events.topic}")
    private String contractEventsTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AuditService auditService;

    @Autowired
    public EventPublishingService(KafkaTemplate<String, Object> kafkaTemplate, AuditService auditService) {
        this.kafkaTemplate = kafkaTemplate;
        this.auditService = auditService;
    }

    /**
     * Publish CONTRACT_CREATED event to Kafka
     * Follows the event payload format specified in business requirements
     */
    public void publishContractCreatedEvent(Contract contract) {
        logger.info("Publishing CONTRACT_CREATED event for contractId: {}", contract.getContractId());

        try {
            // Create event payload as per business requirements
            Map<String, Object> eventPayload = new HashMap<>();
            eventPayload.put("eventId", UUID.randomUUID().toString());
            eventPayload.put("eventType", "CONTRACT_CREATED");
            eventPayload.put("eventTimestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("contractId", contract.getContractId());
            eventData.put("purchaseRequestId", contract.getPurchaseRequestId());
            eventData.put("dealId", contract.getDealId());
            eventData.put("contractPdfLocation", contract.getPdfStorageLocation());
            
            eventPayload.put("data", eventData);

            // Send event to Kafka topic
            kafkaTemplate.send(contractEventsTopic, contract.getContractId(), eventPayload)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        logger.info("CONTRACT_CREATED event published successfully for contractId: {}", 
                                   contract.getContractId());
                        auditService.logEventPublished("CONTRACT_CREATED", contract.getContractId(), contractEventsTopic);
                    } else {
                        logger.error("Failed to publish CONTRACT_CREATED event for contractId: {}", 
                                    contract.getContractId(), ex);
                        auditService.logEventPublishingFailed("CONTRACT_CREATED", contract.getContractId(), 
                                                             contractEventsTopic, ex.getMessage());
                    }
                });

        } catch (Exception e) {
            logger.error("Error publishing CONTRACT_CREATED event for contractId: {}", 
                        contract.getContractId(), e);
            auditService.logEventPublishingFailed("CONTRACT_CREATED", contract.getContractId(), 
                                                 contractEventsTopic, e.getMessage());
            throw new RuntimeException("Failed to publish contract created event", e);
        }
    }
}
