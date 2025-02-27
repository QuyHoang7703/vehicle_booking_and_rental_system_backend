package com.pbl6.VehicleBookingRental.user.repository.vehicle_rental;

import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface VehicleRentalOrderRepo extends JpaRepository<CarRentalOrders,String> {
    @Query("SELECT o FROM CarRentalOrders o WHERE o.carRentalService.id = :carRentalServiceId AND DATE(o.start_rental_time) >= DATE(:currentDate) AND o.status = :status")
    List<CarRentalOrders> findFutureOrdersByCarRentalServiceId(
            @Param("carRentalServiceId") int carRentalServiceId,
            @Param("currentDate") Instant currentDate,
            @Param("status") String status);
//    @Query("SELECT o FROM CarRentalOrders o WHERE o.carRentalService.id = :carRentalServiceId AND  o.status = :status")
//    List<CarRentalOrders> findOrdersByCarRentalServiceId(
//            @Param("carRentalServiceId") int carRentalServiceId,
//            @Param("status") String status);
    @Query("SELECT o FROM CarRentalOrders o WHERE DATE(o.start_rental_time) >= DATE(:currentDate) AND o.carRentalService.vehicleRegister.carRentalPartner.id = :partnerId")
    List<CarRentalOrders> findFutureOrdersByCarRentalPartner(
            @Param("currentDate") Instant currentDate,
            @Param("partnerId") int partnerId);


    List<CarRentalOrders> findCarRentalOrdersByCarRentalService_VehicleRegister_CarRentalPartner_Id(int carRentalPartnerId);
    List<CarRentalOrders> findCarRentalOrdersByCarRentalServiceId(int carRentalServiceId);


    List<CarRentalOrders> findCarRentalOrdersByStatus(String status);

    List<CarRentalOrders> findCarRentalOrdersByAccountIdAndStatus(int accountId,String status);
    List<CarRentalOrders> findCarRentalOrdersByAccountId(int accountId);

    @Query("SELECT  o from CarRentalOrders  o where (:location is null or o.carRentalService.vehicleRegister.location = :location)" +
            "and (:vehicleTypeName is null  or o.carRentalService.vehicleRegister.vehicleType.name = :vehicleTypeName)" +
            "and o.carRentalService.vehicleRegister.carRentalPartner.id = :partnerId ")
    List<CarRentalOrders> findCarRentalOrdersByLocationOrType(@Param("location") String location,@Param("vehicleTypeName") String vehicleTypeName,@Param("partnerId") int partnerId);

    @Query("SELECT cro FROM CarRentalOrders cro " +
            "JOIN cro.carRentalService crs " +
            "JOIN crs.vehicleRegister vhr " +
            "JOIN vhr.carRentalPartner crp " +
            "JOIN cro.order o " +
            "WHERE crp.id = :carRentalPartnerId " +
            "AND (:month IS NULL OR MONTH(o.create_at) = :month) " +
            "AND YEAR(o.create_at) = :year " +
            "AND cro.order.cancelTime IS NULL ")
    Page<CarRentalOrders> findCarRentalOrderByCarRentalPartner(@Param("carRentalPartnerId") int carRentalPartnerId, @Param("month") Integer month, @Param("year") Integer year, Pageable pageable);
}
