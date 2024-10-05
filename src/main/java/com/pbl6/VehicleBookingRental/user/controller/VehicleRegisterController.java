package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.interfaces.VehicleRegisterInterface;
import com.pbl6.VehicleBookingRental.user.interfaces.VehicleRentalInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/vehicle_register")
public class VehicleRegisterController {
    @Autowired
    private VehicleRegisterInterface vehicleRegisterInterface;

    @GetMapping("/all")
    public ResponseEntity<?> get_all_by_service_type(@RequestParam("service_type") int service_type,@RequestParam("status")String status)
    {
        System.out.println(service_type+status);
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRegisterInterface.get_all_by_service_type(service_type,status));
    }
}
