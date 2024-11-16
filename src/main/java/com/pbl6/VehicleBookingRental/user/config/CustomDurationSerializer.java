package com.pbl6.VehicleBookingRental.user.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;
// Change java object (in database) to JSON
// Use in case to get data from database
public class CustomDurationSerializer extends JsonSerializer<Duration> {
    @Override
    public void serialize(Duration duration, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();
        String formattedDuration = String.format("%02d:%02d", hours, minutes);
        jsonGenerator.writeString(formattedDuration);
    }
}
