package com.pbl6.VehicleBookingRental.user.interfaces;

import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalOrdersDTO;
import com.pbl6.VehicleBookingRental.user.dto.redis.OrderVehicleRentalRedisDTO;
import com.pbl6.VehicleBookingRental.user.util.error.ApplicationException;

public interface VehicleRentalOrdersInterface {
    public OrderVehicleRentalRedisDTO create_order_Rental(VehicleRentalOrdersDTO vehicleRentalOrdersDTO) throws ApplicationException;

}
