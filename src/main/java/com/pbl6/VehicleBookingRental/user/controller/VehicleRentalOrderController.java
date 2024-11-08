package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.RestResponse;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalOrdersDTO;
import com.pbl6.VehicleBookingRental.user.interfaces.VehicleRentalOrdersInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vehicle-rental-order")
public class VehicleRentalOrderController {

    @Autowired
    private VehicleRentalOrdersInterface vehicleRentalOrdersInterface;

    @PostMapping("/ordering")
    public ResponseEntity<?> orderingVehicleService(@RequestBody VehicleRentalOrdersDTO vehicleRentalOrdersDTO){
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setStatusCode(200);
        boolean result = vehicleRentalOrdersInterface.save_order(vehicleRentalOrdersDTO);
        vehicleRentalOrdersInterface.update_amount(vehicleRentalOrdersDTO.getVehicle_rental_service_id(),vehicleRentalOrdersDTO.getAmount());
        if(result){
            restResponse.setMessage("Ordering successfully !");
        }else{
            restResponse.setMessage("Ordering failed !");
        }
        return ResponseEntity.status(HttpStatus.OK).body(restResponse);
    }
}
