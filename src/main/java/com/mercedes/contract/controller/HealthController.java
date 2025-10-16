package com.mercedes.contract.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Dedicated Health Controller as per common guidelines
 * Implements health endpoints: /health/ready, /health/live, /v1/contract/health
 */
@RestController
public class HealthController {

    @Autowired
    private DataSource dataSource;

    /**
     * Readiness probe endpoint
     * Checks if the service is ready to accept traffic
     */
    @GetMapping("/health/ready")
    public ResponseEntity<Map<String, Object>> readiness() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check database connectivity
            try (Connection connection = dataSource.getConnection()) {
                boolean isValid = connection.isValid(5);
                if (isValid) {
                    response.put("status", "UP");
                    response.put("database", "UP");
                    response.put("message", "Service is ready");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("status", "DOWN");
                    response.put("database", "DOWN");
                    response.put("message", "Database connection invalid");
                    return ResponseEntity.status(503).body(response);
                }
            }
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("database", "DOWN");
            response.put("message", "Database connection failed");
            response.put("error", e.getMessage());
            return ResponseEntity.status(503).body(response);
        }
    }

    /**
     * Liveness probe endpoint
     * Checks if the service is alive
     */
    @GetMapping("/health/live")
    public ResponseEntity<Map<String, Object>> liveness() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Service is alive");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Business capability health endpoint
     * Follows /v1/{capability-name}/health pattern
     */
    @GetMapping("/v1/contract/health")
    public ResponseEntity<Map<String, Object>> contractHealth() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check database connectivity
            try (Connection connection = dataSource.getConnection()) {
                boolean isValid = connection.isValid(5);
                
                response.put("service", "contract-service");
                response.put("status", isValid ? "UP" : "DOWN");
                response.put("database", isValid ? "UP" : "DOWN");
                response.put("capability", "contract");
                response.put("version", "1.0.0");
                response.put("timestamp", System.currentTimeMillis());
                
                if (isValid) {
                    response.put("message", "Contract service is healthy");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("message", "Contract service is unhealthy - database issues");
                    return ResponseEntity.status(503).body(response);
                }
            }
        } catch (Exception e) {
            response.put("service", "contract-service");
            response.put("status", "DOWN");
            response.put("database", "DOWN");
            response.put("capability", "contract");
            response.put("message", "Contract service is unhealthy");
            response.put("error", "Database connection failed");
            return ResponseEntity.status(503).body(response);
        }
    }


}
