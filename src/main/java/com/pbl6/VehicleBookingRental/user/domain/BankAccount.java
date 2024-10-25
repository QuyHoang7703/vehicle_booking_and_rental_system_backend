package com.pbl6.VehicleBookingRental.user.domain;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "bank_account")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "account_number", length = 20)
    private String accountNumber;

    @Column(name = "account_holder_name", length = 100)
    private String accountHolderName;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    // Many-to-One relationship với bảng Account (giả định đã có class Account)
    @ManyToOne
//    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    @JoinColumn(name = "account_id")

    private Account account;
}

