package com.pbl6.VehicleBookingRental.user.dto.redis;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderVehicleRentalRedisDTO {
    private String id;
    private String customerName;
    private String customerPhoneNumber;
    private int numberOfVehicles;
    private double price;
    private double priceTotal;
    private Instant start_rental_time;
    private Instant end_rental_time;
    private String pickup_location;
    private double voucher_value;
    private double voucher_percentage;
    private double car_deposit;
    private double reservation_fee;
    private Instant created_at;

    private int account_Id;
    private int vehicle_rental_service_id;
    private Integer voucherId;
    private String key;
}
