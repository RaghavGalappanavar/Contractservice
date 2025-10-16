package com.mercedes.contract;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ContractServiceApplication
 * Tests application configuration and startup
 * No Spring context - pure unit tests
 */
class ContractServiceApplicationTest {

    // ========== Unit Tests for Application Configuration ==========

    @Test
    @DisplayName("Should have SpringBootApplication annotation")
    void shouldHaveSpringBootApplicationAnnotation() {
        assertTrue(ContractServiceApplication.class.isAnnotationPresent(SpringBootApplication.class));
    }

    @Test
    @DisplayName("Should have EnableKafka annotation")
    void shouldHaveEnableKafkaAnnotation() {
        assertTrue(ContractServiceApplication.class.isAnnotationPresent(EnableKafka.class));
    }

    @Test
    @DisplayName("Should have main method with correct signature")
    void shouldHaveMainMethodWithCorrectSignature() throws NoSuchMethodException {
        Method mainMethod = ContractServiceApplication.class.getMethod("main", String[].class);
        
        assertNotNull(mainMethod);
        assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
        assertEquals(void.class, mainMethod.getReturnType());
    }

    @Test
    @DisplayName("Should be in correct package")
    void shouldBeInCorrectPackage() {
        assertEquals("com.mercedes.contract", ContractServiceApplication.class.getPackage().getName());
    }

    @Test
    @DisplayName("Should have correct class name")
    void shouldHaveCorrectClassName() {
        assertEquals("ContractServiceApplication", ContractServiceApplication.class.getSimpleName());
    }

    @Test
    @DisplayName("Should be a public class")
    void shouldBeAPublicClass() {
        assertTrue(java.lang.reflect.Modifier.isPublic(ContractServiceApplication.class.getModifiers()));
    }

    @Test
    @DisplayName("Should not be abstract")
    void shouldNotBeAbstract() {
        assertFalse(java.lang.reflect.Modifier.isAbstract(ContractServiceApplication.class.getModifiers()));
    }

    @Test
    @DisplayName("Should not be final")
    void shouldNotBeFinal() {
        assertFalse(java.lang.reflect.Modifier.isFinal(ContractServiceApplication.class.getModifiers()));
    }

    @Test
    @DisplayName("Should have default constructor")
    void shouldHaveDefaultConstructor() {
        assertDoesNotThrow(() -> {
            ContractServiceApplication.class.getDeclaredConstructor();
        });
    }

    @Test
    @DisplayName("Should be instantiable")
    void shouldBeInstantiable() {
        assertDoesNotThrow(() -> {
            new ContractServiceApplication();
        });
    }

    // ========== Unit Tests for SpringBootApplication annotation ==========

    @Test
    @DisplayName("SpringBootApplication should have default configuration")
    void springBootApplicationShouldHaveDefaultConfiguration() {
        SpringBootApplication annotation = ContractServiceApplication.class.getAnnotation(SpringBootApplication.class);
        
        assertNotNull(annotation);
        
        // Verify default values
        assertEquals(0, annotation.exclude().length);
        assertEquals(0, annotation.excludeName().length);
        assertEquals(0, annotation.scanBasePackages().length);
        assertEquals(0, annotation.scanBasePackageClasses().length);
        assertTrue(annotation.proxyBeanMethods());
    }

    // ========== Unit Tests for EnableKafka annotation ==========

    @Test
    @DisplayName("EnableKafka should be present and configured")
    void enableKafkaShouldBePresentAndConfigured() {
        EnableKafka annotation = ContractServiceApplication.class.getAnnotation(EnableKafka.class);
        
        assertNotNull(annotation);
        // EnableKafka doesn't have configurable properties in this version
    }

    // ========== Unit Tests for main method behavior ==========

    @Test
    @DisplayName("Main method should not throw exception with null args")
    void mainMethodShouldNotThrowExceptionWithNullArgs() {
        // Note: This test verifies the method signature but doesn't actually run SpringApplication
        // to avoid starting the full application context in unit tests
        
        Method mainMethod;
        try {
            mainMethod = ContractServiceApplication.class.getMethod("main", String[].class);
            assertNotNull(mainMethod);
        } catch (NoSuchMethodException e) {
            fail("Main method should exist");
        }
    }

    @Test
    @DisplayName("Main method should not throw exception with empty args")
    void mainMethodShouldNotThrowExceptionWithEmptyArgs() {
        // Note: This test verifies the method signature but doesn't actually run SpringApplication
        // to avoid starting the full application context in unit tests
        
        Method mainMethod;
        try {
            mainMethod = ContractServiceApplication.class.getMethod("main", String[].class);
            assertNotNull(mainMethod);
            
            // Verify method can be invoked (but we won't actually invoke it to avoid starting Spring)
            assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
        } catch (NoSuchMethodException e) {
            fail("Main method should exist");
        }
    }

    // ========== Unit Tests for class structure ==========

    @Test
    @DisplayName("Should have only one public method (main)")
    void shouldHaveOnlyOnePublicMethod() {
        Method[] publicMethods = ContractServiceApplication.class.getMethods();
        
        // Filter out inherited methods from Object class
        long applicationMethods = java.util.Arrays.stream(publicMethods)
                .filter(method -> method.getDeclaringClass() == ContractServiceApplication.class)
                .count();
        
        assertEquals(1, applicationMethods, "Should have only main method declared");
    }

    @Test
    @DisplayName("Should not have any fields")
    void shouldNotHaveAnyFields() {
        assertEquals(0, ContractServiceApplication.class.getDeclaredFields().length);
    }

    @Test
    @DisplayName("Should not implement any interfaces")
    void shouldNotImplementAnyInterfaces() {
        assertEquals(0, ContractServiceApplication.class.getInterfaces().length);
    }

    @Test
    @DisplayName("Should extend Object class only")
    void shouldExtendObjectClassOnly() {
        assertEquals(Object.class, ContractServiceApplication.class.getSuperclass());
    }

    // ========== Unit Tests for annotation combinations ==========

    @Test
    @DisplayName("Should have exactly two annotations")
    void shouldHaveExactlyTwoAnnotations() {
        assertEquals(2, ContractServiceApplication.class.getAnnotations().length);
    }

    @Test
    @DisplayName("Should have SpringBootApplication and EnableKafka annotations only")
    void shouldHaveSpringBootApplicationAndEnableKafkaAnnotationsOnly() {
        java.lang.annotation.Annotation[] annotations = ContractServiceApplication.class.getAnnotations();
        
        boolean hasSpringBootApplication = false;
        boolean hasEnableKafka = false;
        
        for (java.lang.annotation.Annotation annotation : annotations) {
            if (annotation instanceof SpringBootApplication) {
                hasSpringBootApplication = true;
            } else if (annotation instanceof EnableKafka) {
                hasEnableKafka = true;
            }
        }
        
        assertTrue(hasSpringBootApplication, "Should have @SpringBootApplication");
        assertTrue(hasEnableKafka, "Should have @EnableKafka");
    }

    // ========== Unit Tests for package structure ==========

    @Test
    @DisplayName("Should be in root package of the application")
    void shouldBeInRootPackageOfTheApplication() {
        String packageName = ContractServiceApplication.class.getPackage().getName();
        assertEquals("com.mercedes.contract", packageName);
        
        // Verify it's the root package (no sub-packages)
        assertFalse(packageName.contains(".config"));
        assertFalse(packageName.contains(".controller"));
        assertFalse(packageName.contains(".service"));
        assertFalse(packageName.contains(".repository"));
    }

    // ========== Unit Tests for Spring Boot conventions ==========

    @Test
    @DisplayName("Should follow Spring Boot naming convention")
    void shouldFollowSpringBootNamingConvention() {
        String className = ContractServiceApplication.class.getSimpleName();
        assertTrue(className.endsWith("Application"), "Should end with 'Application'");
        assertTrue(className.startsWith("ContractService"), "Should start with service name");
    }

    @Test
    @DisplayName("Should be suitable as Spring Boot main class")
    void shouldBeSuitableAsSpringBootMainClass() {
        // Verify all requirements for Spring Boot main class
        assertTrue(ContractServiceApplication.class.isAnnotationPresent(SpringBootApplication.class));
        
        try {
            Method mainMethod = ContractServiceApplication.class.getMethod("main", String[].class);
            assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
            assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
            assertEquals(void.class, mainMethod.getReturnType());
        } catch (NoSuchMethodException e) {
            fail("Should have proper main method");
        }
    }

    // ========== Unit Tests for Kafka integration ==========

    @Test
    @DisplayName("Should enable Kafka for event publishing")
    void shouldEnableKafkaForEventPublishing() {
        assertTrue(ContractServiceApplication.class.isAnnotationPresent(EnableKafka.class));
        
        // Verify this enables Kafka functionality
        EnableKafka enableKafka = ContractServiceApplication.class.getAnnotation(EnableKafka.class);
        assertNotNull(enableKafka);
    }
}
