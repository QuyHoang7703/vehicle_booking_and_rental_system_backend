package com.pbl6.VehicleBookingRental.user.repository.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.DropOffLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Query("SELECT dol FROM DropOffLocation dol " +
            "WHERE dol.province = :province " +
            "AND dol.priceTicket = " +
                "(SELECT MIN(dol_sub.priceTicket) FROM DropOffLocation dol_sub " +
                "WHERE dol_sub.province = :province)")
    List<DropOffLocation> findPriceTicketForArrivalLocation(@Param("province") String province);

    @Query("SELECT dol FROM DropOffLocation dol " +
            "JOIN dol.busTrip bt " +
            "WHERE bt.id = :busTripId " +
            "AND dol.province = :arrivalLocation")
    Optional<DropOffLocation> findArrivalLocationOfBusTrip(@Param("busTripId") int busTripId,
                                                           @Param("arrivalLocation") String arrivalLocation);
}
