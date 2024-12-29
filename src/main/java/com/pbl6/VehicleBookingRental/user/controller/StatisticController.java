package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.statistic.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.service.statistic.StatisticService;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping("statistic/revenue")
    public ResponseEntity<ResultStatisticDTO> getRevenueByMonthOrByYear(@RequestParam(value="year", required = false) Integer year) throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(this.statisticService.getRevenueByMonthOrByYear(year));
    }

    @GetMapping("statistic/revenue-of-business-partner")
    public ResponseEntity<ResultPaginationDTO> getRevenueOfBusinessPartner(@RequestParam(value = "month", required = false) Integer month,
                                                                           @RequestParam("year") Integer year,
                                                                           @RequestParam("partnerType") PartnerTypeEnum partnerType,
                                                                           Pageable pageable) throws ApplicationException {

        return ResponseEntity.status(HttpStatus.OK).body(this.statisticService.getRevenueOfBusinessPartner(month, year, partnerType, pageable));
    }

    @GetMapping("statistic/customer-statistic")
    public ResponseEntity<ResultPaginationDTO> getCustomerOfBusinessPartner(@RequestParam(value = "month", required = false) Integer month,
                                                                           @RequestParam("year") Integer year,
                                                                           @RequestParam("businessPartnerId") int businessPartnerId,
                                                                           Pageable pageable) throws IdInvalidException {

        return ResponseEntity.status(HttpStatus.OK).body(this.statisticService.getCustomerOfBusinessPartner(month, year, businessPartnerId, pageable));
    }


}
