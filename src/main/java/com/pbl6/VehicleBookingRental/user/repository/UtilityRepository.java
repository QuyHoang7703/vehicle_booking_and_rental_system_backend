package com.pbl6.VehicleBookingRental.user.repository;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.Utility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UtilityRepository extends JpaRepository<Utility, Integer>, JpaSpecificationExecutor<Utility> {
    Optional<Utility> findByName(String name);
    List<Utility> findByIdIn(List<Integer> ids);
}
