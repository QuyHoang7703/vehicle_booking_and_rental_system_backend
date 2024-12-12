package com.pbl6.VehicleBookingRental.user.controller.busPartner;

import com.pbl6.VehicleBookingRental.user.dto.LocationDTO;
import com.pbl6.VehicleBookingRental.user.dto.OpenRouteServiceDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.bus.ReqDropOffLocationDTO;
import com.pbl6.VehicleBookingRental.user.service.DropOffLocationService;
import com.pbl6.VehicleBookingRental.user.service.OSRService;
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
public class DropOffLocationController {
    private final DropOffLocationService dropOffLocationService;
    private final OSRService osrService;

    @PostMapping("/dropOffLocations")
    @ApiMessage("Created new drop off location")
    public ResponseEntity<Void> createDropOffLocation(@RequestBody ReqDropOffLocationDTO reqDropOffLocationDTO) throws Exception {
        this.dropOffLocationService.createDropOffLocation(reqDropOffLocationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/get-duration-distance-for-journey")
    public ResponseEntity<?> getDistance(@RequestBody List<LocationDTO> locationDTOS){
        if (locationDTOS == null || locationDTOS.size() < 2) {
            return ResponseEntity.badRequest().body("Cần ít nhất hai địa điểm.");
        }
        LocationDTO source = locationDTOS.get(0);
        LocationDTO destination = locationDTOS.get(1);
        System.out.println(source);
        System.out.println(destination);
        try{
            OpenRouteServiceDTO openRouteServiceDTO = osrService.getDistanceAndDuration(source,destination);
            return ResponseEntity.status(HttpStatus.OK).body(openRouteServiceDTO);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
