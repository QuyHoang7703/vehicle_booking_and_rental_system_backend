package com.pbl6.VehicleBookingRental.user.repository;

import com.pbl6.VehicleBookingRental.user.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer>, JpaSpecificationExecutor<Rating> {
    @Query("SELECT COUNT(r) FROM Rating r " +
            "JOIN r.order o " +
            "JOIN o.orderBusTrip obt " +
            "JOIN obt.busTripSchedule bts " +
            "WHERE bts.id = :busTripScheduleId")
    Integer getNumberRatingsOfBusTripSchedule(@Param("busTripScheduleId") int busTripScheduleId);

    @Query("SELECT COUNT(r) FROM Rating r " +
            "JOIN r.order o " +
            "JOIN o.carRentalOrders cro " +
            "JOIN cro.carRentalService crs " +
            "WHERE crs.id = :carRentalServiceId")
    Integer getNumberRatingOfCarRentalService(@Param("carRentalServiceId") int carRentalServiceId);
}
