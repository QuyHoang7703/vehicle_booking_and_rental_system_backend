package com.pbl6.VehicleBookingRental.user.service;

import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import com.pbl6.VehicleBookingRental.user.dto.ResultPaginationDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface VehicleTypeService {
    VehicleType createVehicleType(VehicleType vehicleType);
    VehicleType updateVehicleType(VehicleType vehicleType);
    VehicleType findVehicleTypeById(int id);
    void deleteVehicleType(int id);
    ResultPaginationDTO getAllVehicleTypes(Specification<VehicleType> specification, Pageable pageable);
}
