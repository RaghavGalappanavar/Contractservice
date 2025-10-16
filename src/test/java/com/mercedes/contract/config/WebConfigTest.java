package com.mercedes.contract.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WebConfig
 * Tests CORS configuration setup
 * No Spring context - pure unit tests with mock objects
 */
class WebConfigTest {

    private WebConfig webConfig;
    private MockCorsRegistry corsRegistry;

    @BeforeEach
    void setUp() {
        webConfig = new WebConfig();
        corsRegistry = new MockCorsRegistry();
        
        // Set default values using reflection
        setPrivateField(webConfig, "allowedOrigins", new String[]{
            "http://localhost:3000", "http://localhost:8080", "http://localhost:8081", 
            "http://localhost:8082", "http://localhost:8083"
        });
        setPrivateField(webConfig, "allowedMethods", new String[]{
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        });
        setPrivateField(webConfig, "allowedHeaders", new String[]{"*"});
        setPrivateField(webConfig, "allowCredentials", true);
    }

    // ========== Unit Tests for addCorsMappings() method ==========

    @Test
    @DisplayName("Should configure CORS for API endpoints")
    void shouldConfigureCorsForApiEndpoints() {
        webConfig.addCorsMappings(corsRegistry);
        
        // Verify API mapping was added
        assertTrue(corsRegistry.hasMappingFor("/api/**"));
        
        MockCorsRegistration apiMapping = corsRegistry.getMapping("/api/**");
        assertNotNull(apiMapping);
        
        // Verify allowed origins
        assertArrayEquals(new String[]{
            "http://localhost:3000", "http://localhost:8080", "http://localhost:8081", 
            "http://localhost:8082", "http://localhost:8083"
        }, apiMapping.getAllowedOrigins());
        
        // Verify allowed methods
        assertArrayEquals(new String[]{
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        }, apiMapping.getAllowedMethods());
        
        // Verify allowed headers
        assertArrayEquals(new String[]{"*"}, apiMapping.getAllowedHeaders());
        
        // Verify credentials
        assertTrue(apiMapping.isAllowCredentials());
    }

    @Test
    @DisplayName("Should configure CORS for API docs endpoints")
    void shouldConfigureCorsForApiDocsEndpoints() {
        webConfig.addCorsMappings(corsRegistry);
        
        // Verify API docs mapping was added
        assertTrue(corsRegistry.hasMappingFor("/api-docs/**"));
        
        MockCorsRegistration apiDocsMapping = corsRegistry.getMapping("/api-docs/**");
        assertNotNull(apiDocsMapping);
        
        // Verify allowed origins (same as API)
        assertArrayEquals(new String[]{
            "http://localhost:3000", "http://localhost:8080", "http://localhost:8081", 
            "http://localhost:8082", "http://localhost:8083"
        }, apiDocsMapping.getAllowedOrigins());
        
        // Verify allowed methods (restricted for docs)
        assertArrayEquals(new String[]{"GET", "OPTIONS"}, apiDocsMapping.getAllowedMethods());
        
        // Verify allowed headers
        assertArrayEquals(new String[]{"*"}, apiDocsMapping.getAllowedHeaders());
        
        // Verify credentials
        assertTrue(apiDocsMapping.isAllowCredentials());
    }

    @Test
    @DisplayName("Should configure CORS for Swagger UI endpoints")
    void shouldConfigureCorsForSwaggerUiEndpoints() {
        webConfig.addCorsMappings(corsRegistry);
        
        // Verify Swagger UI mapping was added
        assertTrue(corsRegistry.hasMappingFor("/swagger-ui/**"));
        
        MockCorsRegistration swaggerMapping = corsRegistry.getMapping("/swagger-ui/**");
        assertNotNull(swaggerMapping);
        
        // Verify allowed origins (same as API)
        assertArrayEquals(new String[]{
            "http://localhost:3000", "http://localhost:8080", "http://localhost:8081", 
            "http://localhost:8082", "http://localhost:8083"
        }, swaggerMapping.getAllowedOrigins());
        
        // Verify allowed methods (restricted for UI)
        assertArrayEquals(new String[]{"GET", "OPTIONS"}, swaggerMapping.getAllowedMethods());
        
        // Verify allowed headers
        assertArrayEquals(new String[]{"*"}, swaggerMapping.getAllowedHeaders());
        
        // Verify credentials
        assertTrue(swaggerMapping.isAllowCredentials());
    }

    @Test
    @DisplayName("Should configure all three CORS mappings")
    void shouldConfigureAllThreeCorsMapping() {
        webConfig.addCorsMappings(corsRegistry);
        
        // Verify all three mappings are configured
        assertEquals(3, corsRegistry.getMappingCount());
        assertTrue(corsRegistry.hasMappingFor("/api/**"));
        assertTrue(corsRegistry.hasMappingFor("/api-docs/**"));
        assertTrue(corsRegistry.hasMappingFor("/swagger-ui/**"));
    }

    @Test
    @DisplayName("Should handle custom allowed origins")
    void shouldHandleCustomAllowedOrigins() {
        String[] customOrigins = {"https://example.com", "https://app.example.com"};
        setPrivateField(webConfig, "allowedOrigins", customOrigins);
        
        webConfig.addCorsMappings(corsRegistry);
        
        MockCorsRegistration apiMapping = corsRegistry.getMapping("/api/**");
        assertArrayEquals(customOrigins, apiMapping.getAllowedOrigins());
    }

    @Test
    @DisplayName("Should handle custom allowed methods")
    void shouldHandleCustomAllowedMethods() {
        String[] customMethods = {"GET", "POST"};
        setPrivateField(webConfig, "allowedMethods", customMethods);
        
        webConfig.addCorsMappings(corsRegistry);
        
        MockCorsRegistration apiMapping = corsRegistry.getMapping("/api/**");
        assertArrayEquals(customMethods, apiMapping.getAllowedMethods());
        
        // Verify docs and swagger still use restricted methods
        MockCorsRegistration docsMapping = corsRegistry.getMapping("/api-docs/**");
        assertArrayEquals(new String[]{"GET", "OPTIONS"}, docsMapping.getAllowedMethods());
    }

    @Test
    @DisplayName("Should handle custom allowed headers")
    void shouldHandleCustomAllowedHeaders() {
        String[] customHeaders = {"Content-Type", "Authorization"};
        setPrivateField(webConfig, "allowedHeaders", customHeaders);
        
        webConfig.addCorsMappings(corsRegistry);
        
        MockCorsRegistration apiMapping = corsRegistry.getMapping("/api/**");
        assertArrayEquals(customHeaders, apiMapping.getAllowedHeaders());
    }

    @Test
    @DisplayName("Should handle disabled credentials")
    void shouldHandleDisabledCredentials() {
        setPrivateField(webConfig, "allowCredentials", false);
        
        webConfig.addCorsMappings(corsRegistry);
        
        MockCorsRegistration apiMapping = corsRegistry.getMapping("/api/**");
        assertFalse(apiMapping.isAllowCredentials());
        
        MockCorsRegistration docsMapping = corsRegistry.getMapping("/api-docs/**");
        assertFalse(docsMapping.isAllowCredentials());
        
        MockCorsRegistration swaggerMapping = corsRegistry.getMapping("/swagger-ui/**");
        assertFalse(swaggerMapping.isAllowCredentials());
    }

    @Test
    @DisplayName("Should handle empty allowed origins")
    void shouldHandleEmptyAllowedOrigins() {
        setPrivateField(webConfig, "allowedOrigins", new String[]{});
        
        webConfig.addCorsMappings(corsRegistry);
        
        MockCorsRegistration apiMapping = corsRegistry.getMapping("/api/**");
        assertArrayEquals(new String[]{}, apiMapping.getAllowedOrigins());
    }

    @Test
    @DisplayName("Should handle null allowed origins")
    void shouldHandleNullAllowedOrigins() {
        setPrivateField(webConfig, "allowedOrigins", null);
        
        assertDoesNotThrow(() -> {
            webConfig.addCorsMappings(corsRegistry);
        });
        
        MockCorsRegistration apiMapping = corsRegistry.getMapping("/api/**");
        assertNull(apiMapping.getAllowedOrigins());
    }

    @Test
    @DisplayName("Should handle single allowed origin")
    void shouldHandleSingleAllowedOrigin() {
        String[] singleOrigin = {"https://production.example.com"};
        setPrivateField(webConfig, "allowedOrigins", singleOrigin);
        
        webConfig.addCorsMappings(corsRegistry);
        
        MockCorsRegistration apiMapping = corsRegistry.getMapping("/api/**");
        assertArrayEquals(singleOrigin, apiMapping.getAllowedOrigins());
    }

    @Test
    @DisplayName("Should handle wildcard in allowed headers")
    void shouldHandleWildcardInAllowedHeaders() {
        String[] wildcardHeaders = {"*"};
        setPrivateField(webConfig, "allowedHeaders", wildcardHeaders);
        
        webConfig.addCorsMappings(corsRegistry);
        
        MockCorsRegistration apiMapping = corsRegistry.getMapping("/api/**");
        assertArrayEquals(wildcardHeaders, apiMapping.getAllowedHeaders());
    }

    // ========== Helper Methods ==========

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }

    // ========== Mock CORS Registry and Registration ==========

    private static class MockCorsRegistry extends CorsRegistry {
        private final List<MockCorsRegistration> registrations = new ArrayList<>();
        
        @Override
        public CorsRegistration addMapping(String pathPattern) {
            MockCorsRegistration registration = new MockCorsRegistration(pathPattern);
            registrations.add(registration);
            return registration;
        }
        
        public boolean hasMappingFor(String pathPattern) {
            return registrations.stream()
                    .anyMatch(reg -> reg.getPathPattern().equals(pathPattern));
        }
        
        public MockCorsRegistration getMapping(String pathPattern) {
            return registrations.stream()
                    .filter(reg -> reg.getPathPattern().equals(pathPattern))
                    .findFirst()
                    .orElse(null);
        }
        
        public int getMappingCount() {
            return registrations.size();
        }
    }

    private static class MockCorsRegistration extends CorsRegistration {
        private final String pathPattern;
        private String[] allowedOrigins;
        private String[] allowedMethods;
        private String[] allowedHeaders;
        private boolean allowCredentials;
        
        public MockCorsRegistration(String pathPattern) {
            super(pathPattern);
            this.pathPattern = pathPattern;
        }
        
        @Override
        public CorsRegistration allowedOrigins(String... origins) {
            this.allowedOrigins = origins;
            return this;
        }
        
        @Override
        public CorsRegistration allowedMethods(String... methods) {
            this.allowedMethods = methods;
            return this;
        }
        
        @Override
        public CorsRegistration allowedHeaders(String... headers) {
            this.allowedHeaders = headers;
            return this;
        }
        
        @Override
        public CorsRegistration allowCredentials(boolean allowCredentials) {
            this.allowCredentials = allowCredentials;
            return this;
        }
        
        // Getters for testing
        public String getPathPattern() {
            return pathPattern;
        }
        
        public String[] getAllowedOrigins() {
            return allowedOrigins;
        }
        
        public String[] getAllowedMethods() {
            return allowedMethods;
        }
        
        public String[] getAllowedHeaders() {
            return allowedHeaders;
        }
        
        public boolean isAllowCredentials() {
            return allowCredentials;
        }
    }
}
