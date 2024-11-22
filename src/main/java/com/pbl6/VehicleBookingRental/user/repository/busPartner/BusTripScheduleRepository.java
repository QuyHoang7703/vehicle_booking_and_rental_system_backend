package com.pbl6.VehicleBookingRental.user.repository.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTripSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BusTripScheduleRepository extends JpaRepository<BusTripSchedule, Integer>, JpaSpecificationExecutor<BusTripSchedule> {
    @Query("SELECT b FROM BusTripSchedule b WHERE b.startOperationDay <= :currentDate")
    List<BusTripSchedule> findSchedulesBeforeToday(@Param("currentDate") LocalDate currentDate);
}
