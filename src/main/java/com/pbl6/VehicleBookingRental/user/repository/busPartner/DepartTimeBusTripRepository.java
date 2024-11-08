package com.pbl6.VehicleBookingRental.user.repository.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.DepartTimeBusTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartTimeBusTripRepository extends JpaRepository<DepartTimeBusTrip, Integer> {
}
