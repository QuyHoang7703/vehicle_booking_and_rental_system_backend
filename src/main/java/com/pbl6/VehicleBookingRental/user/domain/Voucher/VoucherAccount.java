package com.pbl6.VehicleBookingRental.user.domain.Voucher;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import jakarta.persistence.*;



@Entity
@Table(name = "VoucherAccount")
public class VoucherAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "voucher_id", nullable = false)
    private int voucherId;

    @Column(name = "account_id", nullable = false)
    private int accountId;


    @ManyToOne
    @JoinColumn(name = "voucher_id", insertable = false, updatable = false)
    private Voucher voucher;

    @ManyToOne
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;
}
