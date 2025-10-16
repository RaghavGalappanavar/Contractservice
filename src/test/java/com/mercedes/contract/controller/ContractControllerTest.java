package com.mercedes.contract.controller;

import com.mercedes.contract.dto.ContractDetailsResponse;
import com.mercedes.contract.dto.ContractRequest;
import com.mercedes.contract.dto.ContractResponse;
import com.mercedes.contract.exception.ContractGenerationException;
import com.mercedes.contract.exception.ContractNotFoundException;
import com.mercedes.contract.service.ContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ContractController
 * Tests HTTP request handling, validation, and response construction
 * No Spring context - pure unit tests with mock service
 */
class ContractControllerTest {

    private ContractController contractController;
    private MockContractService mockContractService;

    @Mock
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockContractService = new MockContractService();
        contractController = new ContractController(mockContractService);

        // Setup mock HttpServletRequest
        when(mockRequest.getContentType()).thenReturn("application/json");
        when(mockRequest.getContentLength()).thenReturn(100);
    }

    // ========== Unit Tests for generateContract endpoint ==========

    @Test
    @DisplayName("Should generate contract successfully")
    void shouldGenerateContractSuccessfully() {
        ContractRequest request = createValidContractRequest();
        
        ResponseEntity<ContractResponse> response = contractController.generateContract(request, "trace-123", mockRequest);
        
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONTRACT-12345", response.getBody().getContractId());
        assertEquals("SIGNED", response.getBody().getContractStatus());
        
        // Check location header
        URI location = response.getHeaders().getLocation();
        assertNotNull(location);
        assertEquals("/v1/contracts/CONTRACT-12345", location.toString());
    }

    @Test
    @DisplayName("Should handle valid request with trace ID")
    void shouldHandleValidRequestWithTraceId() {
        ContractRequest request = createValidContractRequest();
        
        ResponseEntity<ContractResponse> response = contractController.generateContract(request, "custom-trace-id", mockRequest);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should handle valid request without trace ID")
    void shouldHandleValidRequestWithoutTraceId() {
        ContractRequest request = createValidContractRequest();
        
        ResponseEntity<ContractResponse> response = contractController.generateContract(request, null, mockRequest);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should propagate ContractGenerationException")
    void shouldPropagateContractGenerationException() {
        ContractRequest request = createValidContractRequest();
        request.setPurchaseRequestId("DUPLICATE_PR");
        
        assertThrows(ContractGenerationException.class, () -> {
            contractController.generateContract(request, "trace-123", mockRequest);
        });
    }

    // ========== Unit Tests for getContractById endpoint ==========

    @Test
    @DisplayName("Should retrieve contract details successfully")
    void shouldRetrieveContractDetailsSuccessfully() {
        String contractId = "CONTRACT-12345";
        
        ResponseEntity<ContractDetailsResponse> response = contractController.getContractById(contractId, "trace-123");
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(contractId, response.getBody().getContractId());
        assertEquals("PR-12345", response.getBody().getPurchaseRequestId());
    }

    @Test
    @DisplayName("Should handle contract retrieval with trace ID")
    void shouldHandleContractRetrievalWithTraceId() {
        String contractId = "CONTRACT-12345";
        
        ResponseEntity<ContractDetailsResponse> response = contractController.getContractById(contractId, "custom-trace");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should handle contract retrieval without trace ID")
    void shouldHandleContractRetrievalWithoutTraceId() {
        String contractId = "CONTRACT-12345";
        
        ResponseEntity<ContractDetailsResponse> response = contractController.getContractById(contractId, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Should propagate ContractNotFoundException")
    void shouldPropagateContractNotFoundException() {
        String contractId = "NON_EXISTENT";
        
        assertThrows(ContractNotFoundException.class, () -> {
            contractController.getContractById(contractId, "trace-123");
        });
    }

    // ========== Unit Tests for downloadContractPdf endpoint ==========

    @Test
    @DisplayName("Should download PDF successfully when file exists")
    void shouldDownloadPdfSuccessfullyWhenFileExists() throws IOException {
        String contractId = "CONTRACT-12345";
        
        // Create a temporary file to simulate PDF existence
        File tempFile = File.createTempFile("contract", ".pdf");
        tempFile.deleteOnExit();
        mockContractService.setPdfLocation(tempFile.getAbsolutePath());
        
        ResponseEntity<Resource> response = contractController.downloadContractPdf(contractId, "trace-123");
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Check headers
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());
        assertTrue(response.getHeaders().getContentDisposition().toString().contains("attachment"));
        assertTrue(response.getHeaders().getContentDisposition().toString().contains(contractId + ".pdf"));
    }

    @Test
    @DisplayName("Should return 404 when PDF file does not exist")
    void shouldReturn404WhenPdfFileDoesNotExist() {
        String contractId = "CONTRACT-12345";
        mockContractService.setPdfLocation("/non/existent/path.pdf");
        
        ResponseEntity<Resource> response = contractController.downloadContractPdf(contractId, "trace-123");
        
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Should handle PDF download with trace ID")
    void shouldHandlePdfDownloadWithTraceId() throws IOException {
        String contractId = "CONTRACT-12345";
        File tempFile = File.createTempFile("contract", ".pdf");
        tempFile.deleteOnExit();
        mockContractService.setPdfLocation(tempFile.getAbsolutePath());
        
        ResponseEntity<Resource> response = contractController.downloadContractPdf(contractId, "custom-trace");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle PDF download without trace ID")
    void shouldHandlePdfDownloadWithoutTraceId() throws IOException {
        String contractId = "CONTRACT-12345";
        File tempFile = File.createTempFile("contract", ".pdf");
        tempFile.deleteOnExit();
        mockContractService.setPdfLocation(tempFile.getAbsolutePath());
        
        ResponseEntity<Resource> response = contractController.downloadContractPdf(contractId, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should propagate ContractNotFoundException for PDF download")
    void shouldPropagateContractNotFoundExceptionForPdfDownload() {
        String contractId = "NON_EXISTENT";
        
        assertThrows(ContractNotFoundException.class, () -> {
            contractController.downloadContractPdf(contractId, "trace-123");
        });
    }

    // ========== Helper Methods ==========

    private ContractRequest createValidContractRequest() {
        ContractRequest request = new ContractRequest();
        request.setPurchaseRequestId("PR-12345");
        request.setDealId("DEAL-67890");
        
        // Create deal data
        ContractRequest.DealData dealData = new ContractRequest.DealData();
        
        // Customer details
        Map<String, Object> customer = new HashMap<>();
        customer.put("firstName", "John");
        customer.put("lastName", "Doe");
        customer.put("email", "john.doe@example.com");
        dealData.setCustomer(customer);
        
        // Finance details
        Map<String, Object> finance = new HashMap<>();
        finance.put("loanAmount", 50000.0);
        finance.put("interestRate", 3.5);
        dealData.setCustomerFinanceDetails(finance);
        
        // Mass orders
        Map<String, Object> massOrder = new HashMap<>();
        massOrder.put("vehicleModel", "Mercedes-Benz C-Class");
        massOrder.put("quantity", 1);
        List<Map<String, Object>> massOrders = Arrays.asList(massOrder);
        dealData.setMassOrders(massOrders);
        
        request.setDealData(dealData);
        return request;
    }

    // ========== Mock Service Implementation ==========

    private static class MockContractService extends ContractService {
        private String pdfLocation = "/mock/path/contract.pdf";
        
        public MockContractService() {
            super(null, null, null, null);
        }
        
        public void setPdfLocation(String location) {
            this.pdfLocation = location;
        }
        
        @Override
        public ContractResponse generateContract(ContractRequest request) {
            if ("DUPLICATE_PR".equals(request.getPurchaseRequestId())) {
                throw new ContractGenerationException(request.getPurchaseRequestId(), "Contract already exists");
            }
            
            return new ContractResponse(
                "CONTRACT-12345",
                "http://example.com/contracts/CONTRACT-12345.pdf",
                "SIGNED",
                LocalDateTime.now()
            );
        }
        
        @Override
        public ContractDetailsResponse getContractById(String contractId) {
            if ("NON_EXISTENT".equals(contractId)) {
                throw new ContractNotFoundException(contractId);
            }
            
            ContractDetailsResponse response = new ContractDetailsResponse();
            response.setContractId(contractId);
            response.setPurchaseRequestId("PR-12345");
            response.setDealId("DEAL-67890");
            response.setCustomerDetails(createMockCustomerDetails());
            response.setFinanceDetails(createMockFinanceDetails());
            response.setMassOrders(createMockMassOrders());
            response.setCreatedAt(LocalDateTime.now());
            return response;
        }
        
        @Override
        public String getContractPdfLocation(String contractId) {
            if ("NON_EXISTENT".equals(contractId)) {
                throw new ContractNotFoundException(contractId);
            }
            return pdfLocation;
        }
        
        private Map<String, Object> createMockCustomerDetails() {
            Map<String, Object> customer = new HashMap<>();
            customer.put("firstName", "John");
            customer.put("lastName", "Doe");
            return customer;
        }
        
        private Map<String, Object> createMockFinanceDetails() {
            Map<String, Object> finance = new HashMap<>();
            finance.put("loanAmount", 50000.0);
            return finance;
        }
        
        private List<Map<String, Object>> createMockMassOrders() {
            Map<String, Object> order = new HashMap<>();
            order.put("vehicleModel", "Mercedes-Benz C-Class");
            return Arrays.asList(order);
        }
    }
}
