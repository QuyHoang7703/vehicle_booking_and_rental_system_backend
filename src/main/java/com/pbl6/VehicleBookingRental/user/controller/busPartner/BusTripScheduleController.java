package com.pbl6.VehicleBookingRental.user.controller.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BreakDay;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.dto.ResponseInfo;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBreakDayDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDetailForAdminDTO;
import com.pbl6.VehicleBookingRental.user.service.BusTripScheduleService;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")

public class BusTripScheduleController {
    private final BusTripScheduleService busTripScheduleService;

    @PreAuthorize("hasRole('BUS_PARTNER')")
    @PostMapping("busTripSchedules")
    public ResponseEntity<ResBusTripScheduleDetailForAdminDTO> createBusTripSchedule(@RequestBody ReqBusTripScheduleDTO reqBusTripScheduleDTO) throws ApplicationException, IdInvalidException {
        BusTripSchedule busTripSchedule = busTripScheduleService.createBusTripSchedule(reqBusTripScheduleDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.busTripScheduleService.convertToResBusTripScheduleDetailDTO(busTripSchedule, null));
    }

    @PreAuthorize("hasRole('BUS_PARTNER')")
    @GetMapping("busTripSchedules/detail")
    public ResponseEntity<ResBusTripScheduleDetailForAdminDTO> getBusTripScheduleDetail(@RequestParam("busTripScheduleId") int busTripScheduleId,
                                                                                        @RequestParam("departureDate") LocalDate departureDate) throws IdInvalidException {

        return ResponseEntity.status(HttpStatus.OK).body(this.busTripScheduleService.getBusTripScheduleById(busTripScheduleId, departureDate));
    }

    @GetMapping("busTripSchedules/get-all")
    public ResponseEntity<ResultPaginationDTO> getAllBusTripScheduleByBusTripId(@Filter Specification<BusTripSchedule> spec, Pageable pageable,
                                                                     @RequestParam("busTripId") int busTripId, @RequestParam("departureDate") LocalDate departureDate) throws ApplicationException, IdInvalidException {

        return ResponseEntity.status(HttpStatus.OK).body(this.busTripScheduleService.getAllBusTripScheduleByBusTripId(spec, pageable, busTripId, departureDate));

    }

    @GetMapping("busTripSchedules/get-break-days/{busTripScheduleId}")
    public ResponseEntity<List<BreakDay>> getBreakDaysForBusTripSchedule(@PathVariable("busTripScheduleId") int busTripScheduleId) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(this.busTripScheduleService.getBreakDaysForBusTripSchedule(busTripScheduleId));
    }

    @PatchMapping("busTripSchedules/{busTripScheduleId}")
    @PreAuthorize("hasRole('BUS_PARTNER')")
    public ResponseEntity<Void> cancelBusTripSchedule(@PathVariable("busTripScheduleId") int busTripScheduleId,
                                                      @RequestParam("cancelDate") LocalDate cancelDate) throws Exception {
        this.busTripScheduleService.cancelBusTripSchedule(busTripScheduleId, cancelDate);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PatchMapping("busTripSchedules/{busTripScheduleId}/status")
    @PreAuthorize("hasRole('BUS_PARTNER')")
    @ApiMessage("Updated status of this bus trip schedule")
    public ResponseEntity<Void> updateStatusOfBusTripSchedule(@PathVariable("busTripScheduleId") int busTripScheduleId,
                                                      @RequestParam("suspended") boolean suspended) throws Exception {
        this.busTripScheduleService.updateStatusOfBusTripSchedule(busTripScheduleId, suspended);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("busTripSchedules/{busTripScheduleId}/hasOrders")
    @PreAuthorize("hasRole('BUS_PARTNER')")
    public ResponseEntity<ResponseInfo<String>> checkBusTripScheduleHasOrders(@PathVariable("busTripScheduleId") int busTripScheduleId) throws Exception {
        boolean check = this.busTripScheduleService.checkBusTripScheduleHasOrder(busTripScheduleId);
        if(check) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("This Bus Trip Schedule has orders in the next days !"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("This Bus Trip Schedule hasn't orders !"));
    }

    @PreAuthorize("hasRole('BUS_PARTNER')")
    @GetMapping("busTripSchedules")
    public ResponseEntity<ResultPaginationDTO> getAllBusTripSchedule(@Filter Specification<BusTripSchedule> spec, Pageable pageable) throws ApplicationException, IdInvalidException {

        return ResponseEntity.status(HttpStatus.OK).body(this.busTripScheduleService.getAllBusTripSchedules(spec, pageable));

    }

    @PostMapping("busTripSchedules/breakDays")
    public ResponseEntity<Void> addBreakDayForBusTripSchedules(@RequestBody ReqBreakDayDTO reqBreakDayDTO) throws Exception {
        this.busTripScheduleService.addBreakDay(reqBreakDayDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @DeleteMapping("busTripSchedule/breakDays/{breakDayId}")
    @ApiMessage("Deleted this break day")
    public ResponseEntity<Void> deleteBreakDay(@PathVariable("breakDayId") int breakDayId) throws Exception {
        this.busTripScheduleService.deleteBreakDay(breakDayId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }



}
