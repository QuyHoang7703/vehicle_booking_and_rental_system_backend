package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.dto.ResponseInfo;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.service.VehicleTypeService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
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

public class VehicleTypeController {
    private final VehicleTypeService vehicleTypeService;

    @PostMapping("/vehicle-types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleType> createVehicleType(@RequestBody VehicleType vehicleType) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.vehicleTypeService.createVehicleType(vehicleType));
    }

    @PutMapping("/vehicle-types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleType> updateVehicleType(@RequestBody VehicleType vehicleType) throws IdInvalidException {
        if(this.vehicleTypeService.findVehicleTypeById(vehicleType.getId()) == null) {
            throw new IdInvalidException("Id vehicle type is invalid with id = " + vehicleType.getId());
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.vehicleTypeService.updateVehicleType(vehicleType));
    }

    @DeleteMapping("/vehicle-types/{vehicleTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiMessage("Deleted Vehicle Type")
    public ResponseEntity<Void> deleteVehicleTypeById(@PathVariable("vehicleTypeId") int id) throws IdInvalidException {
        VehicleType vehicleType = this.vehicleTypeService.findVehicleTypeById(id);
        if(vehicleType == null) {
            throw new IdInvalidException("Id vehicle type is invalid with id = " + id);
        }
        this.vehicleTypeService.deleteVehicleType(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/vehicle-types/{vehicleTypeId}")
    public ResponseEntity<VehicleType> fetchById(@PathVariable("vehicleTypeId") int id) throws IdInvalidException {
        VehicleType vehicleType = this.vehicleTypeService.findVehicleTypeById(id);
        if(vehicleType == null) {
            throw new IdInvalidException("Id vehicle type is invalid with id = " + id);
        }
        return ResponseEntity.status(HttpStatus.OK).body(vehicleType);
    }

    @GetMapping("/vehicle-types")
    @ApiMessage("All vehicle types")
    public ResponseEntity<ResultPaginationDTO> fetchAllVehicleType(@Filter Specification<VehicleType> specification, Pageable pageable) {
        ResultPaginationDTO res = this.vehicleTypeService.getAllVehicleTypes(specification, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
