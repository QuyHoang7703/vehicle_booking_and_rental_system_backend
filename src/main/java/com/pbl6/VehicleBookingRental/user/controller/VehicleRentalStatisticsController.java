package com.pbl6.VehicleBookingRental.user.controller;


import com.pbl6.VehicleBookingRental.user.service.VehicleRentalStatisticService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/statistic-by-location-or-vehicleType")
    public ResponseEntity<?> statisticByLocationOrVehicleType(@RequestParam(required = false,value = "location")String location,
                                                              @RequestParam(required = false,value = "vehicle_type")String vehicleType){
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRentalStatisticService.statisticFromLocationOrVehicleType(location,vehicleType));
    }
    @GetMapping("/statistic-by-date")
    public ResponseEntity<?> statisticByDate(@RequestParam("start_date") String startDate,@RequestParam("end_date")String endDate)
    {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRentalStatisticService.statisticByDate(startDate,endDate));
    }
    @GetMapping("/revenue")
    @PreAuthorize("hasRole('CAR_RENTAL_PARTNER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMonthlyVenueInYear(@RequestParam(value = "year", required = false) Integer year) throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRentalStatisticService.calculateMonthlyRevenue(year));
    }
    @PostMapping("/get-venue-by-year")
    public ResponseEntity<?> getVenueByYear(@RequestParam(value = "yearArrays",required = false) List<Integer> years) throws ApplicationException {
        System.out.println(years);
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRentalStatisticService.calculateRevenueByYear(years));
    }
}
