package com.pbl6.VehicleBookingRental.user.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CustomInstantSerializer extends JsonSerializer<Instant> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")
            .withZone(ZoneId.systemDefault());


    @Override
    public void serialize(Instant instant, JsonGenerator json, SerializerProvider provider) throws IOException {
        if (instant != null) {
            // Chuyển Instant thành LocalDateTime theo định dạng mong muốn
            json.writeString(FORMATTER.format(instant));

        } else {
            json.writeNull(); // Xử lý trường hợp giá trị là null
        }
    }
}

