package com.pbl6.VehicleBookingRental.user.repository.order;

import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.util.constant.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderBusTripRepository extends JpaRepository<OrderBusTrip, String>, JpaSpecificationExecutor<OrderBusTrip> {
    List<OrderBusTrip> findByDepartureDateAndBusTripScheduleId(LocalDate departureDate, int busTripScheduleId);

    List<OrderBusTrip> findByBusTripSchedule_IdIn(List<Integer> busTripScheduleIds);

    @Query("SELECT obt FROM OrderBusTrip obt " +
            "JOIN obt.busTripSchedule bts " +
            "JOIN bts.busTrip bt " +
            "JOIN bt.busPartner bp " +
            "WHERE bp.id = :busPartnerId")
    List<OrderBusTrip> findOrderBusTripsOfBusPartner(@Param("busPartnerId") int busPartnerId);


    @Query("SELECT obt FROM OrderBusTrip obt " +
            "JOIN obt.busTripSchedule bts " +
            "JOIN bts.busTrip bt " +
            "JOIN bt.busPartner bp " +
            "WHERE obt.departureDate BETWEEN :startDate AND :endDate " +
            "AND bp.id = :busPartnerId")
    List<OrderBusTrip> findOrderBusTripBetweenDates(@Param("busPartnerId") int busPartnerId,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);





}
