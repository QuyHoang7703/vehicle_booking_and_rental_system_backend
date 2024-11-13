package com.pbl6.VehicleBookingRental.user.controller.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDetailDTO;
import com.pbl6.VehicleBookingRental.user.service.BusTripScheduleService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@PreAuthorize("hasRole('BUS_PARTNER')")
public class BusTripScheduleController {
    private final BusTripScheduleService busTripScheduleService;

    @PostMapping("busScheduleTrips")
    public ResponseEntity<ResBusTripScheduleDetailDTO> createBusTripSchedule(@RequestBody ReqBusTripScheduleDTO reqBusTripScheduleDTO) throws ApplicationException, IdInvalidException {
        BusTripSchedule busTripSchedule = busTripScheduleService.createBusTripSchedule(reqBusTripScheduleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.busTripScheduleService.convertToResBusTripScheduleDetailDTO(busTripSchedule));
    }

    @GetMapping("busScheduleTrips")
    public ResponseEntity<ResBusTripScheduleDetailDTO> getBusTripSchedule(@RequestParam("idBusTripSchedule") int idBusTripSchedule) throws IdInvalidException {

        return ResponseEntity.status(HttpStatus.OK).body(this.busTripScheduleService.getBusTripScheduleById(idBusTripSchedule));
    }

    @GetMapping("busScheduleTrips/get-all")
    public ResponseEntity<ResultPaginationDTO> getAllBusTripSchedule(@Filter Specification<BusTripSchedule> spec, Pageable pageable) throws ApplicationException {

        return ResponseEntity.status(HttpStatus.OK).body(this.busTripScheduleService.getAllBusTripSchedules(spec, pageable));

    }


}
