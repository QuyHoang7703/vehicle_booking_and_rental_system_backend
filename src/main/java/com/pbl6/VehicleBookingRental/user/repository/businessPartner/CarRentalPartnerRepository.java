package com.pbl6.VehicleBookingRental.user.repository.businessPartner;

import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRentalPartnerRepository extends JpaRepository<CarRentalPartner, Integer>, JpaSpecificationExecutor<CarRentalPartner> {
    Optional<CarRentalPartner> findByBusinessPartner(BusinessPartner businessPartner);
}
