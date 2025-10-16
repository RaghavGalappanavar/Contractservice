package com.mercedes.contract.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercedes.contract.dto.ContractRequest;
import com.mercedes.contract.dto.ContractResponse;
import com.mercedes.contract.dto.ContractDetailsResponse;
import com.mercedes.contract.dto.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Contract Service
 * Tests complete request flow from controller to database
 * Uses H2 in-memory database for testing
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ContractServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // ========== Integration Tests for Contract Generation ==========

    @Test
    @DisplayName("Should generate contract successfully with complete flow")
    void shouldGenerateContractSuccessfullyWithCompleteFlow() {
        ContractRequest request = createValidContractRequest();
        
        ResponseEntity<ContractResponse> response = restTemplate.postForEntity(
                getBaseUrl() + "/v1/contracts",
                createHttpEntity(request),
                ContractResponse.class
        );
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        ContractResponse contractResponse = response.getBody();
        assertNotNull(contractResponse.getContractId());
        assertTrue(contractResponse.getContractId().startsWith("CONTRACT-"));
        assertNotNull(contractResponse.getContractUrl());
        assertEquals("SIGNED", contractResponse.getContractStatus());
        assertNotNull(contractResponse.getSignedAt());
        
        // Verify Location header
        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().toString().contains(contractResponse.getContractId()));
    }

    @Test
    @DisplayName("Should handle duplicate contract generation")
    void shouldHandleDuplicateContractGeneration() {
        ContractRequest request = createValidContractRequest();
        request.setPurchaseRequestId("DUPLICATE-PR-123");
        
        // First request should succeed
        ResponseEntity<ContractResponse> firstResponse = restTemplate.postForEntity(
                getBaseUrl() + "/v1/contracts",
                createHttpEntity(request),
                ContractResponse.class
        );
        assertEquals(HttpStatus.CREATED, firstResponse.getStatusCode());
        
        // Second request with same purchase request ID should fail
        ResponseEntity<ErrorResponse> secondResponse = restTemplate.postForEntity(
                getBaseUrl() + "/v1/contracts",
                createHttpEntity(request),
                ErrorResponse.class
        );
        assertEquals(HttpStatus.CONFLICT, secondResponse.getStatusCode());
        assertNotNull(secondResponse.getBody());
        assertEquals("CONTRACT_GENERATION_ERROR", secondResponse.getBody().getErrorCode());
    }

    @Test
    @DisplayName("Should validate request data and return bad request")
    void shouldValidateRequestDataAndReturnBadRequest() {
        ContractRequest invalidRequest = new ContractRequest();
        // Missing required fields
        
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                getBaseUrl() + "/v1/contracts",
                createHttpEntity(invalidRequest),
                ErrorResponse.class
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
    }

    // ========== Integration Tests for Contract Retrieval ==========

    @Test
    @DisplayName("Should retrieve contract details successfully")
    void shouldRetrieveContractDetailsSuccessfully() {
        // First create a contract
        ContractRequest request = createValidContractRequest();
        ResponseEntity<ContractResponse> createResponse = restTemplate.postForEntity(
                getBaseUrl() + "/v1/contracts",
                createHttpEntity(request),
                ContractResponse.class
        );
        
        String contractId = createResponse.getBody().getContractId();
        
        // Then retrieve it
        ResponseEntity<ContractDetailsResponse> response = restTemplate.getForEntity(
                getBaseUrl() + "/v1/contracts/" + contractId,
                ContractDetailsResponse.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        ContractDetailsResponse details = response.getBody();
        assertEquals(contractId, details.getContractId());
        assertEquals(request.getPurchaseRequestId(), details.getPurchaseRequestId());
        assertEquals(request.getDealId(), details.getDealId());
        assertNotNull(details.getCustomerDetails());
        assertNotNull(details.getFinanceDetails());
        assertNotNull(details.getMassOrders());
        assertNotNull(details.getCreatedAt());
    }

    @Test
    @DisplayName("Should return not found for non-existent contract")
    void shouldReturnNotFoundForNonExistentContract() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                getBaseUrl() + "/v1/contracts/NON-EXISTENT-ID",
                ErrorResponse.class
        );
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONTRACT_NOT_FOUND", response.getBody().getErrorCode());
    }

    // ========== Integration Tests for PDF Download ==========

    @Test
    @DisplayName("Should download contract PDF successfully")
    void shouldDownloadContractPdfSuccessfully() {
        // First create a contract
        ContractRequest request = createValidContractRequest();
        ResponseEntity<ContractResponse> createResponse = restTemplate.postForEntity(
                getBaseUrl() + "/v1/contracts",
                createHttpEntity(request),
                ContractResponse.class
        );
        
        String contractId = createResponse.getBody().getContractId();
        
        // Then download PDF
        ResponseEntity<byte[]> response = restTemplate.getForEntity(
                getBaseUrl() + "/v1/contracts/" + contractId + "/pdf",
                byte[].class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        
        // Verify content type
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        
        // Verify content disposition header
        String contentDisposition = response.getHeaders().getFirst("Content-Disposition");
        assertNotNull(contentDisposition);
        assertTrue(contentDisposition.contains("attachment"));
        assertTrue(contentDisposition.contains(contractId + ".pdf"));
    }

    @Test
    @DisplayName("Should return not found for PDF of non-existent contract")
    void shouldReturnNotFoundForPdfOfNonExistentContract() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
                getBaseUrl() + "/v1/contracts/NON-EXISTENT-ID/pdf",
                ErrorResponse.class
        );
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONTRACT_NOT_FOUND", response.getBody().getErrorCode());
    }

    // ========== Integration Tests for Health Endpoints ==========

    @Test
    @DisplayName("Should return healthy status for readiness probe")
    void shouldReturnHealthyStatusForReadinessProbe() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                getBaseUrl() + "/health/ready",
                Map.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("UP", response.getBody().get("database"));
    }

    @Test
    @DisplayName("Should return healthy status for liveness probe")
    void shouldReturnHealthyStatusForLivenessProbe() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                getBaseUrl() + "/health/live",
                Map.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    @DisplayName("Should return healthy status for contract health endpoint")
    void shouldReturnHealthyStatusForContractHealthEndpoint() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                getBaseUrl() + "/v1/contract/health",
                Map.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("contract-service", response.getBody().get("service"));
        assertEquals("contract", response.getBody().get("capability"));
        assertEquals("1.0.0", response.getBody().get("version"));
    }

    // ========== Integration Tests for Trace ID Handling ==========

    @Test
    @DisplayName("Should handle trace ID in request headers")
    void shouldHandleTraceIdInRequestHeaders() {
        ContractRequest request = createValidContractRequest();
        String traceId = "test-trace-123";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Trace-Id", traceId);
        
        HttpEntity<ContractRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<ContractResponse> response = restTemplate.postForEntity(
                getBaseUrl() + "/v1/contracts",
                entity,
                ContractResponse.class
        );
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        // Verify trace ID is returned in response header
        assertEquals(traceId, response.getHeaders().getFirst("X-Trace-Id"));
    }

    @Test
    @DisplayName("Should generate trace ID when not provided")
    void shouldGenerateTraceIdWhenNotProvided() {
        ContractRequest request = createValidContractRequest();
        
        ResponseEntity<ContractResponse> response = restTemplate.postForEntity(
                getBaseUrl() + "/v1/contracts",
                createHttpEntity(request),
                ContractResponse.class
        );
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        // Verify trace ID is generated and returned
        String traceId = response.getHeaders().getFirst("X-Trace-Id");
        assertNotNull(traceId);
        assertFalse(traceId.isEmpty());
    }

    // ========== Integration Tests for Error Handling ==========

    @Test
    @DisplayName("Should handle global exception handling")
    void shouldHandleGlobalExceptionHandling() {
        // Test with malformed JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<String> entity = new HttpEntity<>("{invalid json}", headers);
        
        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(
                getBaseUrl() + "/v1/contracts",
                entity,
                ErrorResponse.class
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrorCode());
        assertNotNull(response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    // ========== Helper Methods ==========

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api";
    }

    private HttpEntity<ContractRequest> createHttpEntity(ContractRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request, headers);
    }

    private ContractRequest createValidContractRequest() {
        ContractRequest request = new ContractRequest();
        request.setPurchaseRequestId("PR-" + System.currentTimeMillis());
        request.setDealId("DEAL-" + System.currentTimeMillis());

        ContractRequest.DealData dealData = new ContractRequest.DealData();
        dealData.setDealId("DEAL-" + System.currentTimeMillis());
        dealData.setCustomer(createValidCustomerDetails());
        dealData.setCustomerFinanceDetails(createValidFinanceDetails());
        dealData.setRetailerInfo(createValidRetailerInfo());
        dealData.setMassOrders(createValidMassOrders());

        request.setDealData(dealData);
        return request;
    }

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
        financeDetails.put("downPayment", 10000.0);
        return financeDetails;
    }

    private Map<String, Object> createValidMassOrders() {
        Map<String, Object> massOrders = new HashMap<>();
        massOrders.put("vehicleModel", "Mercedes-Benz C-Class");
        massOrders.put("quantity", 1);
        massOrders.put("color", "Black");
        massOrders.put("trim", "AMG Line");
        return massOrders;
    }

    private Map<String, Object> createValidRetailerInfo() {
        Map<String, Object> retailerInfo = new HashMap<>();
        retailerInfo.put("dealerName", "Mercedes-Benz Downtown");
        retailerInfo.put("dealerCode", "MB001");
        retailerInfo.put("salesPerson", "Jane Smith");
        retailerInfo.put("contactPhone", "+1987654321");
        return retailerInfo;
    }
}
