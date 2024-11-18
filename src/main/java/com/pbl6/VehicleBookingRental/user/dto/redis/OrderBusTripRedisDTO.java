package com.pbl6.VehicleBookingRental.user.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderBusTripRedisDTO {
    private int id;
    private int numberOfTicket;
    private double priceTotal;
    private LocalDate departureDate;
    private int account_Id;
    private int busTripScheduleId;
    private Instant orderDate;
    private String key;

}
