package com.mercedes.contract.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Trace configuration for automatic traceId handling
 * Follows common guidelines - no manual traceId appending in log statements
 * TraceId is automatically captured and logged through MDC
 */
@Component
@Order(1)
public class TraceConfig extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Get or generate trace ID
            String traceId = request.getHeader(TRACE_ID_HEADER);
            if (traceId == null || traceId.trim().isEmpty()) {
                traceId = UUID.randomUUID().toString();
            }
            
            // Set trace ID in MDC for automatic logging
            MDC.put(TRACE_ID_MDC_KEY, traceId);
            
            // Add trace ID to response header
            response.setHeader(TRACE_ID_HEADER, traceId);
            
            // Continue with the filter chain
            filterChain.doFilter(request, response);
            
        } finally {
            // Clean up MDC to prevent memory leaks
            MDC.clear();
        }
    }
}
