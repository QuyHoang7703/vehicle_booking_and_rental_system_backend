package com.pbl6.VehicleBookingRental.user.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.*;

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
    private double discountPercentage;
    private double voucherDiscount;
    private double priceTotal;
    private String departureLocation;
    private String arrivalLocation;
    private LocalTime departureTime;
    private LocalDate departureDate;
    private Duration journeyDuration;
    private Instant arrivalTime;
    private int busTripScheduleId;
    private Instant orderDate;
    private Integer voucherId;
    private String key;

}
