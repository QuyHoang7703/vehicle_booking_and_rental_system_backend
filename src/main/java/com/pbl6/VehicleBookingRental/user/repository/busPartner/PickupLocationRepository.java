package com.pbl6.VehicleBookingRental.user.repository.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.PickupLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickupLocationRepository extends JpaRepository<PickupLocation, Integer>, JpaSpecificationExecutor<PickupLocation> {
    List<PickupLocation> findByIdIn(List<Integer> ids);
}
