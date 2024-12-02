package com.pbl6.VehicleBookingRental.user.repository.vehicle_rental;

import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface CarRentalPartnerRepo extends JpaRepository<CarRentalPartner,Integer> {
    public Optional<CarRentalPartner> findCarRentalPartnerByBusinessPartner_AccountId(int accountId);
}
