package com.mercedes.contract.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * Unit tests for ContractResponse DTO
 * Tests getters, setters, and data integrity
 */
class ContractResponseTest {

    private ContractResponse contractResponse;

    @BeforeEach
    void setUp() {
        contractResponse = new ContractResponse();
    }

    @Test
    void testDefaultConstructor() {
        ContractResponse response = new ContractResponse();
        assertNotNull(response);
        assertNull(response.getContractId());
        assertNull(response.getContractUrl());
        assertNull(response.getContractStatus());
        assertNull(response.getSignedAt());
    }

    @Test
    void testSettersAndGetters() {
        String contractId = "CONTRACT-ABC12345";
        String contractUrl = "https://api.mercedes.com/contracts/CONTRACT-ABC12345/pdf";
        String contractStatus = "GENERATED";
        LocalDateTime signedAt = LocalDateTime.now();

        contractResponse.setContractId(contractId);
        contractResponse.setContractUrl(contractUrl);
        contractResponse.setContractStatus(contractStatus);
        contractResponse.setSignedAt(signedAt);

        assertEquals(contractId, contractResponse.getContractId());
        assertEquals(contractUrl, contractResponse.getContractUrl());
        assertEquals(contractStatus, contractResponse.getContractStatus());
        assertEquals(signedAt, contractResponse.getSignedAt());
    }

    @Test
    void testWithNullValues() {
        contractResponse.setContractId(null);
        contractResponse.setContractUrl(null);
        contractResponse.setContractStatus(null);
        contractResponse.setSignedAt(null);

        assertNull(contractResponse.getContractId());
        assertNull(contractResponse.getContractUrl());
        assertNull(contractResponse.getContractStatus());
        assertNull(contractResponse.getSignedAt());
    }

    @Test
    void testWithEmptyStrings() {
        contractResponse.setContractId("");
        contractResponse.setContractUrl("");
        contractResponse.setContractStatus("");

        assertEquals("", contractResponse.getContractId());
        assertEquals("", contractResponse.getContractUrl());
        assertEquals("", contractResponse.getContractStatus());
    }

    @Test
    void testContractIdFormat() {
        String validContractId = "CONTRACT-ABC12345";
        contractResponse.setContractId(validContractId);
        
        assertEquals(validContractId, contractResponse.getContractId());
        assertTrue(contractResponse.getContractId().startsWith("CONTRACT-"));
        assertEquals(17, contractResponse.getContractId().length()); // CONTRACT- + 8 chars
    }

    @Test
    void testContractUrlFormat() {
        String baseUrl = "https://api.mercedes.com/contracts";
        String contractId = "CONTRACT-ABC12345";
        String expectedUrl = baseUrl + "/" + contractId + "/pdf";
        
        contractResponse.setContractUrl(expectedUrl);
        
        assertEquals(expectedUrl, contractResponse.getContractUrl());
        assertTrue(contractResponse.getContractUrl().contains(contractId));
        assertTrue(contractResponse.getContractUrl().endsWith("/pdf"));
    }

    @Test
    void testContractStatusValues() {
        String[] validStatuses = {"GENERATED", "SIGNED", "CANCELLED", "EXPIRED"};
        
        for (String status : validStatuses) {
            contractResponse.setContractStatus(status);
            assertEquals(status, contractResponse.getContractStatus());
        }
    }

    @Test
    void testSignedAtTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minusDays(1);
        LocalDateTime future = now.plusDays(1);

        // Test with current time
        contractResponse.setSignedAt(now);
        assertEquals(now, contractResponse.getSignedAt());

        // Test with past time
        contractResponse.setSignedAt(past);
        assertEquals(past, contractResponse.getSignedAt());
        assertTrue(contractResponse.getSignedAt().isBefore(now));

        // Test with future time
        contractResponse.setSignedAt(future);
        assertEquals(future, contractResponse.getSignedAt());
        assertTrue(contractResponse.getSignedAt().isAfter(now));
    }

    @Test
    void testCompleteContractResponse() {
        String contractId = "CONTRACT-XYZ98765";
        String contractUrl = "https://api.mercedes.com/contracts/CONTRACT-XYZ98765/pdf";
        String contractStatus = "SIGNED";
        LocalDateTime signedAt = LocalDateTime.of(2025, 10, 16, 12, 30, 45);

        contractResponse.setContractId(contractId);
        contractResponse.setContractUrl(contractUrl);
        contractResponse.setContractStatus(contractStatus);
        contractResponse.setSignedAt(signedAt);

        // Verify all fields are set correctly
        assertEquals(contractId, contractResponse.getContractId());
        assertEquals(contractUrl, contractResponse.getContractUrl());
        assertEquals(contractStatus, contractResponse.getContractStatus());
        assertEquals(signedAt, contractResponse.getSignedAt());

        // Verify data consistency
        assertTrue(contractResponse.getContractUrl().contains(contractResponse.getContractId()));
        assertEquals("SIGNED", contractResponse.getContractStatus());
        assertNotNull(contractResponse.getSignedAt());
    }

    @Test
    void testGeneratedContractResponse() {
        String contractId = "CONTRACT-GEN12345";
        String contractUrl = "https://api.mercedes.com/contracts/CONTRACT-GEN12345/pdf";
        String contractStatus = "GENERATED";

        contractResponse.setContractId(contractId);
        contractResponse.setContractUrl(contractUrl);
        contractResponse.setContractStatus(contractStatus);
        // signedAt should be null for generated contracts
        contractResponse.setSignedAt(null);

        assertEquals(contractId, contractResponse.getContractId());
        assertEquals(contractUrl, contractResponse.getContractUrl());
        assertEquals("GENERATED", contractResponse.getContractStatus());
        assertNull(contractResponse.getSignedAt());
    }

    @Test
    void testLongContractId() {
        String longContractId = "CONTRACT-VERYLONGID123456789";
        contractResponse.setContractId(longContractId);
        assertEquals(longContractId, contractResponse.getContractId());
    }

    @Test
    void testSpecialCharactersInUrl() {
        String urlWithParams = "https://api.mercedes.com/contracts/CONTRACT-ABC12345/pdf?version=1&format=pdf";
        contractResponse.setContractUrl(urlWithParams);
        assertEquals(urlWithParams, contractResponse.getContractUrl());
        assertTrue(contractResponse.getContractUrl().contains("?"));
        assertTrue(contractResponse.getContractUrl().contains("&"));
    }

    @Test
    void testCaseInsensitiveStatus() {
        contractResponse.setContractStatus("generated");
        assertEquals("generated", contractResponse.getContractStatus());
        
        contractResponse.setContractStatus("GENERATED");
        assertEquals("GENERATED", contractResponse.getContractStatus());
        
        contractResponse.setContractStatus("Generated");
        assertEquals("Generated", contractResponse.getContractStatus());
    }

    @Test
    void testSignedAtPrecision() {
        LocalDateTime preciseTime = LocalDateTime.of(2025, 10, 16, 12, 30, 45, 123456789);
        contractResponse.setSignedAt(preciseTime);
        
        assertEquals(preciseTime, contractResponse.getSignedAt());
        assertEquals(123456789, contractResponse.getSignedAt().getNano());
    }

    @Test
    void testFieldIndependence() {
        // Test that setting one field doesn't affect others
        contractResponse.setContractId("CONTRACT-TEST123");
        assertNull(contractResponse.getContractUrl());
        assertNull(contractResponse.getContractStatus());
        assertNull(contractResponse.getSignedAt());

        contractResponse.setContractUrl("https://example.com/test");
        assertEquals("CONTRACT-TEST123", contractResponse.getContractId());
        assertNull(contractResponse.getContractStatus());
        assertNull(contractResponse.getSignedAt());

        contractResponse.setContractStatus("GENERATED");
        assertEquals("CONTRACT-TEST123", contractResponse.getContractId());
        assertEquals("https://example.com/test", contractResponse.getContractUrl());
        assertNull(contractResponse.getSignedAt());

        LocalDateTime now = LocalDateTime.now();
        contractResponse.setSignedAt(now);
        assertEquals("CONTRACT-TEST123", contractResponse.getContractId());
        assertEquals("https://example.com/test", contractResponse.getContractUrl());
        assertEquals("GENERATED", contractResponse.getContractStatus());
        assertEquals(now, contractResponse.getSignedAt());
    }
}
