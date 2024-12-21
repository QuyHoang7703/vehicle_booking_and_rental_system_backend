package com.pbl6.VehicleBookingRental.user.controller;

import com.pbl6.VehicleBookingRental.user.dto.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.dto.RevenueStatisticDTO;
import com.pbl6.VehicleBookingRental.user.service.statistic.OrderBusTripStatisticService;
import com.pbl6.VehicleBookingRental.user.service.statistic.StatisticService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ResultStatisticDTO> getRevenueFromBusinessPartner(@RequestParam(value="year", required = false) Integer year) throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(this.statisticService.getRevenueStatisticFromBusinessPartner(year));
    }
}
