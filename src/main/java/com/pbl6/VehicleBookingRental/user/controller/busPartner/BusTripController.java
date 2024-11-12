package com.pbl6.VehicleBookingRental.user.controller.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTrip;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripDTO;
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
    public ResponseEntity<ResBusTripDTO> createBusTrip(@RequestBody ReqBusTripDTO reqBusTripDTO) throws IdInvalidException {
        BusTrip busTrip = busTripService.createBusTrip(reqBusTripDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.busTripService.convertToBusTripDTO(busTrip));
    }

    @PutMapping("busTrips")
    @ApiMessage("Updated bus trip")
    public ResponseEntity<ResBusTripDTO> updateBusTrip(@RequestBody ReqBusTripDTO reqBusTripDTO) throws IdInvalidException {
        BusTrip busTrip = busTripService.updateBusTrip(reqBusTripDTO);
        return ResponseEntity.status(HttpStatus.OK).body(this.busTripService.convertToBusTripDTO(busTrip));
    }

    @GetMapping("busTrips")
    public ResponseEntity<ResBusTripDTO> getBusTripById(@RequestParam("idBusTrip") int idBusTrip) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.OK).body(this.busTripService.findBusTripById(idBusTrip));
    }

}
