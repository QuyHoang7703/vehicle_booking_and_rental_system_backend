package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalStatisticDTO;

import java.util.List;
import java.util.Map;

public interface VehicleRentalStatisticService {
    public List<VehicleRentalStatisticDTO> statisticFromLocationOrVehicleType(String location,String vehicleType);
    public List<VehicleRentalStatisticDTO> statisticByDate(String startDate,String endDate);
    public Map<Integer, Double> calculateMonthlyRevenue(int year);
}
