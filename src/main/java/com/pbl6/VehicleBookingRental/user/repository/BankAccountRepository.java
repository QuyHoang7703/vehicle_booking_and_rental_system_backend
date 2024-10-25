package com.pbl6.VehicleBookingRental.user.repository;

import com.pbl6.VehicleBookingRental.user.domain.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Integer>, JpaSpecificationExecutor<BankAccount> {
}
