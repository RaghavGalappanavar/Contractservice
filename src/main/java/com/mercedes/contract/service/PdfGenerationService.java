package com.mercedes.contract.service;

import com.itextpdf.html2pdf.HtmlConverter;
import com.mercedes.contract.entity.Contract;
import com.mercedes.contract.exception.PdfGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * PDF Generation Service for creating contract documents
 * Handles both local and S3 storage based on configuration
 */
@Service
public class PdfGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(PdfGenerationService.class);

    @Value("${contract.storage.type}")
    private String storageType;

    @Value("${contract.storage.local.base-path}")
    private String localBasePath;

    @Value("${contract.storage.s3.bucket-name:}")
    private String s3BucketName;

    private final AuditService auditService;

    public PdfGenerationService(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Generate PDF document for contract
     * Returns storage location (file path or S3 URI)
     */
    public String generatePdf(Contract contract) {
        logger.info("Generating PDF for contractId: {}", contract.getContractId());

        try {
            // Generate HTML content from contract data
            String htmlContent = generateHtmlContent(contract);

            // Convert HTML to PDF
            ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(htmlContent, pdfOutputStream);
            byte[] pdfBytes = pdfOutputStream.toByteArray();

            // Store PDF based on configuration
            String storageLocation;
            if ("s3".equalsIgnoreCase(storageType)) {
                storageLocation = storeToS3(contract.getContractId(), pdfBytes);
            } else {
                storageLocation = storeToLocal(contract.getContractId(), pdfBytes);
            }

            auditService.logPdfGenerated(contract.getContractId(), storageLocation);
            logger.info("PDF generated successfully for contractId: {}, location: {}", 
                       contract.getContractId(), storageLocation);

            return storageLocation;

        } catch (Exception e) {
            auditService.logPdfGenerationFailed(contract.getContractId(), e.getMessage());
            throw new PdfGenerationException(contract.getContractId(), 
                "Failed to generate PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Generate HTML content from contract data
     */
    private String generateHtmlContent(Contract contract) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>Contract ").append(contract.getContractId()).append("</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 40px; }");
        html.append("h1 { color: #333; border-bottom: 2px solid #333; }");
        html.append("h2 { color: #666; margin-top: 30px; }");
        html.append(".contract-header { text-align: center; margin-bottom: 40px; }");
        html.append(".section { margin-bottom: 30px; }");
        html.append(".field { margin-bottom: 10px; }");
        html.append(".label { font-weight: bold; display: inline-block; width: 200px; }");
        html.append("</style>");
        html.append("</head><body>");

        // Contract Header
        html.append("<div class='contract-header'>");
        html.append("<h1>VEHICLE PURCHASE CONTRACT</h1>");
        html.append("<p>Contract ID: ").append(contract.getContractId()).append("</p>");
        html.append("<p>Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</p>");
        html.append("</div>");

        // Customer Information
        html.append("<div class='section'>");
        html.append("<h2>Customer Information</h2>");
        Map<String, Object> customer = contract.getCustomerDetails();
        if (customer != null) {
            addField(html, "Customer Name", (String) customer.get("customerName"));
            addField(html, "Customer Company", (String) customer.get("customerCompany"));
            addField(html, "Customer Type", (String) customer.get("customerType"));
            addField(html, "Email", (String) customer.get("customerEmail"));
            addField(html, "Phone", (String) customer.get("customerPhone"));
            addField(html, "Address", (String) customer.get("customerAddress"));
            addField(html, "Tax ID", (String) customer.get("customerTaxId"));
        }
        html.append("</div>");

        // Finance Information
        html.append("<div class='section'>");
        html.append("<h2>Finance Details</h2>");
        Map<String, Object> finance = contract.getFinanceDetails();
        if (finance != null) {
            addField(html, "Finance Type", (String) finance.get("type"));
            addField(html, "Provider", (String) finance.get("provider"));
            addField(html, "Approval Status", (String) finance.get("approvalStatus"));
            addField(html, "Reference Number", (String) finance.get("referenceNumber"));
            addField(html, "Terms (Months)", String.valueOf(finance.get("termsInMonths")));
            addField(html, "Interest Rate", String.valueOf(finance.get("interestRate")));
        }
        html.append("</div>");

        // Vehicle Information
        html.append("<div class='section'>");
        html.append("<h2>Vehicle Orders</h2>");
        List<Map<String, Object>> massOrders = contract.getMassOrders();
        if (massOrders != null && !massOrders.isEmpty()) {
            // This would be expanded based on the actual structure of massOrders
            html.append("<p>Vehicle configuration and pricing details as specified in the order.</p>");
            html.append("<p>Number of mass orders: ").append(massOrders.size()).append("</p>");
        }
        html.append("</div>");

        // Contract Terms
        html.append("<div class='section'>");
        html.append("<h2>Contract Terms</h2>");
        html.append("<p>This contract represents the agreement between the customer and Mercedes-Benz for the purchase of the specified vehicles.</p>");
        html.append("<p>Deal ID: ").append(contract.getDealId()).append("</p>");
        html.append("<p>Purchase Request ID: ").append(contract.getPurchaseRequestId()).append("</p>");
        html.append("</div>");

        html.append("</body></html>");
        
        return html.toString();
    }

    /**
     * Add field to HTML content
     */
    private void addField(StringBuilder html, String label, String value) {
        if (value != null && !value.trim().isEmpty()) {
            html.append("<div class='field'>");
            html.append("<span class='label'>").append(label).append(":</span>");
            html.append("<span>").append(value).append("</span>");
            html.append("</div>");
        }
    }

    /**
     * Store PDF to local file system
     */
    private String storeToLocal(String contractId, byte[] pdfBytes) throws IOException {
        // Create directory if it doesn't exist
        Path basePath = Paths.get(localBasePath);
        if (!Files.exists(basePath)) {
            Files.createDirectories(basePath);
        }

        // Generate filename
        String filename = contractId.toLowerCase() + ".pdf";
        Path filePath = basePath.resolve(filename);

        // Write PDF to file
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(pdfBytes);
        }

        return filePath.toString();
    }

    /**
     * Store PDF to S3 (placeholder implementation)
     * In a real implementation, this would use AWS SDK
     */
    private String storeToS3(String contractId, byte[] pdfBytes) {
        // Placeholder for S3 implementation
        // In real implementation, use AWS SDK to upload to S3
        String s3Key = "contracts/" + contractId.toLowerCase() + ".pdf";
        String s3Uri = "s3://" + s3BucketName + "/" + s3Key;
        
        logger.info("S3 storage not implemented - would store to: {}", s3Uri);
        
        // For now, fall back to local storage
        try {
            return storeToLocal(contractId, pdfBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store PDF", e);
        }
    }
}
