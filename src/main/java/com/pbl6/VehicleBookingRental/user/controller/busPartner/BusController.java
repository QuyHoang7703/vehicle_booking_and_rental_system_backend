package com.pbl6.VehicleBookingRental.user.controller.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.Bus;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqBusDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBus;
import com.pbl6.VehicleBookingRental.user.dto.response.bus.ResBusDetail;
import com.pbl6.VehicleBookingRental.user.service.BusService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@PreAuthorize("hasRole('BUS_PARTNER')")
public class BusController {
    private final BusService busService;

    @PostMapping("/buses")
    public ResponseEntity<ResBus> createBus(@RequestPart("busInfo") ReqBusDTO reqBus,
                                            @RequestPart("busImages") List<MultipartFile> busImages) throws Exception {
        Bus createdBus = this.busService.createBus(reqBus, busImages);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.busService.convertToResBus(createdBus));
    }

    @PutMapping("/buses")
    public ResponseEntity<ResBus> updateBus(@RequestPart("busInfo") ReqBusDTO reqBus,
                                                  @RequestPart("busImages") List<MultipartFile> busImages) throws Exception {
        Bus updatedBus = this.busService.updateBus(reqBus, busImages);
        return ResponseEntity.status(HttpStatus.OK).body(this.busService.convertToResBus(updatedBus));
    }

    @DeleteMapping("/buses")
    @ApiMessage("Deleted this bus")
    public ResponseEntity<Void> deleteBus(@RequestParam("busId") int busId) throws Exception {
        this.busService.deleteBus(busId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/buses")
    public ResponseEntity<ResBusDetail> fetchBusDetail(@RequestParam("busId") int busId) throws Exception {
        Bus bus = this.busService.findBusById(busId);
        return ResponseEntity.status(HttpStatus.OK).body(this.busService.convertToResBusDetail(bus));
    }

    @GetMapping("/buses-all")
    public ResponseEntity<ResultPaginationDTO> fetchAllBuses(@Filter Specification<Bus> spec, Pageable pageable) throws Exception {
        return ResponseEntity.status(HttpStatus.OK).body(this.busService.getAllBuses(spec, pageable));
    }

}
