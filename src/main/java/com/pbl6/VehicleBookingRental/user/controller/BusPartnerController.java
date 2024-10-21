package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.dto.ResponseInfo;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqBusPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.service.BusPartnerService;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.util.error.IdInValidException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class BusPartnerController {
    private final BusPartnerService busPartnerService;
    private final BusinessPartnerService businessPartnerService;


    @PostMapping(value ="/bus-partners")
    public ResponseEntity<ResBusinessPartnerDTO> registerBusinessPartner(@RequestPart("businessPartnerInfo") ReqBusPartnerDTO reqBusPartnerDTO,
                                                                         @RequestParam(value = "avatar", required = false) MultipartFile avatar,
                                                                         @RequestParam(value = "businessLicense", required = false) List<MultipartFile> licenses,
                                                                         @RequestParam(value = "businessImages", required = false) List<MultipartFile> images) {
        ResBusinessPartnerDTO resBusinessPartnerDTO = this.busPartnerService.registerBusPartner(reqBusPartnerDTO, avatar, licenses, images);


        return ResponseEntity.status(HttpStatus.OK).body(resBusinessPartnerDTO);
    }

    @GetMapping("/bus-partners/{id}")
    @PreAuthorize("hasAuthority('VIEW_REGISTER_BUSINESS_PARTNER')")
    public ResponseEntity<ResBusPartnerDTO> getBusPartnerById(@PathVariable Integer id) throws IdInValidException {
        BusPartner busPartner = this.busPartnerService.getBusPartnerByBusinessPartnerId(id);
        ResBusPartnerDTO resBusPartnerDTO = this.busPartnerService.convertToResBusPartnerDTO(busPartner);
        return ResponseEntity.status(HttpStatus.OK).body(resBusPartnerDTO);
    }

//    @PutMapping("bus-partners/verify/{id}")
//    public ResponseEntity<ResponseInfo<String>> verifyRegister(@PathVariable Integer id, @RequestParam("partnerType") String partnerType) throws IdInValidException {
//        this.businessPartnerService.verifyRegister(id, partnerType);
//        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Đăng ký thành công đối tác: " + partnerType));
//    }
//
//    @DeleteMapping("bus-partners/cancel-partnership/{id}")
//    public ResponseEntity<ResponseInfo<String>> cancel(@PathVariable Integer id, @RequestParam("partnerType") String partnerType) throws IdInValidException {
//        this.businessPartnerService.cancelPartnership(id, partnerType);
//        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Đã hủy đối tác thành công: " + partnerType));
//    }


}
