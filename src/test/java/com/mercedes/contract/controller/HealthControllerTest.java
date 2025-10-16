package com.mercedes.contract.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HealthController
 * Tests health check endpoints with various database states
 * No Spring context - pure unit tests with mock DataSource
 */
class HealthControllerTest {

    private HealthController healthController;
    private MockDataSource mockDataSource;

    @BeforeEach
    void setUp() {
        healthController = new HealthController();
        mockDataSource = new MockDataSource();
        
        // Use reflection to set the private DataSource field
        try {
            var field = HealthController.class.getDeclaredField("dataSource");
            field.setAccessible(true);
            field.set(healthController, mockDataSource);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock DataSource", e);
        }
    }

    // ========== Unit Tests for /health/ready endpoint ==========

    @Test
    @DisplayName("Should return UP status when database is healthy")
    void shouldReturnUpStatusWhenDatabaseIsHealthy() {
        mockDataSource.setHealthy(true);
        
        ResponseEntity<Map<String, Object>> response = healthController.readiness();
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("UP", body.get("status"));
        assertEquals("UP", body.get("database"));
        assertEquals("Service is ready", body.get("message"));
    }

    @Test
    @DisplayName("Should return DOWN status when database connection is invalid")
    void shouldReturnDownStatusWhenDatabaseConnectionIsInvalid() {
        mockDataSource.setHealthy(false);
        
        ResponseEntity<Map<String, Object>> response = healthController.readiness();
        
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("DOWN", body.get("status"));
        assertEquals("DOWN", body.get("database"));
        assertEquals("Database connection invalid", body.get("message"));
    }

    @Test
    @DisplayName("Should return DOWN status when database connection throws exception")
    void shouldReturnDownStatusWhenDatabaseConnectionThrowsException() {
        mockDataSource.setThrowException(true);
        
        ResponseEntity<Map<String, Object>> response = healthController.readiness();
        
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("DOWN", body.get("status"));
        assertEquals("DOWN", body.get("database"));
        assertEquals("Database connection failed", body.get("message"));
        assertNotNull(body.get("error"));
    }

    // ========== Unit Tests for /health/live endpoint ==========

    @Test
    @DisplayName("Should always return UP status for liveness probe")
    void shouldAlwaysReturnUpStatusForLivenessProbe() {
        // Even when database is down, liveness should be UP
        mockDataSource.setHealthy(false);
        
        ResponseEntity<Map<String, Object>> response = healthController.liveness();
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("UP", body.get("status"));
        assertEquals("Service is alive", body.get("message"));
        assertNotNull(body.get("timestamp"));
        assertTrue(body.get("timestamp") instanceof Long);
    }

    @Test
    @DisplayName("Should return current timestamp in liveness response")
    void shouldReturnCurrentTimestampInLivenessResponse() {
        long beforeCall = System.currentTimeMillis();
        
        ResponseEntity<Map<String, Object>> response = healthController.liveness();
        
        long afterCall = System.currentTimeMillis();
        
        Map<String, Object> body = response.getBody();
        Long timestamp = (Long) body.get("timestamp");
        
        assertTrue(timestamp >= beforeCall);
        assertTrue(timestamp <= afterCall);
    }

    // ========== Unit Tests for /v1/contract/health endpoint ==========

    @Test
    @DisplayName("Should return healthy contract service status when database is UP")
    void shouldReturnHealthyContractServiceStatusWhenDatabaseIsUp() {
        mockDataSource.setHealthy(true);
        
        ResponseEntity<Map<String, Object>> response = healthController.contractHealth();
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("contract-service", body.get("service"));
        assertEquals("UP", body.get("status"));
        assertEquals("UP", body.get("database"));
        assertEquals("contract", body.get("capability"));
        assertEquals("1.0.0", body.get("version"));
        assertEquals("Contract service is healthy", body.get("message"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("Should return unhealthy contract service status when database is invalid")
    void shouldReturnUnhealthyContractServiceStatusWhenDatabaseIsInvalid() {
        mockDataSource.setHealthy(false);
        
        ResponseEntity<Map<String, Object>> response = healthController.contractHealth();
        
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("contract-service", body.get("service"));
        assertEquals("DOWN", body.get("status"));
        assertEquals("DOWN", body.get("database"));
        assertEquals("contract", body.get("capability"));
        assertEquals("1.0.0", body.get("version"));
        assertEquals("Contract service is unhealthy - database issues", body.get("message"));
    }

    @Test
    @DisplayName("Should return unhealthy contract service status when database throws exception")
    void shouldReturnUnhealthyContractServiceStatusWhenDatabaseThrowsException() {
        mockDataSource.setThrowException(true);
        
        ResponseEntity<Map<String, Object>> response = healthController.contractHealth();
        
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("contract-service", body.get("service"));
        assertEquals("DOWN", body.get("status"));
        assertEquals("DOWN", body.get("database"));
        assertEquals("contract", body.get("capability"));
        assertEquals("Contract service is unhealthy", body.get("message"));
        assertEquals("Database connection failed", body.get("error"));
    }

    @Test
    @DisplayName("Should include all required fields in contract health response")
    void shouldIncludeAllRequiredFieldsInContractHealthResponse() {
        mockDataSource.setHealthy(true);
        
        ResponseEntity<Map<String, Object>> response = healthController.contractHealth();
        
        Map<String, Object> body = response.getBody();
        
        // Verify all required fields are present
        assertTrue(body.containsKey("service"));
        assertTrue(body.containsKey("status"));
        assertTrue(body.containsKey("database"));
        assertTrue(body.containsKey("capability"));
        assertTrue(body.containsKey("version"));
        assertTrue(body.containsKey("timestamp"));
        assertTrue(body.containsKey("message"));
        
        // Verify field values
        assertEquals("contract-service", body.get("service"));
        assertEquals("contract", body.get("capability"));
        assertEquals("1.0.0", body.get("version"));
        assertNotNull(body.get("timestamp"));
        assertTrue(body.get("timestamp") instanceof Long);
    }

    // ========== Mock DataSource Implementation ==========

    private static class MockDataSource implements DataSource {
        private boolean healthy = true;
        private boolean throwException = false;
        
        public void setHealthy(boolean healthy) {
            this.healthy = healthy;
        }
        
        public void setThrowException(boolean throwException) {
            this.throwException = throwException;
        }
        
        @Override
        public Connection getConnection() throws SQLException {
            if (throwException) {
                throw new SQLException("Mock database connection failed");
            }
            return new MockConnection(healthy);
        }
        
        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return getConnection();
        }
        
        // Other DataSource methods - not used in tests
        @Override
        public java.io.PrintWriter getLogWriter() throws SQLException { return null; }
        
        @Override
        public void setLogWriter(java.io.PrintWriter out) throws SQLException {}
        
        @Override
        public void setLoginTimeout(int seconds) throws SQLException {}
        
        @Override
        public int getLoginTimeout() throws SQLException { return 0; }
        
        @Override
        public java.util.logging.Logger getParentLogger() { return null; }
        
        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException { return null; }
        
        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
    }

    private static class MockConnection implements Connection {
        private final boolean valid;
        
        public MockConnection(boolean valid) {
            this.valid = valid;
        }
        
        @Override
        public boolean isValid(int timeout) throws SQLException {
            return valid;
        }
        
        @Override
        public void close() throws SQLException {
            // Mock implementation - do nothing
        }
        
        // Other Connection methods - not used in tests, return defaults
        @Override
        public java.sql.Statement createStatement() throws SQLException { return null; }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException { return null; }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql) throws SQLException { return null; }
        
        @Override
        public String nativeSQL(String sql) throws SQLException { return null; }
        
        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {}
        
        @Override
        public boolean getAutoCommit() throws SQLException { return false; }
        
        @Override
        public void commit() throws SQLException {}
        
        @Override
        public void rollback() throws SQLException {}
        
        @Override
        public boolean isClosed() throws SQLException { return false; }
        
        @Override
        public java.sql.DatabaseMetaData getMetaData() throws SQLException { return null; }
        
        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {}
        
        @Override
        public boolean isReadOnly() throws SQLException { return false; }
        
        @Override
        public void setCatalog(String catalog) throws SQLException {}
        
        @Override
        public String getCatalog() throws SQLException { return null; }
        
        @Override
        public void setTransactionIsolation(int level) throws SQLException {}
        
        @Override
        public int getTransactionIsolation() throws SQLException { return 0; }
        
        @Override
        public java.sql.SQLWarning getWarnings() throws SQLException { return null; }
        
        @Override
        public void clearWarnings() throws SQLException {}
        
        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException { return null; }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return null; }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return null; }
        
        @Override
        public java.util.Map<String, Class<?>> getTypeMap() throws SQLException { return null; }
        
        @Override
        public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException {}
        
        @Override
        public void setHoldability(int holdability) throws SQLException {}
        
        @Override
        public int getHoldability() throws SQLException { return 0; }
        
        @Override
        public java.sql.Savepoint setSavepoint() throws SQLException { return null; }
        
        @Override
        public java.sql.Savepoint setSavepoint(String name) throws SQLException { return null; }
        
        @Override
        public void rollback(java.sql.Savepoint savepoint) throws SQLException {}
        
        @Override
        public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException {}
        
        @Override
        public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return null; }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return null; }
        
        @Override
        public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return null; }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException { return null; }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException { return null; }
        
        @Override
        public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException { return null; }
        
        @Override
        public java.sql.Clob createClob() throws SQLException { return null; }
        
        @Override
        public java.sql.Blob createBlob() throws SQLException { return null; }
        
        @Override
        public java.sql.NClob createNClob() throws SQLException { return null; }
        
        @Override
        public java.sql.SQLXML createSQLXML() throws SQLException { return null; }
        
        @Override
        public void setClientInfo(String name, String value) {}
        
        @Override
        public void setClientInfo(java.util.Properties properties) {}
        
        @Override
        public String getClientInfo(String name) { return null; }
        
        @Override
        public java.util.Properties getClientInfo() { return null; }
        
        @Override
        public java.sql.Array createArrayOf(String typeName, Object[] elements) throws SQLException { return null; }
        
        @Override
        public java.sql.Struct createStruct(String typeName, Object[] attributes) throws SQLException { return null; }
        
        @Override
        public void setSchema(String schema) throws SQLException {}
        
        @Override
        public String getSchema() throws SQLException { return null; }
        
        @Override
        public void abort(java.util.concurrent.Executor executor) throws SQLException {}
        
        @Override
        public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) throws SQLException {}
        
        @Override
        public int getNetworkTimeout() throws SQLException { return 0; }
        
        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException { return null; }
        
        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
    }
}
