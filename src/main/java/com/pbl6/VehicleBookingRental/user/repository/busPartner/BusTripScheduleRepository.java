package com.pbl6.VehicleBookingRental.user.repository.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BusTripScheduleRepository extends JpaRepository<BusTripSchedule, Integer>, JpaSpecificationExecutor<BusTripScheduleRepository> {
}
