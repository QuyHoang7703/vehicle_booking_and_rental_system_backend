package com.pbl6.VehicleBookingRental.user.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderBusTripRedisDTO {
    private String id;
    private String customerName;
    private String customerPhoneNumber;
    private int account_Id;
    private int numberOfTicket;
    private double pricePerTicket;
    private double priceTotal;
    private String departureLocation;
    private String arrivalLocation;
    private LocalTime departureTime;
    private LocalDate departureDate;
    private Duration journeyDuration;
    private double discountPercentage;
    private int busTripScheduleId;
    private Instant orderDate;
    private String key;

}
