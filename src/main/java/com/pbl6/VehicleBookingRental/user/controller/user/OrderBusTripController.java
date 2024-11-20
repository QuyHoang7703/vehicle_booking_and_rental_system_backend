package com.pbl6.VehicleBookingRental.user.controller.user;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderBusTripRedisDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.order.ReqOrderBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResOrderBusTripDTO;
import com.pbl6.VehicleBookingRental.user.service.OrderBusTripService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class OrderBusTripController {
    private final OrderBusTripService orderBusTripService;


    @PostMapping("orderBusTrips")
    public ResponseEntity<ResOrderBusTripDTO> createOrderBusTrip(@RequestBody ReqOrderBusTripDTO reqOrderBusTripDTO) throws ApplicationException {
        OrderBusTripRedisDTO orderBusTripRedis = this.orderBusTripService.createOrderBusTrip(reqOrderBusTripDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderBusTripService.convertToResOrderBusTripDTO(orderBusTripRedis));
    }


}
