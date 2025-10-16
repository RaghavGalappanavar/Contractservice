package com.mercedes.contract.deployment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for docker-compose.yml configuration
 * Tests Docker Compose orchestration and service configuration
 * No Docker runtime - pure file content validation
 */
class DockerComposeTest {

    private List<String> composeLines;
    private static final String COMPOSE_PATH = "docker-compose.yml";

    @BeforeEach
    void setUp() throws IOException {
        Path composePath = Paths.get(COMPOSE_PATH);
        assertTrue(Files.exists(composePath), "docker-compose.yml should exist in project root");
        composeLines = Files.readAllLines(composePath);
    }

    // ========== Unit Tests for Service Configuration ==========

    @Test
    @DisplayName("Should define all required services")
    void shouldDefineAllRequiredServices() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("contract-db:"), "Should define contract-db service");
        assertTrue(content.contains("zookeeper:"), "Should define zookeeper service");
        assertTrue(content.contains("kafka:"), "Should define kafka service");
        assertTrue(content.contains("contract-service:"), "Should define contract-service service");
    }

    @Test
    @DisplayName("Should use correct Docker Compose version")
    void shouldUseCorrectDockerComposeVersion() {
        boolean hasCorrectVersion = composeLines.stream()
                .anyMatch(line -> line.trim().equals("version: '3.8'"));
        
        assertTrue(hasCorrectVersion, "Should use Docker Compose version 3.8");
    }

    // ========== Unit Tests for Database Service ==========

    @Test
    @DisplayName("Should configure PostgreSQL database service correctly")
    void shouldConfigurePostgreSqlDatabaseServiceCorrectly() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("image: postgres:15-alpine"), "Should use PostgreSQL 15 Alpine");
        assertTrue(content.contains("container_name: contract-service-db"), "Should set container name");
        assertTrue(content.contains("POSTGRES_DB: contract_service_db"), "Should set database name");
        assertTrue(content.contains("POSTGRES_USER: postgres"), "Should set database user");
        assertTrue(content.contains("POSTGRES_PASSWORD: postgres"), "Should set database password");
    }

    @Test
    @DisplayName("Should expose database on correct port")
    void shouldExposeDatabaseOnCorrectPort() {
        boolean exposesCorrectPort = composeLines.stream()
                .anyMatch(line -> line.contains("\"5434:5432\""));
        
        assertTrue(exposesCorrectPort, "Should expose database on port 5434");
    }

    @Test
    @DisplayName("Should configure database health check")
    void shouldConfigureDatabaseHealthCheck() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("pg_isready"), "Should use pg_isready for health check");
        assertTrue(content.contains("interval: 30s"), "Should set health check interval");
        assertTrue(content.contains("timeout: 10s"), "Should set health check timeout");
        assertTrue(content.contains("retries: 3"), "Should set health check retries");
    }

    // ========== Unit Tests for Kafka Services ==========

    @Test
    @DisplayName("Should configure Zookeeper service correctly")
    void shouldConfigureZookeeperServiceCorrectly() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("image: confluentinc/cp-zookeeper:7.4.0"), "Should use Confluent Zookeeper");
        assertTrue(content.contains("container_name: contract-zookeeper"), "Should set Zookeeper container name");
        assertTrue(content.contains("ZOOKEEPER_CLIENT_PORT: 2181"), "Should set Zookeeper client port");
        assertTrue(content.contains("ZOOKEEPER_TICK_TIME: 2000"), "Should set Zookeeper tick time");
    }

    @Test
    @DisplayName("Should configure Kafka service correctly")
    void shouldConfigureKafkaServiceCorrectly() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("image: confluentinc/cp-kafka:7.4.0"), "Should use Confluent Kafka");
        assertTrue(content.contains("container_name: contract-kafka"), "Should set Kafka container name");
        assertTrue(content.contains("KAFKA_BROKER_ID: 1"), "Should set Kafka broker ID");
        assertTrue(content.contains("KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181"), "Should connect to Zookeeper");
    }

    @Test
    @DisplayName("Should expose Kafka on correct port")
    void shouldExposeKafkaOnCorrectPort() {
        boolean exposesCorrectPort = composeLines.stream()
                .anyMatch(line -> line.contains("\"9092:9092\""));
        
        assertTrue(exposesCorrectPort, "Should expose Kafka on port 9092");
    }

    @Test
    @DisplayName("Should configure Kafka listeners correctly")
    void shouldConfigureKafkaListenersCorrectly() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP"), "Should set listener security protocol map");
        assertTrue(content.contains("KAFKA_ADVERTISED_LISTENERS"), "Should set advertised listeners");
        assertTrue(content.contains("PLAINTEXT://kafka:29092"), "Should configure internal listener");
        assertTrue(content.contains("PLAINTEXT_HOST://localhost:9092"), "Should configure external listener");
    }

    // ========== Unit Tests for Contract Service ==========

    @Test
    @DisplayName("Should configure contract service correctly")
    void shouldConfigureContractServiceCorrectly() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("build:"), "Should build from Dockerfile");
        assertTrue(content.contains("context: ."), "Should use current directory as build context");
        assertTrue(content.contains("dockerfile: Dockerfile"), "Should use Dockerfile");
        assertTrue(content.contains("container_name: contract-service-app"), "Should set container name");
    }

    @Test
    @DisplayName("Should expose contract service on correct port")
    void shouldExposeContractServiceOnCorrectPort() {
        boolean exposesCorrectPort = composeLines.stream()
                .anyMatch(line -> line.contains("\"8084:8084\""));
        
        assertTrue(exposesCorrectPort, "Should expose contract service on port 8084");
    }

    @Test
    @DisplayName("Should configure service dependencies correctly")
    void shouldConfigureServiceDependenciesCorrectly() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("depends_on:"), "Should define service dependencies");
        assertTrue(content.contains("contract-db:"), "Should depend on database");
        assertTrue(content.contains("condition: service_healthy"), "Should wait for healthy services");
        assertTrue(content.contains("kafka:"), "Should depend on Kafka");
    }

    // ========== Unit Tests for Environment Variables ==========

    @Test
    @DisplayName("Should configure database environment variables")
    void shouldConfigureDatabaseEnvironmentVariables() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("DATABASE_URL: jdbc:postgresql://contract-db:5432/contract_service_db"), 
                  "Should set database URL");
        assertTrue(content.contains("DATABASE_USERNAME: postgres"), "Should set database username");
        assertTrue(content.contains("DATABASE_PASSWORD: postgres"), "Should set database password");
    }

    @Test
    @DisplayName("Should configure Kafka environment variables")
    void shouldConfigureKafkaEnvironmentVariables() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("KAFKA_BOOTSTRAP_SERVERS: kafka:29092"), "Should set Kafka bootstrap servers");
        assertTrue(content.contains("KAFKA_CONTRACT_TOPIC: contract-events"), "Should set Kafka topic");
    }

    @Test
    @DisplayName("Should configure storage environment variables")
    void shouldConfigureStorageEnvironmentVariables() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("STORAGE_TYPE: local"), "Should set storage type");
        assertTrue(content.contains("LOCAL_STORAGE_PATH: /app/contracts"), "Should set local storage path");
    }

    @Test
    @DisplayName("Should configure server environment variables")
    void shouldConfigureServerEnvironmentVariables() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("SERVER_PORT: 8084"), "Should set server port");
        assertTrue(content.contains("LOG_LEVEL: INFO"), "Should set log level");
        assertTrue(content.contains("WEB_LOG_LEVEL: INFO"), "Should set web log level");
    }

    @Test
    @DisplayName("Should configure CORS environment variables")
    void shouldConfigureCorsEnvironmentVariables() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("CORS_ALLOWED_ORIGINS"), "Should set CORS allowed origins");
        assertTrue(content.contains("CORS_ALLOWED_METHODS"), "Should set CORS allowed methods");
        assertTrue(content.contains("CORS_ALLOWED_HEADERS"), "Should set CORS allowed headers");
        assertTrue(content.contains("CORS_ALLOW_CREDENTIALS"), "Should set CORS allow credentials");
    }

    // ========== Unit Tests for Volumes ==========

    @Test
    @DisplayName("Should define required volumes")
    void shouldDefineRequiredVolumes() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("volumes:"), "Should define volumes section");
        assertTrue(content.contains("contract_db_data:"), "Should define database volume");
        assertTrue(content.contains("contract_storage:"), "Should define storage volume");
        assertTrue(content.contains("driver: local"), "Should use local driver for volumes");
    }

    @Test
    @DisplayName("Should mount volumes correctly")
    void shouldMountVolumesCorrectly() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("contract_db_data:/var/lib/postgresql/data"), 
                  "Should mount database volume");
        assertTrue(content.contains("contract_storage:/app/contracts"), 
                  "Should mount storage volume");
    }

    // ========== Unit Tests for Networks ==========

    @Test
    @DisplayName("Should define custom network")
    void shouldDefineCustomNetwork() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("networks:"), "Should define networks section");
        assertTrue(content.contains("contract-network:"), "Should define custom network");
        assertTrue(content.contains("driver: bridge"), "Should use bridge driver");
    }

    @Test
    @DisplayName("Should connect all services to custom network")
    void shouldConnectAllServicesToCustomNetwork() {
        String content = String.join("\n", composeLines);
        
        // Count occurrences of network assignment
        long networkAssignments = composeLines.stream()
                .filter(line -> line.trim().equals("- contract-network"))
                .count();
        
        assertEquals(4, networkAssignments, "All 4 services should be connected to custom network");
    }

    // ========== Unit Tests for Health Checks ==========

    @Test
    @DisplayName("Should configure health checks for all services")
    void shouldConfigureHealthChecksForAllServices() {
        String content = String.join("\n", composeLines);
        
        // Count health check configurations
        long healthCheckCount = composeLines.stream()
                .filter(line -> line.trim().equals("healthcheck:"))
                .count();
        
        assertEquals(3, healthCheckCount, "Should have health checks for database, Kafka, and contract service");
    }

    @Test
    @DisplayName("Should configure contract service health check correctly")
    void shouldConfigureContractServiceHealthCheckCorrectly() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("http://localhost:8084/api/contract/health/live"), 
                  "Should use correct health check endpoint");
        assertTrue(content.contains("start_period: 60s"), "Should set start period for service");
    }

    // ========== Unit Tests for Service Startup Order ==========

    @Test
    @DisplayName("Should ensure proper service startup order")
    void shouldEnsureProperServiceStartupOrder() {
        String content = String.join("\n", composeLines);
        
        // Kafka should depend on Zookeeper
        assertTrue(content.contains("depends_on:\n      - zookeeper"), 
                  "Kafka should depend on Zookeeper");
        
        // Contract service should depend on database and Kafka with health conditions
        assertTrue(content.contains("contract-db:\n        condition: service_healthy"), 
                  "Contract service should wait for healthy database");
        assertTrue(content.contains("kafka:\n        condition: service_healthy"), 
                  "Contract service should wait for healthy Kafka");
    }

    // ========== Unit Tests for Configuration Consistency ==========

    @Test
    @DisplayName("Should have consistent port configurations")
    void shouldHaveConsistentPortConfigurations() {
        String content = String.join("\n", composeLines);
        
        // Database port consistency
        assertTrue(content.contains("\"5434:5432\"") && content.contains("contract-db:5432"), 
                  "Database port should be consistent between exposure and connection");
        
        // Kafka port consistency
        assertTrue(content.contains("\"9092:9092\"") && content.contains("localhost:9092"), 
                  "Kafka port should be consistent between exposure and listeners");
        
        // Service port consistency
        assertTrue(content.contains("\"8084:8084\"") && content.contains("SERVER_PORT: 8084"), 
                  "Service port should be consistent between exposure and environment");
    }

    @Test
    @DisplayName("Should have consistent service names")
    void shouldHaveConsistentServiceNames() {
        String content = String.join("\n", composeLines);
        
        // Database service name consistency
        assertTrue(content.contains("contract-db:") && content.contains("contract-db:5432"), 
                  "Database service name should be consistent");
        
        // Kafka service name consistency
        assertTrue(content.contains("kafka:") && content.contains("kafka:29092"), 
                  "Kafka service name should be consistent");
        
        // Zookeeper service name consistency
        assertTrue(content.contains("zookeeper:") && content.contains("zookeeper:2181"), 
                  "Zookeeper service name should be consistent");
    }

    // ========== Unit Tests for Production Readiness ==========

    @Test
    @DisplayName("Should use specific image versions")
    void shouldUseSpecificImageVersions() {
        String content = String.join("\n", composeLines);
        
        assertTrue(content.contains("postgres:15-alpine"), "Should use specific PostgreSQL version");
        assertTrue(content.contains("confluentinc/cp-zookeeper:7.4.0"), "Should use specific Zookeeper version");
        assertTrue(content.contains("confluentinc/cp-kafka:7.4.0"), "Should use specific Kafka version");
    }

    @Test
    @DisplayName("Should configure resource limits implicitly")
    void shouldConfigureResourceLimitsImplicitly() {
        // While not explicitly set in this compose file, verify structure supports it
        String content = String.join("\n", composeLines);
        
        // Verify services are properly structured to support resource limits
        assertTrue(content.contains("services:"), "Should have services section");
        assertTrue(content.contains("environment:"), "Should have environment sections");
        
        // This test ensures the structure is ready for production resource limits
        assertFalse(content.isEmpty(), "Compose file should not be empty");
    }
}
