package com.pbl6.VehicleBookingRental.user.repository.order;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderBusTripRepository extends JpaRepository<OrderBusTrip, String>, JpaSpecificationExecutor<OrderBusTrip> {
    List<OrderBusTrip> findByDepartureDateAndBusTripScheduleId(LocalDate departureDate, int busTripScheduleId);
}
