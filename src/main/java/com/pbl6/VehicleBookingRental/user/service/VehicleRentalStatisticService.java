package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.dto.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalStatisticDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;

import java.util.List;
import java.util.Map;

public interface VehicleRentalStatisticService {
    public List<VehicleRentalStatisticDTO> statisticFromLocationOrVehicleType(String location,String vehicleType);
    public List<VehicleRentalStatisticDTO> statisticByDate(String startDate,String endDate);
    public ResultStatisticDTO calculateMonthlyRevenue(Integer year) throws ApplicationException;
}
