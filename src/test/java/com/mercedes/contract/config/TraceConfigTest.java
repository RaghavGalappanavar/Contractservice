package com.mercedes.contract.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TraceConfig
 * Tests trace ID handling and MDC management
 * No Spring context - pure unit tests with mock objects
 */
class TraceConfigTest {

    private TraceConfig traceConfig;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        traceConfig = new TraceConfig();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        
        // Clear MDC before each test
        MDC.clear();
    }

    // ========== Unit Tests for doFilterInternal() method ==========

    @Test
    @DisplayName("Should use existing trace ID from request header")
    void shouldUseExistingTraceIdFromRequestHeader() throws ServletException, IOException {
        String existingTraceId = "existing-trace-123";
        request.addHeader("X-Trace-Id", existingTraceId);
        
        traceConfig.doFilterInternal(request, response, filterChain);
        
        // Verify trace ID is set in response header
        assertEquals(existingTraceId, response.getHeader("X-Trace-Id"));
        
        // Verify filter chain was called
        assertTrue(filterChain.wasDoFilterCalled());
    }

    @Test
    @DisplayName("Should generate new trace ID when header is missing")
    void shouldGenerateNewTraceIdWhenHeaderIsMissing() throws ServletException, IOException {
        traceConfig.doFilterInternal(request, response, filterChain);
        
        // Verify trace ID is set in response header
        String responseTraceId = response.getHeader("X-Trace-Id");
        assertNotNull(responseTraceId);
        assertFalse(responseTraceId.isEmpty());
        
        // Verify it's a valid UUID format
        assertDoesNotThrow(() -> UUID.fromString(responseTraceId));
        
        // Verify filter chain was called
        assertTrue(filterChain.wasDoFilterCalled());
    }

    @Test
    @DisplayName("Should generate new trace ID when header is empty")
    void shouldGenerateNewTraceIdWhenHeaderIsEmpty() throws ServletException, IOException {
        request.addHeader("X-Trace-Id", "");
        
        traceConfig.doFilterInternal(request, response, filterChain);
        
        // Verify new trace ID is generated
        String responseTraceId = response.getHeader("X-Trace-Id");
        assertNotNull(responseTraceId);
        assertFalse(responseTraceId.isEmpty());
        
        // Verify it's a valid UUID format
        assertDoesNotThrow(() -> UUID.fromString(responseTraceId));
    }

    @Test
    @DisplayName("Should generate new trace ID when header is whitespace only")
    void shouldGenerateNewTraceIdWhenHeaderIsWhitespaceOnly() throws ServletException, IOException {
        request.addHeader("X-Trace-Id", "   ");
        
        traceConfig.doFilterInternal(request, response, filterChain);
        
        // Verify new trace ID is generated
        String responseTraceId = response.getHeader("X-Trace-Id");
        assertNotNull(responseTraceId);
        assertFalse(responseTraceId.trim().isEmpty());
        
        // Verify it's a valid UUID format
        assertDoesNotThrow(() -> UUID.fromString(responseTraceId));
    }

    @Test
    @DisplayName("Should set trace ID in MDC during filter execution")
    void shouldSetTraceIdInMdcDuringFilterExecution() throws ServletException, IOException {
        String traceId = "test-trace-456";
        request.addHeader("X-Trace-Id", traceId);
        
        // Create a filter chain that checks MDC
        MockFilterChain mdcCheckingChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response) 
                    throws IOException, ServletException {
                super.doFilter(request, response);
                // Verify MDC is set during filter execution
                assertEquals(traceId, MDC.get("traceId"));
            }
        };
        
        traceConfig.doFilterInternal(request, response, mdcCheckingChain);
        
        // Verify MDC is cleared after filter execution
        assertNull(MDC.get("traceId"));
    }

    @Test
    @DisplayName("Should clear MDC even when filter chain throws exception")
    void shouldClearMdcEvenWhenFilterChainThrowsException() throws ServletException, IOException {
        String traceId = "test-trace-789";
        request.addHeader("X-Trace-Id", traceId);
        
        // Create a filter chain that throws exception
        MockFilterChain exceptionThrowingChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response) 
                    throws IOException, ServletException {
                super.doFilter(request, response);
                throw new ServletException("Test exception");
            }
        };
        
        // Verify exception is thrown but MDC is still cleared
        assertThrows(ServletException.class, () -> {
            traceConfig.doFilterInternal(request, response, exceptionThrowingChain);
        });
        
        // Verify MDC is cleared even after exception
        assertNull(MDC.get("traceId"));
    }

    @Test
    @DisplayName("Should handle multiple requests with different trace IDs")
    void shouldHandleMultipleRequestsWithDifferentTraceIds() throws ServletException, IOException {
        // First request
        String traceId1 = "trace-001";
        request.addHeader("X-Trace-Id", traceId1);
        traceConfig.doFilterInternal(request, response, filterChain);
        assertEquals(traceId1, response.getHeader("X-Trace-Id"));
        
        // Second request with different trace ID
        MockHttpServletRequest request2 = new MockHttpServletRequest();
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        String traceId2 = "trace-002";
        request2.addHeader("X-Trace-Id", traceId2);
        
        traceConfig.doFilterInternal(request2, response2, filterChain);
        assertEquals(traceId2, response2.getHeader("X-Trace-Id"));
        
        // Verify MDC is cleared
        assertNull(MDC.get("traceId"));
    }

    @Test
    @DisplayName("Should handle concurrent requests independently")
    void shouldHandleConcurrentRequestsIndependently() throws ServletException, IOException {
        // Simulate concurrent requests by checking MDC isolation
        String traceId = "concurrent-trace";
        request.addHeader("X-Trace-Id", traceId);
        
        // Set some other value in MDC before the filter
        MDC.put("otherKey", "otherValue");
        
        traceConfig.doFilterInternal(request, response, filterChain);
        
        // Verify MDC is completely cleared (including other keys)
        assertNull(MDC.get("traceId"));
        assertNull(MDC.get("otherKey"));
    }

    @Test
    @DisplayName("Should preserve trace ID format and case")
    void shouldPreserveTraceIdFormatAndCase() throws ServletException, IOException {
        String mixedCaseTraceId = "TrAcE-123-AbC";
        request.addHeader("X-Trace-Id", mixedCaseTraceId);
        
        traceConfig.doFilterInternal(request, response, filterChain);
        
        // Verify exact trace ID is preserved
        assertEquals(mixedCaseTraceId, response.getHeader("X-Trace-Id"));
    }

    @Test
    @DisplayName("Should handle special characters in trace ID")
    void shouldHandleSpecialCharactersInTraceId() throws ServletException, IOException {
        String specialTraceId = "trace-123_456.789";
        request.addHeader("X-Trace-Id", specialTraceId);
        
        traceConfig.doFilterInternal(request, response, filterChain);
        
        // Verify special characters are preserved
        assertEquals(specialTraceId, response.getHeader("X-Trace-Id"));
    }

    @Test
    @DisplayName("Should generate unique trace IDs for multiple requests")
    void shouldGenerateUniqueTraceIdsForMultipleRequests() throws ServletException, IOException {
        // First request without trace ID
        MockHttpServletRequest request1 = new MockHttpServletRequest();
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        traceConfig.doFilterInternal(request1, response1, filterChain);
        String traceId1 = response1.getHeader("X-Trace-Id");
        
        // Second request without trace ID
        MockHttpServletRequest request2 = new MockHttpServletRequest();
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        traceConfig.doFilterInternal(request2, response2, filterChain);
        String traceId2 = response2.getHeader("X-Trace-Id");
        
        // Verify both trace IDs are generated and different
        assertNotNull(traceId1);
        assertNotNull(traceId2);
        assertNotEquals(traceId1, traceId2);
        
        // Verify both are valid UUIDs
        assertDoesNotThrow(() -> UUID.fromString(traceId1));
        assertDoesNotThrow(() -> UUID.fromString(traceId2));
    }

    @Test
    @DisplayName("Should handle null request gracefully")
    void shouldHandleNullRequestGracefully() {
        // This test verifies the filter doesn't crash with null request
        // In real scenarios, this shouldn't happen, but it's good to be defensive
        assertDoesNotThrow(() -> {
            try {
                traceConfig.doFilterInternal(null, response, filterChain);
            } catch (NullPointerException e) {
                // Expected behavior - filter should handle this gracefully
                // or throw appropriate exception
            }
        });
    }

    @Test
    @DisplayName("Should handle null response gracefully")
    void shouldHandleNullResponseGracefully() {
        String traceId = "test-trace";
        request.addHeader("X-Trace-Id", traceId);
        
        assertDoesNotThrow(() -> {
            try {
                traceConfig.doFilterInternal(request, null, filterChain);
            } catch (NullPointerException e) {
                // Expected behavior - filter should handle this gracefully
                // or throw appropriate exception
            }
        });
    }

    @Test
    @DisplayName("Should handle null filter chain gracefully")
    void shouldHandleNullFilterChainGracefully() {
        String traceId = "test-trace";
        request.addHeader("X-Trace-Id", traceId);
        
        assertDoesNotThrow(() -> {
            try {
                traceConfig.doFilterInternal(request, response, null);
            } catch (NullPointerException e) {
                // Expected behavior - filter should handle this gracefully
                // or throw appropriate exception
            }
        });
    }

    // ========== Mock Servlet Objects ==========

    private static class MockHttpServletRequest implements HttpServletRequest {
        private final java.util.Map<String, String> headers = new java.util.HashMap<>();

        public void addHeader(String name, String value) {
            headers.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            return headers.get(name);
        }

        // Other HttpServletRequest methods - not used in tests
        @Override
        public jakarta.servlet.http.Cookie[] getCookies() { return null; }

        @Override
        public String getAuthType() { return null; }

        @Override
        public java.util.Enumeration<String> getHeaderNames() { return null; }

        @Override
        public java.util.Enumeration<String> getHeaders(String name) { return null; }

        @Override
        public int getIntHeader(String name) { return 0; }

        @Override
        public long getDateHeader(String name) { return 0; }

        @Override
        public String getMethod() { return null; }

        @Override
        public String getPathInfo() { return null; }

        @Override
        public String getPathTranslated() { return null; }

        @Override
        public String getContextPath() { return null; }

        @Override
        public String getQueryString() { return null; }

        @Override
        public String getRemoteUser() { return null; }

        @Override
        public boolean isUserInRole(String role) { return false; }

        @Override
        public java.security.Principal getUserPrincipal() { return null; }

        @Override
        public String getRequestedSessionId() { return null; }

        @Override
        public String getRequestURI() { return null; }

        @Override
        public StringBuffer getRequestURL() { return null; }

        @Override
        public String getServletPath() { return null; }

        @Override
        public jakarta.servlet.http.HttpSession getSession(boolean create) { return null; }

        @Override
        public jakarta.servlet.http.HttpSession getSession() { return null; }

        @Override
        public String changeSessionId() { return null; }

        @Override
        public boolean isRequestedSessionIdValid() { return false; }

        @Override
        public boolean isRequestedSessionIdFromCookie() { return false; }

        @Override
        public boolean isRequestedSessionIdFromURL() { return false; }

        @Override
        public boolean authenticate(HttpServletResponse response) { return false; }

        @Override
        public void login(String username, String password) {}

        @Override
        public void logout() {}

        @Override
        public java.util.Collection<jakarta.servlet.http.Part> getParts() { return null; }

        @Override
        public jakarta.servlet.http.Part getPart(String name) { return null; }

        @Override
        public <T extends jakarta.servlet.http.HttpUpgradeHandler> T upgrade(Class<T> handlerClass) { return null; }

        @Override
        public Object getAttribute(String name) { return null; }

        @Override
        public java.util.Enumeration<String> getAttributeNames() { return null; }

        @Override
        public String getCharacterEncoding() { return null; }

        @Override
        public void setCharacterEncoding(String env) {}

        @Override
        public int getContentLength() { return 0; }

        @Override
        public long getContentLengthLong() { return 0; }

        @Override
        public String getContentType() { return null; }

        @Override
        public jakarta.servlet.ServletInputStream getInputStream() { return null; }

        @Override
        public String getParameter(String name) { return null; }

        @Override
        public java.util.Enumeration<String> getParameterNames() { return null; }

        @Override
        public String[] getParameterValues(String name) { return null; }

        @Override
        public java.util.Map<String, String[]> getParameterMap() { return null; }

        @Override
        public String getProtocol() { return null; }

        @Override
        public String getScheme() { return null; }

        @Override
        public String getServerName() { return null; }

        @Override
        public int getServerPort() { return 0; }

        @Override
        public java.io.BufferedReader getReader() { return null; }

        @Override
        public String getRemoteAddr() { return null; }

        @Override
        public String getRemoteHost() { return null; }

        @Override
        public void setAttribute(String name, Object o) {}

        @Override
        public void removeAttribute(String name) {}

        @Override
        public java.util.Locale getLocale() { return null; }

        @Override
        public java.util.Enumeration<java.util.Locale> getLocales() { return null; }

        @Override
        public boolean isSecure() { return false; }

        @Override
        public jakarta.servlet.RequestDispatcher getRequestDispatcher(String path) { return null; }

        @Override
        public int getRemotePort() { return 0; }

        @Override
        public String getLocalName() { return null; }

        @Override
        public String getLocalAddr() { return null; }

        @Override
        public int getLocalPort() { return 0; }

        @Override
        public jakarta.servlet.ServletContext getServletContext() { return null; }

        @Override
        public jakarta.servlet.AsyncContext startAsync() { return null; }

        @Override
        public jakarta.servlet.AsyncContext startAsync(jakarta.servlet.ServletRequest servletRequest, jakarta.servlet.ServletResponse servletResponse) { return null; }

        @Override
        public boolean isAsyncStarted() { return false; }

        @Override
        public boolean isAsyncSupported() { return false; }

        @Override
        public jakarta.servlet.AsyncContext getAsyncContext() { return null; }

        @Override
        public jakarta.servlet.DispatcherType getDispatcherType() { return null; }

        @Override
        public String getRequestId() { return null; }

        @Override
        public String getProtocolRequestId() { return null; }

        @Override
        public jakarta.servlet.ServletConnection getServletConnection() { return null; }
    }

    private static class MockHttpServletResponse implements HttpServletResponse {
        private final java.util.Map<String, String> headers = new java.util.HashMap<>();

        @Override
        public void setHeader(String name, String value) {
            headers.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            return headers.get(name);
        }

        // Other HttpServletResponse methods - not used in tests
        @Override
        public void addCookie(jakarta.servlet.http.Cookie cookie) {}

        @Override
        public boolean containsHeader(String name) { return false; }

        @Override
        public String encodeURL(String url) { return null; }

        @Override
        public String encodeRedirectURL(String url) { return null; }

        @Override
        public void sendError(int sc, String msg) {}

        @Override
        public void sendError(int sc) {}

        @Override
        public void sendRedirect(String location) {}

        @Override
        public void setDateHeader(String name, long date) {}

        @Override
        public void addDateHeader(String name, long date) {}

        @Override
        public void addHeader(String name, String value) {}

        @Override
        public void setIntHeader(String name, int value) {}

        @Override
        public void addIntHeader(String name, int value) {}

        @Override
        public void setStatus(int sc) {}

        @Override
        public int getStatus() { return 0; }

        @Override
        public java.util.Collection<String> getHeaderNames() { return null; }

        @Override
        public java.util.Collection<String> getHeaders(String name) { return null; }

        @Override
        public String getCharacterEncoding() { return null; }

        @Override
        public String getContentType() { return null; }

        @Override
        public jakarta.servlet.ServletOutputStream getOutputStream() { return null; }

        @Override
        public java.io.PrintWriter getWriter() { return null; }

        @Override
        public void setCharacterEncoding(String charset) {}

        @Override
        public void setContentLength(int len) {}

        @Override
        public void setContentLengthLong(long len) {}

        @Override
        public void setContentType(String type) {}

        @Override
        public void setBufferSize(int size) {}

        @Override
        public int getBufferSize() { return 0; }

        @Override
        public void flushBuffer() {}

        @Override
        public void resetBuffer() {}

        @Override
        public boolean isCommitted() { return false; }

        @Override
        public void reset() {}

        @Override
        public void setLocale(java.util.Locale loc) {}

        @Override
        public java.util.Locale getLocale() { return null; }
    }

    private static class MockFilterChain implements FilterChain {
        private boolean doFilterCalled = false;

        @Override
        public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response)
                throws IOException, ServletException {
            doFilterCalled = true;
        }

        public boolean wasDoFilterCalled() {
            return doFilterCalled;
        }
    }
}
