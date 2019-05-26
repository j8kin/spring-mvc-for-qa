package com.acme.banking.dbo.spring;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
// java -jar target/dbo-<tab>
@SpringBootApplication
@ImportResource("classpath:spring-context.xml")
public class Application {
    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();{
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}