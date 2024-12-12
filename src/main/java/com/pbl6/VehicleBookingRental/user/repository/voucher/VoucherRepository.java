package com.pbl6.VehicleBookingRental.user.repository.voucher;

import com.pbl6.VehicleBookingRental.user.domain.Voucher.Voucher;
import com.pbl6.VehicleBookingRental.user.dto.response.voucher.ResVoucherDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer>, JpaSpecificationExecutor<Voucher> {
    @Query("SELECT v FROM Voucher v " +
            "JOIN v.accountVouchers av " +
            "WHERE av.account.id= :accountId " +
            "AND v.expired = false " +
            "AND v.remainingQuantity > 0" +
            "AND v.minOrderValue <= :totalOrder " +
            "AND av.status = 0")
    List<Voucher> getSuitableVoucherOfAccountForOrder(@Param("accountId") int accountId, @Param("totalOrder") double totalOrder);

    @Query("SELECT v from Voucher v " +
            "WHERE v.expired = false " +
            "AND v.id NOT IN (" +
            "SELECT av.voucher.id FROM AccountVoucher av " +
            "WHERE av.account.id = :accountId " +
            ")")
    List<Voucher> getAvailableVoucher(@Param("accountId") int accountId);

    List<Voucher> findByEndDateBefore(LocalDate localDate);

    @Query("SELECT v FROM Voucher v " +
            "WHERE v.remainingQuantity > 0 " +
            "AND v.expired = false ")
    List<Voucher> findAvailableVouchers();

}
