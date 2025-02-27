package com.pbl6.VehicleBookingRental.user.controller.user;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.Utility;
import com.pbl6.VehicleBookingRental.user.dto.ResponseInfo;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusTripScheduleDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResPickupAndDropOffLocation;
import com.pbl6.VehicleBookingRental.user.service.*;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user/busTripSchedules")
public class BusTripScheduleForUserController {
    private final BusTripScheduleService busTripScheduleService;
    private final BusinessPartnerService businessPartnerService;
    private final BusService busService;
    private final UtilityService utilityService;
    private final BusTripService busTripService;
    @GetMapping("")
    public ResponseEntity<ResultPaginationDTO> getAllBusTripScheduleAvailableForUser(@Filter Specification<BusTripSchedule> spec,
                                                                                     Pageable pageable,
                                                                                     @RequestParam(value = "departureDate", required = false) LocalDate departureDate,
                                                                                     @RequestParam("departureLocation") String departureLocation,
                                                                                     @RequestParam("arrivalProvince") String arrivalProvince) throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(this.busTripScheduleService.getAllBusTripScheduleAvailableForUser(spec, pageable, departureLocation, arrivalProvince, departureDate));
    }
    @GetMapping("/policies/{businessPartnerId}")
    public ResponseEntity<List<String>> getPolicies(@PathVariable("businessPartnerId") int businessPartnerId) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.OK).body(this.businessPartnerService.getPolicies(businessPartnerId));
    }

    @GetMapping("/imagesOfBus/{busId}")
    public ResponseEntity<List<String>> getImagesOfBus(@PathVariable("busId") int busId) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.OK).body(this.busService.getImages(busId));
    }

    @GetMapping("/utilities/{busId}")
    public ResponseEntity<List<Utility>> getUtilitiesOfBus(@PathVariable("busId") int busId) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.OK).body(this.utilityService.getAllUtilityByBusId(busId));
    }

    @GetMapping("/pickup-dropOff-locations")
    public ResponseEntity<ResPickupAndDropOffLocation> getPickupAndDropOffLocation(@RequestParam("busTripScheduleId") int busTripScheduleId,
                                                                                   @RequestParam("arrivalProvince") String arrivalProvince) throws IdInvalidException, ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(this.busTripService.getPickupAndDropOffLocationById(busTripScheduleId, arrivalProvince));
    }

    @GetMapping("/detail")
    public ResponseEntity<ResBusTripScheduleDTO> getBusTripScheduleById(@RequestParam("busTripScheduleId") int busTripScheduleId,
                                                                        @RequestParam("departureDate") LocalDate departureDate,
                                                                        @RequestParam("arrivalProvince") String arrivalProvince) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(this.busTripScheduleService.getBusTripScheduleByIdForUser(busTripScheduleId, departureDate, arrivalProvince));
    }




}
