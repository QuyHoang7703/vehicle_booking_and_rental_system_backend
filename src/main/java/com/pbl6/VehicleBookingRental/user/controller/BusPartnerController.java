package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqBusPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.service.BusPartnerService;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class BusPartnerController {
    private final BusPartnerService busPartnerService;
    private final BusinessPartnerService businessPartnerService;


    @PostMapping(value ="/bus-partners")
    public ResponseEntity<ResBusinessPartnerDTO> registerBusinessPartner(@RequestPart("businessPartnerInfo") ReqBusPartnerDTO reqBusPartnerDTO,
                                                                         @RequestParam(value = "avatar") MultipartFile avatar,
                                                                         @RequestParam(value = "businessLicense") List<MultipartFile> licenses,
                                                                         @RequestParam(value = "businessImages") List<MultipartFile> images) throws Exception {

        ResBusinessPartnerDTO resBusinessPartnerDTO = this.busPartnerService.registerBusPartner(reqBusPartnerDTO, avatar, licenses, images);


        return ResponseEntity.status(HttpStatus.OK).body(resBusinessPartnerDTO);
    }

    @GetMapping("/bus-partner/businessName")
    public ResponseEntity<List<String>> getAllBusinessNames() {
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
