package com.pbl6.VehicleBookingRental.user.controller;


import com.pbl6.VehicleBookingRental.user.service.VehicleRentalStatisticService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/get-monthly-venue-in-year")
    public ResponseEntity<?> getMonthlyVenueInYear(@RequestParam("year") int year) throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRentalStatisticService.calculateMonthlyRevenue(year));
    }
}
