package com.pbl6.VehicleBookingRental.user.repository.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BreakDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BreakDayRepository extends JpaRepository<BreakDay, Integer> {
    List<BreakDay> findByBusTripSchedule_Id(int busTripScheduleId);
    boolean existsByStartDayAndEndDayAndBusTripSchedule_Id(LocalDate startDay, LocalDate endDay, int busTripScheduleId);
}
