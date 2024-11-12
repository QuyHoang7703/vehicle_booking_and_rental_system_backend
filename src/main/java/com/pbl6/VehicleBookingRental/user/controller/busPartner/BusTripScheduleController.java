package com.pbl6.VehicleBookingRental.user.controller.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.service.BusTripScheduleService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
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
public class BusTripScheduleController {
    private final BusTripScheduleService busTripScheduleService;

    @PostMapping("busScheduleTrips")
    public ResponseEntity<ResBusTripScheduleDTO> createBusTripSchedule(@RequestBody ReqBusTripScheduleDTO reqBusTripScheduleDTO) throws ApplicationException, IdInvalidException {
        BusTripSchedule busTripSchedule = busTripScheduleService.createBusTripSchedule(reqBusTripScheduleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.busTripScheduleService.convertToResBusTripScheduleDTO(busTripSchedule));
    }

    @GetMapping("busScheduleTrips")
    public ResponseEntity<ResBusTripScheduleDTO> getBusTripSchedule(@RequestParam("idBusTripSchedule") int idBusTripSchedule) throws ApplicationException, IdInvalidException {

        return ResponseEntity.status(HttpStatus.OK).body(this.busTripScheduleService.getBusTripScheduleById(idBusTripSchedule));
    }


}
