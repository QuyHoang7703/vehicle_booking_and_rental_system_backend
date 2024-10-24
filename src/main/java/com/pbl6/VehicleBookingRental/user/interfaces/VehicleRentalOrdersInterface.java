package com.pbl6.VehicleBookingRental.user.interfaces;

import com.pbl6.VehicleBookingRental.user.dto.car_rental_DTO.VehicleRentalOrdersDTO;
import org.springframework.stereotype.Repository;

public interface VehicleRentalOrdersInterface {
    public boolean save_order(VehicleRentalOrdersDTO vehicleRentalOrdersDTO);

}
