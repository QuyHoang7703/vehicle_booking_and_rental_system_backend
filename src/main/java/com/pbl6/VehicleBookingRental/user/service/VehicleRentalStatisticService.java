package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.dto.response.statistic.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalStatisticDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;

import java.util.List;
import java.util.Map;

public interface VehicleRentalStatisticService {
    public List<VehicleRentalStatisticDTO> statisticByDate(String location,String vehicleType,String startDate,String endDate);
    public ResultStatisticDTO calculateMonthlyRevenue(Integer year) throws ApplicationException;
    public Map<Integer,Double> calculateRevenueByYear(List<Integer> years)throws ApplicationException;
    public List<VehicleRentalStatisticDTO> statisticFromLocationOrVehicleTypeByDate(String location, String vehicleType, String startDate, String endDate) ;
    public List<VehicleRentalStatisticDTO> statisticFromLocationOrVehicleTypeByMonthAndYear(String location, String vehicleType, int month , int year) ;
    public List<VehicleRentalStatisticDTO> statisticFromLocationOrVehicleTypeByYear(String location, String vehicleType, List<Integer> year) throws ApplicationException ;

}
