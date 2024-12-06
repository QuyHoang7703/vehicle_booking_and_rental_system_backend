package com.pbl6.VehicleBookingRental.user.repository.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTrip;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusTripRepository extends JpaRepository<BusTrip, Integer>, JpaSpecificationExecutor<BusTrip> {
    @Query("SELECT bt from BusTrip bt " +
            "JOIN bt.busPartner bp " +
            "WHERE bp.id = :busPartnerId " +
            "AND bt.departureLocation = :departureLocation " +
            "AND bt.arrivalLocation = :arrivalLocation")
    Optional<BusTrip> findBusTripByDepartureLocationAndArrivalLocationAndBusPartner(@Param("busPartnerId") int busPartnerId,
                                                                                    @Param("departureLocation") String departureLocation,
                                                                                    @Param("arrivalLocation") String arrivalLocation);


}
