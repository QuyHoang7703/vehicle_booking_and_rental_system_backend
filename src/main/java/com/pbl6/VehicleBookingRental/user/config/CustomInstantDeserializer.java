package com.pbl6.VehicleBookingRental.user.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CustomInstantDeserializer extends JsonDeserializer<Instant> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");

    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String dateString = parser.getText();
        // Parse chuỗi thành LocalDateTime và sau đó chuyển thành Instant
        return LocalDateTime.parse(dateString, FORMATTER).toInstant(ZoneOffset.UTC);
    }
}
