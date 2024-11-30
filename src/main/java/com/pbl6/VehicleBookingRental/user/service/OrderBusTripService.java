package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderBusTripRedisDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.order.ReqOrderBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResOrderBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResOrderBusTripDetailDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResOrderKey;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;
import com.pbl6.VehicleBookingRental.user.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface OrderBusTripService {
    OrderBusTripRedisDTO createOrderBusTrip(ReqOrderBusTripDTO reqOrderBusTripDTO) throws ApplicationException;
//    ResOrderBusTripDTO convertToResOrderBusTripDTO(OrderBusTripRedisDTO orderBusTrip) throws ApplicationException;
    ResOrderKey getKeyOfOrderBusTripRedisDTO(OrderBusTripRedisDTO orderBusTripRedisDTO) throws ApplicationException;
    ResOrderBusTripDetailDTO convertToResOrderBusTripDetailDTO(Orders orders) throws ApplicationException;
    ResOrderBusTripDTO convertToResOrderBusTripDTO(OrderBusTrip orderBusTrip) throws ApplicationException, IdInvalidException;
    ResultPaginationDTO getAllOrderBusTrip(Specification<OrderBusTrip> spec, Pageable pageable, Boolean isGone) throws ApplicationException;
    void cancelOrderBusTrip(String orderBusTripId) throws IdInvalidException, ApplicationException;
    ResultPaginationDTO getCustomersByOrderBusTrip(int busTripScheduleId, Specification<OrderBusTrip> spec, Pageable pageable);
}
