package com.pbl6.VehicleBookingRental.user.controller.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTrip;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.ResultStatisticDTO;
import com.pbl6.VehicleBookingRental.user.service.statistic.OrderBusTripStatisticService;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@PreAuthorize("hasRole('BUS_PARTNER') or hasRole('ADMIN')")
public class OrderBusTripStatisticController {
    private final OrderBusTripStatisticService orderBusTripStatisticService;

    @GetMapping("/bus-trip-order/statistics/revenue")
    public ResponseEntity<ResultStatisticDTO> getRevenueByMonthOfYear(@RequestParam(value = "year", required = false) Integer year) throws ApplicationException {

        return ResponseEntity.status(HttpStatus.OK).body(this.orderBusTripStatisticService.getOrderBusTripRevenueByPeriod(year));
    }

//    @GetMapping("/bus-trip-order/statistics/revenue/by-year")
//    public ResponseEntity<ResultStatisticDTO> getRevenueByYear() throws ApplicationException {
//
//        return ResponseEntity.status(HttpStatus.OK).body(this.orderBusTripStatisticService.getOrderBusTripRevenueByYear());
//    }

    @GetMapping("/bus-trip-order/statistics/orders")
    public ResponseEntity<ResultPaginationDTO> getStatisticOfOrdersByDays(Pageable pageable,
                                                                          @RequestParam(value = "startDate", required = false)LocalDate startDate,
                                                                          @RequestParam(value = "endDate", required = false)LocalDate endDate,
                                                                          @RequestParam(value = "route", required = false) String route,
                                                                          @RequestParam(value = "month", required = false) Integer month,
                                                                          @RequestParam(value = "year", required = false) Integer year) throws ApplicationException {
        if((startDate != null && endDate == null) || (startDate == null && endDate != null)) {
            throw new ApplicationException("The input must be complete with both start and end date");
        }
        if(month!=null && year == null) {
            throw new ApplicationException("The input must be complete with both month and year");
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.orderBusTripStatisticService.getStatisticOfOrdersByDays(pageable, startDate, endDate, route, month, year));
    }

}
