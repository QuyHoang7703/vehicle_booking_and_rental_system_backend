package com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class VehicleRentalStatisticDTO {
    private String location;
    private String vehicle_type;
    private int vehicleRentalAmount;
    private int canceledVehicleAmount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate date;
}
