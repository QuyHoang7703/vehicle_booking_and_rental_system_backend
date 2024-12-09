package com.pbl6.VehicleBookingRental.user.controller;


import com.pbl6.VehicleBookingRental.user.service.VehicleRentalStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}
