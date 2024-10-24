package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalPartner;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqCarRentalPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusinessPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResCarRentalPartnerDTO;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.service.CarRentalPartnerService;
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
public class CarRentalPartnerController {
    private final BusinessPartnerService businessPartnerService;
    private final CarRentalPartnerService carRentalPartnerService;

    @PostMapping(value ="/car-rental-partners")
    public ResponseEntity<ResBusinessPartnerDTO> registerBusinessPartner(@RequestPart("businessPartnerInfo") ReqCarRentalPartnerDTO reqCarRentalPartnerDTO,
                                                                         @RequestParam(value = "avatar", required = false) MultipartFile avatar,
                                                                         @RequestParam(value = "businessLicense", required = false) List<MultipartFile> licenses,
                                                                         @RequestParam(value = "businessImages", required = false) List<MultipartFile> images) throws ApplicationException {


        ResBusinessPartnerDTO resBusinessPartnerDTO = this.carRentalPartnerService.registerBusPartner(reqCarRentalPartnerDTO, avatar, licenses, images);


        return ResponseEntity.status(HttpStatus.OK).body(resBusinessPartnerDTO);
    }

    @GetMapping("/car-rental-partners/{id}")
    @PreAuthorize("hasAuthority('VIEW_REGISTER_BUSINESS_PARTNER')")
    public ResponseEntity<ResCarRentalPartnerDTO> getCarRentalPartnerById(@PathVariable Integer id) throws IdInvalidException {
        CarRentalPartner carRentalPartner = this.carRentalPartnerService.getCarRentalPartnerByBusinessPartnerId(id);
        ResCarRentalPartnerDTO resCarRentalPartnerDTO = this.carRentalPartnerService.convertoCarRentalPartnerDTO(carRentalPartner);
        return ResponseEntity.status(HttpStatus.OK).body(resCarRentalPartnerDTO);
    }

//    @PutMapping("car-rental-partners/verify/{id}")
//    public ResponseEntity<ResponseInfo<String>> verifyRegister(@PathVariable Integer id, @RequestParam("partnerType") String partnerType) throws IdInValidException {
//        this.businessPartnerService.verifyRegister(id, partnerType);
//        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Đăng ký thành công đối tác: " + partnerType));
//    }
//
//    @DeleteMapping("car-rental-partners/cancel-partnership/{id}")
//    public ResponseEntity<ResponseInfo<String>> cancel(@PathVariable Integer id, @RequestParam("partnerType") String partnerType) throws IdInValidException {
//        this.businessPartnerService.cancelPartnership(id, partnerType);
//        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Đã hủy đối tác thành công: " + partnerType));
//    }


}
