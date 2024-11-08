package com.pbl6.VehicleBookingRental.user.controller.busPartner;

import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripDTO;
import com.pbl6.VehicleBookingRental.user.service.BusTripService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@PreAuthorize("hasRole('BUS_PARTNER')")
public class BusTripController {
    private final BusTripService busTripService;

    @PostMapping("busTrips")
    @ApiMessage("Created bus trip")
    public ResponseEntity<Void> createBusTrip(@RequestBody ReqBusTripDTO reqBusTripDTO) throws IdInvalidException {
        this.busTripService.createBusTrip(reqBusTripDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

}
