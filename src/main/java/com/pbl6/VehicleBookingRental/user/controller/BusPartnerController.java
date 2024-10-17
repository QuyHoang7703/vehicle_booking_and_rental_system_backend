package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqBusPartnerDTO;
import com.pbl6.VehicleBookingRental.user.service.BusPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class BusPartnerController {
    private final BusPartnerService businessPartnerService;

    @PostMapping(value ="/bus-partners")
    public ResponseEntity<String> registerBusinessPartner(@RequestPart("businessPartnerInfo") ReqBusPartnerDTO reqBusPartnerDTO,
                                                         @RequestParam("businessLicense") List<MultipartFile> licenses,
                                                          @RequestParam("businessImages") List<MultipartFile> images) {
        BusPartner busPartner = businessPartnerService.registerBusPartner(reqBusPartnerDTO, licenses, images);


        return ResponseEntity.ok("ok");
    }
}
