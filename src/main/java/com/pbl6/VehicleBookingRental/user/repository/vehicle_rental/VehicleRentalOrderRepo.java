package com.pbl6.VehicleBookingRental.user.repository.vehicle_rental;

import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface VehicleRentalOrderRepo extends JpaRepository<CarRentalOrders,String> {
    @Query("SELECT o FROM CarRentalOrders o WHERE o.carRentalService.id = :carRentalServiceId AND o.start_rental_time >= :currentDate")
    List<CarRentalOrders> findFutureOrdersByCarRentalServiceId(@Param("carRentalServiceId") int carRentalServiceId, @Param("currentDate") Instant currentDate);
    @Query("select o from CarRentalOrders o where (:location is null or o.carRentalService.vehicleRegister.location = :location)" +
                                            "and (:vehicle_type is null or o.carRentalService.vehicleRegister.vehicleType.name = :vehicle_type)   ")
    List<CarRentalOrders> findCarRentalOrdersByVehicleRegisterProperties(@Param("location") String location,@Param("vehicle_type")String vehicle_type);
    List<CarRentalOrders> findCarRentalOrdersByCarRentalService_VehicleRegister_CarRentalPartner_Id(int carRentalPartnerId);
    List<CarRentalOrders> findCarRentalOrdersByCarRentalServiceId(int carRentalServiceId);
    List<CarRentalOrders> findCarRentalOrdersByStatus(String status);
    List<CarRentalOrders> findCarRentalOrdersByAccountId(int accountId);
}
