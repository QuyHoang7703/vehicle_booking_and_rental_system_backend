package com.pbl6.VehicleBookingRental.user.domain.Voucher;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.util.constant.VoucherStatusEnum;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "acccount_voucher")
@Data
public class AccountVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private VoucherStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

}
