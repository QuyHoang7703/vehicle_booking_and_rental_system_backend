package com.pbl6.VehicleBookingRental.user.controller.busPartner;

import com.pbl6.VehicleBookingRental.user.dto.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.service.statistic.OrderBusTripStatisticService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@PreAuthorize("hasRole('BUS_PARTNER')")
public class OrderBusTripStatisticController {
    private final OrderBusTripStatisticService orderBusTripStatisticService;

    @GetMapping("/bus_trip_order/statistics/revenue/by-month/{year}")
    public ResponseEntity<ResultStatisticDTO> getRevenueByMonthOfYear(@PathVariable("year") Integer year) throws ApplicationException {

        return ResponseEntity.status(HttpStatus.OK).body(this.orderBusTripStatisticService.getOrderBusTripRevenueByMonthOfYear(year));
    }

    @GetMapping("/bus-trip-order/statistics/revenue/by-year")
    public ResponseEntity<ResultStatisticDTO> getRevenueByYear() throws ApplicationException {

        return ResponseEntity.status(HttpStatus.OK).body(this.orderBusTripStatisticService.getOrderBusTripRevenueByYear());
    }

}
