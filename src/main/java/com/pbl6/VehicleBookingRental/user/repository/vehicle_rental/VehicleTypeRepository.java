package com.pbl6.VehicleBookingRental.user.repository.vehicle_rental;

import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleTypeRepository extends JpaRepository<VehicleType,Integer>, JpaSpecificationExecutor<VehicleType> {
    Optional<VehicleType> findVehicleTypeByName(String name);
}