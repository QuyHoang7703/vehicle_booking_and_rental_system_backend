package com.pbl6.VehicleBookingRental.user.repository.vehicle_rental;

import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRentalServiceRepo extends JpaRepository<CarRentalService,Integer> {
    @Query(value = "SELECT c.* FROM car_rental_service c " +
            "INNER JOIN vehicle_register v ON c.vehicle_register_id = v.id " +
            "WHERE c.type = ?1 AND v.status = ?2 AND v.car_rental_partner_id = ?3",
            nativeQuery = true)
    List<CarRentalService> findCarRentalServiceByTypeAndVehicleRegister_StatusAndVehicleRegister_CarRentalPartner_Id(
            int type,
            String status,
            int car_rental_partner_id);
    List<CarRentalService> findCarRentalServiceByTypeAndVehicleRegister_Status(int type,String status);

    List<CarRentalService> findAllByVehicleRegister_CarRentalPartner_Id(int car_rental_partner_id);
}
