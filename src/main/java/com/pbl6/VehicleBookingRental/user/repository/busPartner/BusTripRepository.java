package com.pbl6.VehicleBookingRental.user.repository.busPartner;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.BusTrip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    Page<BusTrip> findByBusPartner_Id(int busPartnerId, Pageable pageable);

    Page<BusTrip> findByBusPartner_IdAndDepartureLocationAndArrivalLocation(int busPartnerId, String departureLocation, String arrivalLocation, Pageable pageable);

    @Query(
            value = """
        WITH TopOrders AS (
            SELECT 
                obt.departure_location, 
                obt.arrival_location, 
                COUNT(*) AS order_count
            FROM order_bus_trip obt
            GROUP BY obt.departure_location, obt.arrival_location
            ORDER BY order_count DESC
            LIMIT 5
        )
        SELECT 
            bt.departure_location, 
            bt.arrival_location, 
            COUNT(*) AS number_of_order
        FROM bus_trip bt
        JOIN bus_trip_schedule bts ON bts.bus_trip_id = bt.id
        JOIN order_bus_trip obt ON obt.bus_trip_schedule_id = bts.id
        JOIN TopOrders top ON 
            obt.departure_location = top.departure_location 
            AND obt.arrival_location = top.arrival_location
        GROUP BY bt.departure_location, bt.arrival_location
        ORDER BY number_of_order DESC;
        """,
            nativeQuery = true
    )
    List<Object[]> findTopBusTrips();

//    List<>

}
