package com.pbl6.VehicleBookingRental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.pbl6.VehicleBookingRental.domain.Account;
import java.util.Optional;
@Repository
public interface AccountRepository  extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account>{
    Optional<Account> findByEmail(String email);
    Optional<Account> findByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Account> findByRefreshTokenAndEmail(String refreshToken, String email);
    Optional<Account> findByRefreshTokenAndPhoneNumber(String refreshToken, String phoneNumber);
}
