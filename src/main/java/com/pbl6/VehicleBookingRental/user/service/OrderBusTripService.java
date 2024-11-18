package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderBusTripRedisDTO;
import com.pbl6.VehicleBookingRental.user.dto.request.order.ReqOrderBusTripDTO;
import com.pbl6.VehicleBookingRental.user.dto.response.order.ResOrderBusTripDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;

public interface OrderBusTripService {
    OrderBusTripRedisDTO createOrderBusTrip(ReqOrderBusTripDTO reqOrderBusTripDTO) throws ApplicationException;
    ResOrderBusTripDTO convertToResOrderBusTripDTO(OrderBusTripRedisDTO orderBusTrip) throws ApplicationException;
}
