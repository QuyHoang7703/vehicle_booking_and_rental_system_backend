package com.pbl6.VehicleBookingRental.user.controller.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.PickupLocation;
import com.pbl6.VehicleBookingRental.user.service.PickupLocationService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@PreAuthorize("hasRole('BUS_PARTNER')")
public class PickupLocationController {
    private final PickupLocationService pickupLocationService;

    @PostMapping("/pickupLocations")
    public ResponseEntity<PickupLocation> createPickupLocation(@RequestBody PickupLocation pickupLocation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.pickupLocationService.createPickupLocation(pickupLocation));
    }

    @PutMapping("/pickupLocations")
    public ResponseEntity<PickupLocation> updatePickupLocation(@RequestBody PickupLocation pickupLocation) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.OK).body(this.pickupLocationService.updatePickupLocation(pickupLocation));
    }

    @DeleteMapping("/pickupLocations")
    @ApiMessage("Deleted this pickup location")
    public ResponseEntity<Void> deletePickupLocation(@RequestParam("pickupLocationId") int pickupLocationId) throws IdInvalidException {
        this.pickupLocationService.deletePickupLocation(pickupLocationId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("pickupLocations")
    public ResponseEntity<List<String>> getPickupLocationNames(@RequestParam("provinceName") String provinceName) {
        return ResponseEntity.status(HttpStatus.OK).body(this.pickupLocationService.getPickupLocationByProvinceName(provinceName));
    }
}
