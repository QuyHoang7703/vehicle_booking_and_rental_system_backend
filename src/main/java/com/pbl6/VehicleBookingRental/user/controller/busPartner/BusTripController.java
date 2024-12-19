package com.pbl6.VehicleBookingRental.user.controller.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTrip;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripDTO;
import com.pbl6.VehicleBookingRental.user.service.BusTripService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@PreAuthorize("hasRole('BUS_PARTNER')")
public class BusTripController {
    private final BusTripService busTripService;

    @PostMapping("busTrips")
    @ApiMessage("Created bus trip")
    public ResponseEntity<ResBusTripDTO> createBusTrip(@RequestBody ReqBusTripDTO reqBusTripDTO) throws ApplicationException {
        BusTrip busTrip = busTripService.createBusTrip(reqBusTripDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.busTripService.convertToResBusTripDTO(busTrip));
    }

    @PutMapping("busTrips")
    @ApiMessage("Updated bus trip")
    public ResponseEntity<ResBusTripDTO> updateBusTrip(@RequestBody ReqBusTripDTO reqBusTripDTO) throws Exception {
        BusTrip busTrip = busTripService.updateBusTrip(reqBusTripDTO);
        return ResponseEntity.status(HttpStatus.OK).body(this.busTripService.convertToResBusTripDTO(busTrip));
    }

    @GetMapping("busTrips/{busTripId}")
    public ResponseEntity<ResBusTripDTO> getBusTripById(@PathVariable("busTripId") int busTripId) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(this.busTripService.findBusTripById(busTripId));
    }

    @GetMapping("busTrips")
    public ResponseEntity<ResultPaginationDTO> getAllBusTrips(@Filter Specification<BusTrip> spec, Pageable pageable ) throws ApplicationException {

        return ResponseEntity.status(HttpStatus.OK).body(this.busTripService.getAllBusTrips(spec, pageable));
    }

    @DeleteMapping("busTrips")
    @ApiMessage("Deleted this bus trip")
    public ResponseEntity<Void> deleteBusTripById(@RequestParam("idBusTrip") int idBusTrip) throws IdInvalidException, ApplicationException {
        this.busTripService.deleteBusTrip(idBusTrip);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("busTrips/routes")
    public ResponseEntity<List<String>> getRouteOfBusTrips() throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(this.busTripService.getRouteOfBusTrips());
    }

}
