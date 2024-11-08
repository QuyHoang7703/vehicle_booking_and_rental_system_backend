package com.pbl6.VehicleBookingRental.user.controller.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.DropOffLocation;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.DropOffLocation;
import com.pbl6.VehicleBookingRental.user.service.DropOffLocaionSevice;
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
public class DropOffLocationController {
    private final DropOffLocaionSevice dropOffLocaionSevice;

    @PostMapping("/dropOffLocations")
    public ResponseEntity<DropOffLocation> createDropOffLocation(@RequestBody DropOffLocation dropOffLocation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.dropOffLocaionSevice.createDropOffLocation(dropOffLocation));
    }

    @PutMapping("/dropOffLocations")
    public ResponseEntity<DropOffLocation> updateDropOffLocation(@RequestParam("dropOffLocationId") int dropOffLocationId) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.OK).body(this.dropOffLocaionSevice.updateDropOffLocation(dropOffLocationId));
    }

    @DeleteMapping("/dropOffLocations")
    @ApiMessage("Deleted this drop off location")
    public ResponseEntity<Void> deleteDropOffLocation(@RequestParam("dropOffLocationId") int dropOffLocationId) throws IdInvalidException {
        this.dropOffLocaionSevice.deleteDropOffLocation(dropOffLocationId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
