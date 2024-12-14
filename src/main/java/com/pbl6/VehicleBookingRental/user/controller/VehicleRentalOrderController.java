package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.RestResponse;
import com.pbl6.VehicleBookingRental.user.dto.LocationDTO;
import com.pbl6.VehicleBookingRental.user.dto.OpenRouteServiceDTO;
import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalOrdersDTO;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderVehicleRentalRedisDTO;
import com.pbl6.VehicleBookingRental.user.interfaces.VehicleRentalOrdersInterface;
import com.pbl6.VehicleBookingRental.user.service.impl.OSRImplement;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicle-rental-order")
public class VehicleRentalOrderController {
    @Autowired
    private OSRImplement osrService;

    @Autowired
    private VehicleRentalOrdersInterface vehicleRentalOrdersInterface;

    @PostMapping("/ordering")
    public ResponseEntity<?> orderingVehicleService(@RequestBody VehicleRentalOrdersDTO vehicleRentalOrdersDTO) throws ApplicationException {
        OrderVehicleRentalRedisDTO orderVehicleRentalRedis = vehicleRentalOrdersInterface.create_order_Rental(vehicleRentalOrdersDTO);
        return ResponseEntity.status(HttpStatus.OK).body(this.vehicleRentalOrdersInterface.getKeyOfOrderVehicleRentalRedisDTO(orderVehicleRentalRedis));

    }
    @GetMapping("/getDistance")
    public ResponseEntity<?> getDistance(@RequestBody List<LocationDTO> locationDTOS){
        if (locationDTOS == null || locationDTOS.size() < 2) {
            return ResponseEntity.badRequest().body("Cần ít nhất hai địa điểm.");
        }
        LocationDTO source = locationDTOS.get(0);
        LocationDTO destination = locationDTOS.get(1);
        System.out.println(source);
        System.out.println(destination);
        try{
            OpenRouteServiceDTO openRouteServiceDTO = osrService.getDistanceAndDuration(source,destination);
            return ResponseEntity.status(HttpStatus.OK).body(openRouteServiceDTO);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/calculate-price-by-start-and-end-time")
    public ResponseEntity<?> calculate_price_by_start_and_end_time(@RequestParam("start_time")String startTime,
                                                                    @RequestParam("end_time") String endTime,
                                                                   @RequestParam("priceADay") double priceADay){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy").withZone(ZoneId.systemDefault());
        Instant startDateInstant = Instant.from(dateTimeFormatter.parse(startTime));
        Instant endDateInstant = Instant.from(dateTimeFormatter.parse(endTime));
        return ResponseEntity.status(HttpStatus.OK).body(vehicleRentalOrdersInterface.calculatePriceOrderByStartAndEndDate(startDateInstant,endDateInstant,priceADay));
    }
}
