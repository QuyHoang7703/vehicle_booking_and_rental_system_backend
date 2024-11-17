package com.pbl6.VehicleBookingRental.user.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        // Đăng ký CustomInstantSerializer chỉ cho Instant
        module.addSerializer(Instant.class, new CustomInstantSerializer());
        module.addSerializer(Duration.class, new CustomDurationSerializer());
        module.addDeserializer(Duration.class, new CustomDurationDeserializer());
        module.addDeserializer(Instant.class, new CustomInstantDeserializer());

        objectMapper.registerModule(new JavaTimeModule()); // Đăng ký module cho Java 8 Time API
        objectMapper.registerModule(module);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


        return objectMapper;
    }
}

