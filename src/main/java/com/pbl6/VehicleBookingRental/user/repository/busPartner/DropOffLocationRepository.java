package com.pbl6.VehicleBookingRental.user.repository.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.DropOffLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DropOffLocationRepository extends JpaRepository<DropOffLocation, Integer>, JpaSpecificationExecutor<DropOffLocation> {
    @Query("SELECT dol FROM DropOffLocation dol " +
            "JOIN dol.busTrip bt " +
            "JOIN bt.busTripSchedules bts " +
            "WHERE dol.province = :province " +
            "AND bts.id = :busTripScheduleId")
    Optional<DropOffLocation> findByProvinceAndBusTripScheduleId(@Param("province") String province,
                                                                 @Param("busTripScheduleId") int busTripScheduleId);

    Optional<DropOffLocation> findByProvinceAndBusTripId(String province, int busTripId);
}
