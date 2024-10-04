package com.pbl6.VehicleBookingRental.user.interfaces;

import com.pbl6.VehicleBookingRental.user.domain.car_rental.VehicleRegister;
import com.pbl6.VehicleBookingRental.user.domain.dto.car_rental_DTO.VehicleRentalServiceDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface VehicleRegisterInterface {
    public boolean register_vehicle(VehicleRegister vehicleRegister);

    public List<VehicleRentalServiceDTO> get_all_by_service_type(int serviceType,String status);
}
