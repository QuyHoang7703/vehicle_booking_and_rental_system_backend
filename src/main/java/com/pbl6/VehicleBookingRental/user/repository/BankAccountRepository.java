package com.pbl6.VehicleBookingRental.user.repository;

import com.pbl6.VehicleBookingRental.user.domain.BankAccount;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Integer>, JpaSpecificationExecutor<BankAccount> {
    Optional<BankAccount> findByAccount_IdAndPartnerType(int account_id, PartnerTypeEnum partnerType);
}
