package com.pbl6.VehicleBookingRental.user.repository.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.DropOffLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DropOffLocationRepository extends JpaRepository<DropOffLocation, Integer>, JpaSpecificationExecutor<DropOffLocation> {
    List<DropOffLocation> findByIdIn(List<Integer> ids);
}
