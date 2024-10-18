package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.service.BusinessPartnerService;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
