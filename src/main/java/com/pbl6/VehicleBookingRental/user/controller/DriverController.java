package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqDriveDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResDriverDTO;
import com.pbl6.VehicleBookingRental.user.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @PostMapping("/drivers")
    public ResponseEntity<ResDriverDTO> registerDriver(@RequestPart("driverInfo") ReqDriveDTO driveDTO,
                                                 @RequestParam("citizenImages") List<MultipartFile> citizenImages,
                                                 @RequestParam("driverImages") List<MultipartFile> driverImages) {
        ResDriverDTO resDriverDTO = driverService.registerDriver(driveDTO, citizenImages, driverImages);
        return ResponseEntity.status(HttpStatus.OK).body(resDriverDTO);
    }
}
