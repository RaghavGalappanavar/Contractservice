package com.mercedes.contract.controller;

import com.mercedes.contract.dto.ContractDetailsResponse;
import com.mercedes.contract.dto.ContractRequest;
import com.mercedes.contract.dto.ContractResponse;
import com.mercedes.contract.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URI;

/**
 * Contract Controller handling HTTP requests
 * Follows Controller → Service → Repository pattern
 * Handles input validation, request parsing, and response construction
 */
@RestController
@RequestMapping("/v1/contracts")
@Tag(name = "Contract Management", description = "APIs for contract generation and retrieval")
public class ContractController {

    private static final Logger logger = LoggerFactory.getLogger(ContractController.class);

    private final ContractService contractService;

    @Autowired
    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    /**
     * Generate a new contract
     * Implements FR-01: Generate a New Contract via API
     * POST /contracts endpoint
     */
    @PostMapping
    @Operation(
        summary = "Generate a new contract",
        description = "Creates a new contract from purchase request data, generates PDF, and publishes event"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Contract created successfully",
            content = @Content(schema = @Schema(implementation = ContractResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request data"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Contract already exists for this purchase request"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public ResponseEntity<ContractResponse> generateContract(
            @Valid @RequestBody ContractRequest request,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {

        logger.info("Received contract generation request for purchaseRequestId: {}", 
                   request.getPurchaseRequestId());

        ContractResponse response = contractService.generateContract(request);

        // Create location header for the newly created resource
        URI location = URI.create("/v1/contracts/" + response.getContractId());

        logger.info("Contract generation completed successfully, contractId: {}", 
                   response.getContractId());

        return ResponseEntity.created(location).body(response);
    }

    /**
     * Retrieve contract details by ID
     * Implements FR-02: Retrieve Contract Details
     * GET /contracts/{contractId} endpoint
     */
    @GetMapping("/{contractId}")
    @Operation(
        summary = "Retrieve contract details",
        description = "Gets the complete contract object in JSON format by contract ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Contract details retrieved successfully",
            content = @Content(schema = @Schema(implementation = ContractDetailsResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Contract not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public ResponseEntity<ContractDetailsResponse> getContractById(
            @Parameter(description = "Contract ID", required = true)
            @PathVariable String contractId,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {

        logger.info("Received request to retrieve contract details for contractId: {}", contractId);

        ContractDetailsResponse response = contractService.getContractById(contractId);

        logger.info("Contract details retrieved successfully for contractId: {}", contractId);

        return ResponseEntity.ok(response);
    }

    /**
     * Download contract PDF
     * Implements FR-03: Retrieve Contract PDF
     * GET /contracts/{contractId}/pdf endpoint
     */
    @GetMapping("/{contractId}/pdf")
    @Operation(
        summary = "Download contract PDF",
        description = "Downloads the PDF file for a specific contract"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "PDF file downloaded successfully",
            content = @Content(mediaType = "application/pdf")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Contract or PDF not found"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public ResponseEntity<Resource> downloadContractPdf(
            @Parameter(description = "Contract ID", required = true)
            @PathVariable String contractId,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId) {

        logger.info("Received request to download PDF for contractId: {}", contractId);

        String pdfLocation = contractService.getContractPdfLocation(contractId);

        // Create file resource
        File pdfFile = new File(pdfLocation);
        if (!pdfFile.exists()) {
            logger.error("PDF file not found at location: {}", pdfLocation);
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(pdfFile);

        // Set appropriate headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", contractId + ".pdf");

        logger.info("PDF download initiated for contractId: {}", contractId);

        return ResponseEntity.ok()
            .headers(headers)
            .body(resource);
    }
}
