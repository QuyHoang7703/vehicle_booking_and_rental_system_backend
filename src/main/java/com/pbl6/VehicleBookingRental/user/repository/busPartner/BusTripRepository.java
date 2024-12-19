package com.pbl6.VehicleBookingRental.user.repository.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTrip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    Page<BusTrip> findByBusPartner_Id(int busPartnerId, Pageable pageable);

    Page<BusTrip> findByBusPartner_IdAndDepartureLocationAndArrivalLocation(int busPartnerId, String departureLocation, String arrivalLocation, Pageable pageable);


}
