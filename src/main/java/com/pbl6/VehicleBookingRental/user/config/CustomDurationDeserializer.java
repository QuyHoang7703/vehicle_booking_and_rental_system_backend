package com.pbl6.VehicleBookingRental.user.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Duration;
// Change JSON to java object
// Use in case to create an object
public class CustomDurationDeserializer extends JsonDeserializer<Duration> {
    @Override
    public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String value = jsonParser.getText().trim();
        // Check value with suitable format "HH:mm"
        if(value.matches("\\d{2}h:\\d{2}m")){
            String[] parts = value.split(":");
            int hours = Integer.parseInt(parts[0].replace("h", ""));
            int minutes = Integer.parseInt(parts[1].replace("m", ""));
            return Duration.ofHours(hours).plusMinutes(minutes);
        }
        // Nếu chuỗi không phải định dạng "HH:mm", ném ngoại lệ
        throw new IOException("Invalid duration format, expected HH:mm");
    }
}
