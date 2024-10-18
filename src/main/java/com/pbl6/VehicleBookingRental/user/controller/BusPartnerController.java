package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqBusPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusPartnerDTO;
import com.pbl6.VehicleBookingRental.user.service.BusPartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class BusPartnerController {
    private final BusPartnerService busPartnerService;


    @PostMapping(value ="/bus-partners")
    public ResponseEntity<ResBusPartnerDTO> registerBusinessPartner(@RequestPart("businessPartnerInfo") ReqBusPartnerDTO reqBusPartnerDTO,
                                                                    @RequestParam(value = "businessLicense", required = false) List<MultipartFile> licenses,
                                                                    @RequestParam(value = "businessImages", required = false) List<MultipartFile> images) {
        BusPartner busPartner = this.busPartnerService.registerBusPartner(reqBusPartnerDTO, licenses, images);


        return ResponseEntity.status(HttpStatus.OK).body(this.busPartnerService.convertToResBusPartnerDTO(busPartner));
    }
}
