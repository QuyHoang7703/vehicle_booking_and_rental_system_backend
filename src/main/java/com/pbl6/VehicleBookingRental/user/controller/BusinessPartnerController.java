package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusPartner;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalPartner;
import com.pbl6.VehicleBookingRental.user.dto.ResponseInfo;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.businessPartner.ReqPartnerAction;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResBusPartnerDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.businessPartner.ResCarRentalPartnerDTO;
import com.pbl6.VehicleBookingRental.user.service.BusPartnerService;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.service.CarRentalPartnerService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.user.util.constant.ApprovalStatusEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class BusinessPartnerController {
    private final BusinessPartnerService businessPartnerService;
    private final BusPartnerService busPartnerService;
    private final CarRentalPartnerService carRentalPartnerService;

    @GetMapping("business-partners")
    @ApiMessage("Get all register business partner forms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultPaginationDTO> getAllBusinessPartners(@Filter Specification<BusinessPartner> spec,
                                                                      @PageableDefault Pageable pageable) {
        ResultPaginationDTO resultPaginationDTO = this.businessPartnerService.handleFetchAllBusinessPartner(spec, pageable);

        
        return ResponseEntity.status(HttpStatus.OK).body(resultPaginationDTO);
    }

    @PutMapping("business-partner/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseInfo<String>> verifyRegister(@RequestParam("formRegisterId") int id) throws Exception {
        BusinessPartner businessPartner = this.businessPartnerService.getBusinessPartnerById(id);
        PartnerTypeEnum partnerType = businessPartner.getPartnerType();
        if (businessPartner == null) {
            throw new IdInvalidException("Không tìm thấy đơn đăng ký đối tác (" + partnerType + ") với id: " + id );
        }
        if(businessPartner.getApprovalStatus()==ApprovalStatusEnum.APPROVED){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Bạn đã duyệt đơn đăng ký này rồi"));
        }
        this.businessPartnerService.verifyRegister(id, partnerType);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Đăng ký thành công đối tác: " + partnerType));
    }

    @PutMapping("business-partner/cancel-partnership")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseInfo<String>> cancelPartnership(@RequestBody ReqPartnerAction reqPartnerAction) throws Exception {
        int formRegisterId = reqPartnerAction.getFormRegisterId();
        BusinessPartner businessPartner = this.businessPartnerService.getBusinessPartnerById(formRegisterId);
        PartnerTypeEnum partnerType = businessPartner.getPartnerType();
        if (businessPartner == null) {
            throw new IdInvalidException("Không tìm thấy đơn đăng ký đối tác (" + partnerType + ") với id: " + formRegisterId );
        }
        if(businessPartner.getApprovalStatus()==ApprovalStatusEnum.CANCEL){
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Bạn đã hủy đơn đăng ký này rồi"));
        }
        this.businessPartnerService.cancelPartnership(reqPartnerAction);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Đã hủy đối tác thành công: " + partnerType));
    }

    @GetMapping("business-partner/detail")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getBusPartnerById(@RequestParam("formRegisterId") int id) throws Exception {
        BusinessPartner businessPartner = this.businessPartnerService.getBusinessPartnerById(id);

        // Check user have the permission to see form register (Must be admin or owner's form register)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();
        String email = authentication.getName();

        if(!authorities.contains("ROLE_ADMIN") && !businessPartner.getAccount().getEmail().equals(email)){
            throw new ApplicationException("You don't have permission to see this form register");
        }

        PartnerTypeEnum partnerType = businessPartner.getPartnerType();
        if(partnerType == PartnerTypeEnum.BUS_PARTNER){
            BusPartner busPartner = this.busPartnerService.getBusPartnerByBusinessPartnerId(id);
            ResBusPartnerDTO resBusPartnerDTO = this.busPartnerService.convertToResBusPartnerDTO(busPartner);
            return ResponseEntity.status(HttpStatus.OK).body(resBusPartnerDTO);
        }
        if(partnerType == PartnerTypeEnum.CAR_RENTAL_PARTNER){
            CarRentalPartner carRentalPartner = this.carRentalPartnerService.getCarRentalPartnerByBusinessPartnerId(id);
            ResCarRentalPartnerDTO resCarRentalPartnerDTO = this.carRentalPartnerService.convertoCarRentalPartnerDTO(carRentalPartner);
            return ResponseEntity.status(HttpStatus.OK).body(resCarRentalPartnerDTO);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid partnerType provided.");

    }

    @DeleteMapping("business-partner/refuse-register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseInfo<String>> refuseBusinessPartner(@RequestBody ReqPartnerAction reqPartnerAction) throws Exception {
        this.businessPartnerService.refuseOrDeleteRegisterBusinessPartner(reqPartnerAction);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Refused this register"));
    }

    @GetMapping("business-partner/register-status")
    public ResponseEntity<ResponseInfo<String>> registerStatus(@RequestParam("partnerType") PartnerTypeEnum partnerType) throws ApplicationException {
        String status = this.businessPartnerService.getStatusRegisterPartner(partnerType);
        if(status == null){
          return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Not registered yet"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>(status));
    }


}
