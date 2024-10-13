package com.pbl6.VehicleBookingRental.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


import com.pbl6.VehicleBookingRental.user.domain.account.Account;


import java.util.Optional;

@Repository
public interface AccountRepository  extends JpaRepository<Account, Integer>, JpaSpecificationExecutor<Account>{
    Optional<Account> findByEmail(String email);
    Optional<Account> findByPhoneNumber(String phoneNumber);
    Optional<Account> findByToken(String token);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Account> findByRefreshTokenAndEmail(String refreshToken, String email);
    Optional<Account> findByRefreshTokenAndPhoneNumber(String refreshToken, String phoneNumber);
}
