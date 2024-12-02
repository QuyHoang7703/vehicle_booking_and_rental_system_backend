package com.pbl6.VehicleBookingRental.user.repository.businessPartner;

import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Driver;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Integer>, JpaSpecificationExecutor<Driver> {
    void deleteById(int id);
    Optional<Driver> findById(int id);
    Optional<Driver> findDriverByAccountId(int accountId);
    boolean existsByAccount_Id(int id);
    @Transactional
    void delete(Driver driver);

}
