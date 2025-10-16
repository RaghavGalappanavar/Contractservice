package com.mercedes.contract;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main application class for Contract Service
 * Follows Spring Boot 3.2.x conventions
 */
@SpringBootApplication
@EnableKafka
public class ContractServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContractServiceApplication.class, args);
    }
}
