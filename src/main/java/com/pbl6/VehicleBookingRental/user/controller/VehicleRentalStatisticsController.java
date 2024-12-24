package com.pbl6.VehicleBookingRental.user.controller;


import com.pbl6.VehicleBookingRental.user.service.VehicleRentalStatisticService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicle-rental-statistic")
public class VehicleRentalStatisticsController {
    @Autowired
    private VehicleRentalStatisticService vehicleRentalStatisticService;
    @GetMapping("/statistic-from-location-or-vehicleType-by-date")
    public ResponseEntity<?> statisticByLocationOrVehicleTypeDate(@RequestParam(required = false,value = "location")String location,
                                                              @RequestParam(required = false,value = "vehicle_type")String vehicleTypeName,
                                                              @RequestParam("start_date") String startDate,@RequestParam("end_date")String endDate){
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRentalStatisticService.statisticFromLocationOrVehicleTypeByDate(location,vehicleTypeName,startDate,endDate));
    }
    @GetMapping("/statistic-from-location-or-vehicleType-by-month-and-year")
    public ResponseEntity<?> statisticByLocationOrVehicleTypeMonthAndYear(@RequestParam(required = false,value = "location")String location,
                                                              @RequestParam(required = false,value = "vehicle_type")String vehicleTypeName,
                                                              @RequestParam("month") int month,@RequestParam("end_date")int year){
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRentalStatisticService.statisticFromLocationOrVehicleTypeByMonthAndYear(location,vehicleTypeName,month,year));
    }
    @GetMapping("/statistic-from-location-or-vehicleType-by-date")
    public ResponseEntity<?> statisticByLocationOrVehicleTypeYear(@RequestParam(required = false,value = "location")String location,
                                                              @RequestParam(required = false,value = "vehicle_type")String vehicleTypeName,
                                                              @RequestParam("year") List<Integer> years) throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRentalStatisticService.statisticFromLocationOrVehicleTypeByYear(location,vehicleTypeName,years));
    }
    @GetMapping("/statistic-by-date")
    public ResponseEntity<?> statisticByDate(@RequestParam(required = false,value = "location")String location,
                                             @RequestParam(required = false,value = "vehicle_type")String vehicleTypeName,
                                             @RequestParam("start_date") String startDate,@RequestParam("end_date")String endDate)
    {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRentalStatisticService.statisticByDate(location,vehicleTypeName,startDate,endDate));
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('CAR_RENTAL_PARTNER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMonthlyVenueInYear(@RequestParam(value = "year", required = false) Integer year) throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRentalStatisticService.calculateMonthlyRevenue(year));
    }
    @PostMapping("/get-venue-by-year")
    public ResponseEntity<?> getVenueByYear(@RequestParam(value = "yearArrays",required = false) List<Integer> years) throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRentalStatisticService.calculateRevenueByYear(years));
    }
}
