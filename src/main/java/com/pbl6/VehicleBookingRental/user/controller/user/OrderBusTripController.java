package com.pbl6.VehicleBookingRental.user.controller.user;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderBusTripRedisDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.order.ReqOrderBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResOrderKey;
import com.pbl6.VehicleBookingRental.user.service.OrderBusTripService;
import com.pbl6.VehicleBookingRental.user.util.annotation.ApiMessage;
import com.pbl6.VehicleBookingRental.user.util.constant.OrderTypeEnum;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
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
public class OrderBusTripController {
    private final OrderBusTripService orderBusTripService;

    @PostMapping("orderBusTrips")
    public ResponseEntity<ResOrderKey> createOrderBusTrip(@RequestBody ReqOrderBusTripDTO reqOrderBusTripDTO) throws ApplicationException {
        OrderBusTripRedisDTO orderBusTripRedis = this.orderBusTripService.createOrderBusTrip(reqOrderBusTripDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.orderBusTripService.getKeyOfOrderBusTripRedisDTO(orderBusTripRedis));
    }

    @GetMapping("orderBusTrips")
    public ResponseEntity<ResultPaginationDTO> getAllOrderBusTrips(@Filter Specification<OrderBusTrip> spec,
                                                                   Pageable pageable,
                                                                   @RequestParam(value = "isGone", required = false) Boolean isGone) throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(this.orderBusTripService.getAllOrderBusTrip(spec, pageable, isGone));
    }

    @PatchMapping("/orderBusTrips/cancel-order/{orderId}")
    @ApiMessage("Cancelled this order successfully")
    public ResponseEntity<Void> cancelOrder(@PathVariable("orderId") String orderId) throws ApplicationException, IdInvalidException {
        this.orderBusTripService.cancelOrderBusTrip(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("orderBusTrips/customer")
    @PreAuthorize("hasRole('BUS_PARTNER')")
    public ResponseEntity<ResultPaginationDTO> getCustomersByOrderBusTrip(@Filter Specification<OrderBusTrip> spec, Pageable pageable,
                                                                          @RequestParam("busTripScheduleId") int busTripScheduleId,
                                                                          @RequestParam(value = "orderDate", required = false) LocalDate orderDate) throws ApplicationException {
        return ResponseEntity.status(HttpStatus.OK).body(this.orderBusTripService.getCustomersByOrderBusTrip(spec, pageable, busTripScheduleId, orderDate));
    }


}
