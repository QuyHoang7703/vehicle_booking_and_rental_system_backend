package com.pbl6.VehicleBookingRental.user.repository;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.Utility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UtilityRepository extends JpaRepository<Utility, Integer>, JpaSpecificationExecutor<Utility> {
    Optional<Utility> findByName(String name);
    List<Utility> findByIdIn(List<Integer> ids);
    @Query("SELECT u FROM Utility u JOIN u.buses b WHERE b.id = :busId")
    List<Utility> findBy_BusId(@Param("busId") int busId);
}
