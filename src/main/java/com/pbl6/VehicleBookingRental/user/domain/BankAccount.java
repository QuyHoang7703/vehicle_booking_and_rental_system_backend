package com.pbl6.VehicleBookingRental.user.domain;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import com.pbl6.VehicleBookingRental.user.util.constant.PartnerTypeEnum;
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

    @Column(columnDefinition = "MEDIUMTEXT")
    private String accountNumber;

    @Column(name = "account_holder_name", length = 100)
    private String accountHolderName;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String aesKey;
    @Enumerated(EnumType.STRING)
    private PartnerTypeEnum partnerType;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}

