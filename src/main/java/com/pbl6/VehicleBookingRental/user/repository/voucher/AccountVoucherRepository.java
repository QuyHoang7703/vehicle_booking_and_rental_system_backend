package com.pbl6.VehicleBookingRental.user.repository.voucher;

import com.pbl6.VehicleBookingRental.user.domain.Voucher.AccountVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountVoucherRepository extends JpaRepository<AccountVoucher, Integer>, JpaSpecificationExecutor<AccountVoucher> {
    Optional<AccountVoucher> findByAccount_IdAndVoucher_Id(int accountId, int voucherId);
    List<AccountVoucher> findByAccount_Id(int accountId);
}
