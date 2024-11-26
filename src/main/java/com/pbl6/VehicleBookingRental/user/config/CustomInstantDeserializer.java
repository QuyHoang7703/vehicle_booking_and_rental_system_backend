package com.pbl6.VehicleBookingRental.user.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CustomInstantDeserializer extends JsonDeserializer<Instant> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");

    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String dateString = parser.getText();
        // Parse chuỗi thành LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.parse(dateString, FORMATTER);
        // Chuyển đổi sang Instant với múi giờ "Asia/Ho_Chi_Minh" (GMT+7)
        return localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
    }
}
