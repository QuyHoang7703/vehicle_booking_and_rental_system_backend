package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalStatisticDTO;

import java.util.List;

public interface VehicleRentalStatisticService {
    public List<VehicleRentalStatisticDTO> statisticFromLocationOrVehicleType(String location,String vehicleType);
}
