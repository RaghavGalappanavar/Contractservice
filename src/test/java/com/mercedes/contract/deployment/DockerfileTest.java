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
 * Unit tests for Dockerfile configuration
 * Tests Docker build configuration and best practices
 * No Docker runtime - pure file content validation
 */
class DockerfileTest {

    private List<String> dockerfileLines;
    private static final String DOCKERFILE_PATH = "Dockerfile";

    @BeforeEach
    void setUp() throws IOException {
        Path dockerfilePath = Paths.get(DOCKERFILE_PATH);
        assertTrue(Files.exists(dockerfilePath), "Dockerfile should exist in project root");
        dockerfileLines = Files.readAllLines(dockerfilePath);
    }

    // ========== Unit Tests for Multi-stage Build ==========

    @Test
    @DisplayName("Should use multi-stage build with build and runtime stages")
    void shouldUseMultiStageBuildWithBuildAndRuntimeStages() {
        boolean hasBuildStage = dockerfileLines.stream()
                .anyMatch(line -> line.contains("FROM maven:") && line.contains("AS build"));
        boolean hasRuntimeStage = dockerfileLines.stream()
                .anyMatch(line -> line.contains("FROM eclipse-temurin:") && !line.contains("AS"));
        
        assertTrue(hasBuildStage, "Should have build stage with Maven");
        assertTrue(hasRuntimeStage, "Should have runtime stage with JRE");
    }

    @Test
    @DisplayName("Should use appropriate base images")
    void shouldUseAppropriateBaseImages() {
        boolean usesMavenForBuild = dockerfileLines.stream()
                .anyMatch(line -> line.contains("FROM maven:3.9.4-eclipse-temurin-17"));
        boolean usesJreForRuntime = dockerfileLines.stream()
                .anyMatch(line -> line.contains("FROM eclipse-temurin:17-jre-alpine"));
        
        assertTrue(usesMavenForBuild, "Should use Maven with Java 17 for build stage");
        assertTrue(usesJreForRuntime, "Should use JRE Alpine for runtime stage");
    }

    // ========== Unit Tests for Security Best Practices ==========

    @Test
    @DisplayName("Should create and use non-root user")
    void shouldCreateAndUseNonRootUser() {
        boolean createsUser = dockerfileLines.stream()
                .anyMatch(line -> line.contains("adduser") && line.contains("appuser"));
        boolean switchesToUser = dockerfileLines.stream()
                .anyMatch(line -> line.trim().equals("USER appuser"));
        
        assertTrue(createsUser, "Should create non-root user");
        assertTrue(switchesToUser, "Should switch to non-root user");
    }

    @Test
    @DisplayName("Should set proper file ownership")
    void shouldSetProperFileOwnership() {
        boolean setsOwnership = dockerfileLines.stream()
                .anyMatch(line -> line.contains("chown") && line.contains("appuser:appgroup"));
        
        assertTrue(setsOwnership, "Should set proper file ownership for non-root user");
    }

    // ========== Unit Tests for Build Optimization ==========

    @Test
    @DisplayName("Should copy pom.xml before source code for dependency caching")
    void shouldCopyPomXmlBeforeSourceCodeForDependencyCaching() {
        int pomCopyIndex = -1;
        int srcCopyIndex = -1;
        
        for (int i = 0; i < dockerfileLines.size(); i++) {
            String line = dockerfileLines.get(i);
            if (line.contains("COPY pom.xml")) {
                pomCopyIndex = i;
            }
            if (line.contains("COPY src")) {
                srcCopyIndex = i;
            }
        }
        
        assertTrue(pomCopyIndex != -1, "Should copy pom.xml");
        assertTrue(srcCopyIndex != -1, "Should copy src directory");
        assertTrue(pomCopyIndex < srcCopyIndex, "Should copy pom.xml before src for better caching");
    }

    @Test
    @DisplayName("Should download dependencies offline for caching")
    void shouldDownloadDependenciesOfflineForCaching() {
        boolean downloadsDependencies = dockerfileLines.stream()
                .anyMatch(line -> line.contains("mvn dependency:go-offline"));
        
        assertTrue(downloadsDependencies, "Should download dependencies offline for caching");
    }

    @Test
    @DisplayName("Should skip tests during build")
    void shouldSkipTestsDuringBuild() {
        boolean skipsTests = dockerfileLines.stream()
                .anyMatch(line -> line.contains("mvn clean package") && line.contains("-DskipTests"));
        
        assertTrue(skipsTests, "Should skip tests during Docker build");
    }

    // ========== Unit Tests for Runtime Configuration ==========

    @Test
    @DisplayName("Should expose correct port")
    void shouldExposeCorrectPort() {
        boolean exposesPort = dockerfileLines.stream()
                .anyMatch(line -> line.trim().equals("EXPOSE 8084"));
        
        assertTrue(exposesPort, "Should expose port 8084");
    }

    @Test
    @DisplayName("Should set appropriate JVM options")
    void shouldSetAppropriateJvmOptions() {
        boolean setsJavaOpts = dockerfileLines.stream()
                .anyMatch(line -> line.contains("JAVA_OPTS") && 
                         line.contains("-Xmx") && 
                         line.contains("-Xms") && 
                         line.contains("UseG1GC") &&
                         line.contains("UseContainerSupport"));
        
        assertTrue(setsJavaOpts, "Should set appropriate JVM options for container environment");
    }

    @Test
    @DisplayName("Should have proper entrypoint")
    void shouldHaveProperEntrypoint() {
        boolean hasEntrypoint = dockerfileLines.stream()
                .anyMatch(line -> line.contains("ENTRYPOINT") && 
                         line.contains("java") && 
                         line.contains("$JAVA_OPTS") && 
                         line.contains("app.jar"));
        
        assertTrue(hasEntrypoint, "Should have proper entrypoint with JVM options");
    }

    // ========== Unit Tests for Health Check ==========

    @Test
    @DisplayName("Should include health check configuration")
    void shouldIncludeHealthCheckConfiguration() {
        boolean hasHealthCheck = dockerfileLines.stream()
                .anyMatch(line -> line.contains("HEALTHCHECK"));
        
        assertTrue(hasHealthCheck, "Should include health check configuration");
    }

    @Test
    @DisplayName("Should configure health check with proper parameters")
    void shouldConfigureHealthCheckWithProperParameters() {
        String healthCheckLine = dockerfileLines.stream()
                .filter(line -> line.contains("HEALTHCHECK"))
                .findFirst()
                .orElse("");
        
        assertTrue(healthCheckLine.contains("--interval=30s"), "Should set health check interval");
        assertTrue(healthCheckLine.contains("--timeout=10s"), "Should set health check timeout");
        assertTrue(healthCheckLine.contains("--start-period=60s"), "Should set health check start period");
        assertTrue(healthCheckLine.contains("--retries=3"), "Should set health check retries");
    }

    @Test
    @DisplayName("Should use correct health check endpoint")
    void shouldUseCorrectHealthCheckEndpoint() {
        boolean usesCorrectEndpoint = dockerfileLines.stream()
                .anyMatch(line -> line.contains("http://localhost:8084/api/contract/health/live"));
        
        assertTrue(usesCorrectEndpoint, "Should use correct health check endpoint");
    }

    // ========== Unit Tests for Directory Structure ==========

    @Test
    @DisplayName("Should create contract storage directory")
    void shouldCreateContractStorageDirectory() {
        boolean createsDirectory = dockerfileLines.stream()
                .anyMatch(line -> line.contains("mkdir") && line.contains("/app/contracts"));
        
        assertTrue(createsDirectory, "Should create contract storage directory");
    }

    @Test
    @DisplayName("Should set working directory")
    void shouldSetWorkingDirectory() {
        boolean setsWorkdir = dockerfileLines.stream()
                .anyMatch(line -> line.trim().equals("WORKDIR /app"));
        
        assertTrue(setsWorkdir, "Should set working directory to /app");
    }

    // ========== Unit Tests for JAR Handling ==========

    @Test
    @DisplayName("Should copy JAR from build stage")
    void shouldCopyJarFromBuildStage() {
        boolean copiesJar = dockerfileLines.stream()
                .anyMatch(line -> line.contains("COPY --from=build") && 
                         line.contains("contract-service-*.jar") && 
                         line.contains("app.jar"));
        
        assertTrue(copiesJar, "Should copy JAR from build stage with wildcard pattern");
    }

    // ========== Unit Tests for Build Commands ==========

    @Test
    @DisplayName("Should use batch mode for Maven commands")
    void shouldUseBatchModeForMavenCommands() {
        boolean usesBatchMode = dockerfileLines.stream()
                .filter(line -> line.contains("mvn"))
                .allMatch(line -> line.contains("-B"));
        
        assertTrue(usesBatchMode, "Should use batch mode (-B) for all Maven commands");
    }

    // ========== Unit Tests for Layer Optimization ==========

    @Test
    @DisplayName("Should optimize Docker layers for caching")
    void shouldOptimizeDockerLayersForCaching() {
        // Verify the order of operations for optimal caching
        int workdirIndex = findLineIndex("WORKDIR /app");
        int pomCopyIndex = findLineIndex("COPY pom.xml");
        int dependencyIndex = findLineIndex("mvn dependency:go-offline");
        int srcCopyIndex = findLineIndex("COPY src");
        int buildIndex = findLineIndex("mvn clean package");
        
        assertTrue(workdirIndex < pomCopyIndex, "WORKDIR should come before COPY pom.xml");
        assertTrue(pomCopyIndex < dependencyIndex, "COPY pom.xml should come before dependency download");
        assertTrue(dependencyIndex < srcCopyIndex, "Dependency download should come before COPY src");
        assertTrue(srcCopyIndex < buildIndex, "COPY src should come before build");
    }

    @Test
    @DisplayName("Should minimize runtime image size")
    void shouldMinimizeRuntimeImageSize() {
        // Runtime stage should use Alpine for smaller size
        boolean usesAlpine = dockerfileLines.stream()
                .anyMatch(line -> line.contains("eclipse-temurin:17-jre-alpine"));
        
        assertTrue(usesAlpine, "Should use Alpine-based JRE for smaller runtime image");
    }

    // ========== Unit Tests for Comments and Documentation ==========

    @Test
    @DisplayName("Should include descriptive comments")
    void shouldIncludeDescriptiveComments() {
        long commentCount = dockerfileLines.stream()
                .filter(line -> line.trim().startsWith("#"))
                .count();
        
        assertTrue(commentCount >= 5, "Should include descriptive comments for maintainability");
    }

    @Test
    @DisplayName("Should document multi-stage build purpose")
    void shouldDocumentMultiStageBuildPurpose() {
        boolean documentsMultiStage = dockerfileLines.stream()
                .anyMatch(line -> line.contains("#") && 
                         (line.toLowerCase().contains("multi-stage") || 
                          line.toLowerCase().contains("build stage") || 
                          line.toLowerCase().contains("runtime stage")));
        
        assertTrue(documentsMultiStage, "Should document multi-stage build purpose");
    }

    // ========== Helper Methods ==========

    private int findLineIndex(String searchText) {
        for (int i = 0; i < dockerfileLines.size(); i++) {
            if (dockerfileLines.get(i).contains(searchText)) {
                return i;
            }
        }
        return -1;
    }
}
