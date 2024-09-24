package com.pbl6.VehicleBookingRental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pbl6.VehicleBookingRental.domain.Account;

@Repository
public interface AccountRepository  extends JpaRepository<Account, Long>{
    
    
}
