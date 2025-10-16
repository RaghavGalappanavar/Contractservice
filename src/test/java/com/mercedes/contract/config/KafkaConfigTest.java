package com.mercedes.contract.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KafkaConfig
 * Tests Kafka configuration setup and bean creation
 * No Spring context - pure unit tests with reflection
 */
class KafkaConfigTest {

    private KafkaConfig kafkaConfig;

    @BeforeEach
    void setUp() {
        kafkaConfig = new KafkaConfig();
        // Set bootstrap servers using reflection
        setPrivateField(kafkaConfig, "bootstrapServers", "localhost:9092");
    }

    // ========== Unit Tests for producerFactory() method ==========

    @Test
    @DisplayName("Should create producer factory with correct configuration")
    void shouldCreateProducerFactoryWithCorrectConfiguration() {
        ProducerFactory<String, Object> producerFactory = kafkaConfig.producerFactory();
        
        assertNotNull(producerFactory);
        
        // Get configuration properties
        Map<String, Object> configProps = producerFactory.getConfigurationProperties();
        
        // Verify bootstrap servers
        assertEquals("localhost:9092", configProps.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        
        // Verify serializers
        assertEquals(StringSerializer.class, configProps.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(JsonSerializer.class, configProps.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }

    @Test
    @DisplayName("Should configure producer for reliability")
    void shouldConfigureProducerForReliability() {
        ProducerFactory<String, Object> producerFactory = kafkaConfig.producerFactory();
        Map<String, Object> configProps = producerFactory.getConfigurationProperties();
        
        // Verify reliability configurations
        assertEquals("all", configProps.get(ProducerConfig.ACKS_CONFIG));
        assertEquals(3, configProps.get(ProducerConfig.RETRIES_CONFIG));
        assertEquals(true, configProps.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
        assertEquals(1, configProps.get(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION));
    }

    @Test
    @DisplayName("Should disable type headers in JSON serializer")
    void shouldDisableTypeHeadersInJsonSerializer() {
        ProducerFactory<String, Object> producerFactory = kafkaConfig.producerFactory();
        Map<String, Object> configProps = producerFactory.getConfigurationProperties();
        
        // Verify type headers are disabled
        assertEquals(false, configProps.get(JsonSerializer.ADD_TYPE_INFO_HEADERS));
    }

    @Test
    @DisplayName("Should handle different bootstrap servers configuration")
    void shouldHandleDifferentBootstrapServersConfiguration() {
        // Test with different bootstrap servers
        setPrivateField(kafkaConfig, "bootstrapServers", "kafka1:9092,kafka2:9092,kafka3:9092");
        
        ProducerFactory<String, Object> producerFactory = kafkaConfig.producerFactory();
        Map<String, Object> configProps = producerFactory.getConfigurationProperties();
        
        assertEquals("kafka1:9092,kafka2:9092,kafka3:9092", 
                    configProps.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
    }

    @Test
    @DisplayName("Should handle null bootstrap servers gracefully")
    void shouldHandleNullBootstrapServersGracefully() {
        setPrivateField(kafkaConfig, "bootstrapServers", null);

        // This should throw an exception because null bootstrap servers is not valid
        assertThrows(NullPointerException.class, () -> {
            kafkaConfig.producerFactory();
        });
    }

    @Test
    @DisplayName("Should handle empty bootstrap servers")
    void shouldHandleEmptyBootstrapServers() {
        setPrivateField(kafkaConfig, "bootstrapServers", "");
        
        ProducerFactory<String, Object> producerFactory = kafkaConfig.producerFactory();
        Map<String, Object> configProps = producerFactory.getConfigurationProperties();
        
        assertEquals("", configProps.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
    }

    // ========== Unit Tests for kafkaTemplate() method ==========

    @Test
    @DisplayName("Should create KafkaTemplate with producer factory")
    void shouldCreateKafkaTemplateWithProducerFactory() {
        KafkaTemplate<String, Object> kafkaTemplate = kafkaConfig.kafkaTemplate();
        
        assertNotNull(kafkaTemplate);
        assertNotNull(kafkaTemplate.getProducerFactory());
    }

    @Test
    @DisplayName("Should create KafkaTemplate with same configuration as producer factory")
    void shouldCreateKafkaTemplateWithSameConfigurationAsProducerFactory() {
        ProducerFactory<String, Object> producerFactory = kafkaConfig.producerFactory();
        KafkaTemplate<String, Object> kafkaTemplate = kafkaConfig.kafkaTemplate();
        
        // Both should have the same configuration
        Map<String, Object> producerConfig = producerFactory.getConfigurationProperties();
        Map<String, Object> templateConfig = kafkaTemplate.getProducerFactory().getConfigurationProperties();
        
        assertEquals(producerConfig.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG),
                    templateConfig.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(producerConfig.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG),
                    templateConfig.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(producerConfig.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG),
                    templateConfig.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
    }

    @Test
    @DisplayName("Should create new KafkaTemplate instance on each call")
    void shouldCreateNewKafkaTemplateInstanceOnEachCall() {
        KafkaTemplate<String, Object> template1 = kafkaConfig.kafkaTemplate();
        KafkaTemplate<String, Object> template2 = kafkaConfig.kafkaTemplate();
        
        assertNotNull(template1);
        assertNotNull(template2);
        // Should be different instances (no @Bean singleton behavior in unit test)
        assertNotSame(template1, template2);
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("Should create complete Kafka configuration chain")
    void shouldCreateCompleteKafkaConfigurationChain() {
        // Test the complete chain: Config -> ProducerFactory -> KafkaTemplate
        ProducerFactory<String, Object> producerFactory = kafkaConfig.producerFactory();
        KafkaTemplate<String, Object> kafkaTemplate = kafkaConfig.kafkaTemplate();
        
        assertNotNull(producerFactory);
        assertNotNull(kafkaTemplate);
        assertNotNull(kafkaTemplate.getProducerFactory());
        
        // Verify the chain is properly connected
        Map<String, Object> configProps = kafkaTemplate.getProducerFactory().getConfigurationProperties();
        assertEquals("localhost:9092", configProps.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
    }

    @Test
    @DisplayName("Should maintain configuration consistency across multiple calls")
    void shouldMaintainConfigurationConsistencyAcrossMultipleCalls() {
        // Create multiple instances
        ProducerFactory<String, Object> factory1 = kafkaConfig.producerFactory();
        ProducerFactory<String, Object> factory2 = kafkaConfig.producerFactory();
        KafkaTemplate<String, Object> template1 = kafkaConfig.kafkaTemplate();
        KafkaTemplate<String, Object> template2 = kafkaConfig.kafkaTemplate();
        
        // All should have the same configuration
        Map<String, Object> config1 = factory1.getConfigurationProperties();
        Map<String, Object> config2 = factory2.getConfigurationProperties();
        Map<String, Object> config3 = template1.getProducerFactory().getConfigurationProperties();
        Map<String, Object> config4 = template2.getProducerFactory().getConfigurationProperties();
        
        assertEquals(config1.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG),
                    config2.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(config1.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG),
                    config3.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals(config1.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG),
                    config4.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
    }

    // ========== Edge Case Tests ==========

    @Test
    @DisplayName("Should handle special characters in bootstrap servers")
    void shouldHandleSpecialCharactersInBootstrapServers() {
        setPrivateField(kafkaConfig, "bootstrapServers", "kafka-cluster.example.com:9092");
        
        ProducerFactory<String, Object> producerFactory = kafkaConfig.producerFactory();
        Map<String, Object> configProps = producerFactory.getConfigurationProperties();
        
        assertEquals("kafka-cluster.example.com:9092", 
                    configProps.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
    }

    @Test
    @DisplayName("Should handle IPv6 addresses in bootstrap servers")
    void shouldHandleIpv6AddressesInBootstrapServers() {
        setPrivateField(kafkaConfig, "bootstrapServers", "[::1]:9092");
        
        ProducerFactory<String, Object> producerFactory = kafkaConfig.producerFactory();
        Map<String, Object> configProps = producerFactory.getConfigurationProperties();
        
        assertEquals("[::1]:9092", configProps.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
    }

    @Test
    @DisplayName("Should verify all required configuration properties are set")
    void shouldVerifyAllRequiredConfigurationPropertiesAreSet() {
        ProducerFactory<String, Object> producerFactory = kafkaConfig.producerFactory();
        Map<String, Object> configProps = producerFactory.getConfigurationProperties();
        
        // Verify all required properties are present
        assertTrue(configProps.containsKey(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertTrue(configProps.containsKey(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertTrue(configProps.containsKey(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
        assertTrue(configProps.containsKey(ProducerConfig.ACKS_CONFIG));
        assertTrue(configProps.containsKey(ProducerConfig.RETRIES_CONFIG));
        assertTrue(configProps.containsKey(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
        assertTrue(configProps.containsKey(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION));
        assertTrue(configProps.containsKey(JsonSerializer.ADD_TYPE_INFO_HEADERS));
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
}
