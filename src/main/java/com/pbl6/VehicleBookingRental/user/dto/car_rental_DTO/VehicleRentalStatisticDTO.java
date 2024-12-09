package com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO;

import lombok.Data;

import java.time.Instant;
@Data
public class VehicleRentalStatisticDTO {
    private String location;
    private String vehicle_type;
    private int vehicleRentalAmount;
    private int canceledVehicleAmount;
    private Instant date;
}
