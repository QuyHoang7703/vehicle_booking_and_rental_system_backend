package com.pbl6.VehicleBookingRental.user.controller.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusType;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.busType.ResBusType;
import com.pbl6.VehicleBookingRental.user.service.BusTypeService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@PreAuthorize("hasRole('BUS_PARTNER')")
@RequiredArgsConstructor
public class BusTypeController {
    private final BusTypeService busTypeService;

    @PostMapping("/bus-types")
    public ResponseEntity<ResBusType> createBusType(@RequestBody BusType reqBusType) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.busTypeService.createBusType(reqBusType));
    }

    @GetMapping("/bus-types")
    public ResponseEntity<ResBusType> fetchById(@RequestParam("idBusType") int idBusType) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.busTypeService.findById(idBusType));
    }

    @PutMapping("/bus-types")
    public ResponseEntity<ResBusType> updateBusType(@RequestBody BusType reqBusType) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.busTypeService.updateBusType(reqBusType));
    }

    @DeleteMapping("/bus-types")
    @ApiMessage("Deleted this bus type")
    public ResponseEntity<Void> deleteBusType(@RequestParam("idBusType") int idBusType) throws Exception {
        this.busTypeService.deleteById(idBusType);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @GetMapping("/bus-types-all")
    public ResponseEntity<ResultPaginationDTO> fetchAllBusType(@Filter Specification<BusType> spec, Pageable pageable,
                                                               @RequestParam("idBusPartner") int idBusPartner) throws ApplicationException {
        Specification<BusType> newSpec = (root, query, criteriaBuilder) ->{
            Join<BusType, BusPartner> busPartnerJoin = root.join("busPartner");
            return criteriaBuilder.equal(busPartnerJoin.get("id"), idBusPartner);
        };
        Specification<BusType> finalSpec = spec.and(newSpec);
        return ResponseEntity.status(HttpStatus.OK).body(this.busTypeService.getAllBusTypes(finalSpec, pageable));
    }

}
