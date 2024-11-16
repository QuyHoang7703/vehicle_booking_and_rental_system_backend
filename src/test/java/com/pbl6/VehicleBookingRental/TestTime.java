package com.pbl6.VehicleBookingRental;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
@Slf4j
public class TestTime {
    @Test
    public void demo() {
        LocalDate testToday = LocalDate.of(2024, 11, 16);
        LocalDate testStartDay = LocalDate.of(2024, 11, 14);
        log.info("Test Today is Before Test Start Day: " + testToday.isBefore(testStartDay));

    }
}
