package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.dto.ResponseInfo;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.pbl6.VehicleBookingRental.user.util.error.IdInValidException;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class BusinessPartnerController {
    private final BusinessPartnerService businessPartnerService;

    @GetMapping("business-partners")
    public ResponseEntity<ResultPaginationDTO> getAllBusinessPartners(@Filter Specification<BusinessPartner> spec,
                                                                      @PageableDefault(size = 10) Pageable pageable) {
        ResultPaginationDTO resultPaginationDTO = this.businessPartnerService.handleFetchAllBusinessPartner(spec, pageable);

        
        return ResponseEntity.status(HttpStatus.OK).body(resultPaginationDTO);
    }

    @PutMapping("business-partner/verify/{id}")
    @PreAuthorize("hasAuthority('VERIFY_REGISTER_BUSINESS_PARTNER')")
    public ResponseEntity<ResponseInfo<String>> verifyRegister(@PathVariable Integer id, @RequestParam("partnerType") String partnerType) throws IdInValidException {
        this.businessPartnerService.verifyRegister(id, partnerType);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Đăng ký thành công đối tác: " + partnerType));
    }

    @DeleteMapping("business-partner/cancel-partnership/{id}")
    @PreAuthorize("hasAuthority('CANCEL_BUSINESS_PARTNER')")
    public ResponseEntity<ResponseInfo<String>> cancel(@PathVariable Integer id, @RequestParam("partnerType") String partnerType) throws IdInValidException {
        this.businessPartnerService.cancelPartnership(id, partnerType);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseInfo<>("Đã hủy đối tác thành công: " + partnerType));
    }
}
