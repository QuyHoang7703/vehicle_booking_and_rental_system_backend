package com.pbl6.VehicleBookingRental.user.repository.vehicle_rental;

import com.pbl6.VehicleBookingRental.user.domain.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleTypeRepo extends JpaRepository<VehicleType,Integer> {

}
