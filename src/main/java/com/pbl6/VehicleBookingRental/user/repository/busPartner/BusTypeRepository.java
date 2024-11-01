package com.pbl6.VehicleBookingRental.user.repository.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BusTypeRepository extends JpaRepository<BusType, Integer>, JpaSpecificationExecutor<BusType> {
    boolean existsByNameAndNumberOfSeatAndChairType(String name, int numberOfSeat, String chairType);
}
