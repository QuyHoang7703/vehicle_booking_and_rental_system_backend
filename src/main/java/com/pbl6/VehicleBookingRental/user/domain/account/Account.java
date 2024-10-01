package com.pbl6.VehicleBookingRental.user.domain.account;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.BankAccount;
import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.Voucher.VoucherAccount;
import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Booking;
import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Driver;
import com.pbl6.VehicleBookingRental.user.domain.notification.NotificationAccount;

import com.pbl6.VehicleBookingRental.user.util.constant.AccountEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="account")
@Getter
@Setter
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    @Column(unique = true)
    private String phoneNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate birthDay;

    private boolean male;

    private String avatar;

    private boolean active;

    private String lockReason;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    private AccountEnum accountType;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<AccountRole> accountRole;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Booking> bookings;
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<NotificationAccount> notificationAccountList;

    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<VoucherAccount> voucherAccounts;
    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BankAccount> bankAccounts;
    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BusinessPartner> businessPartners;

    @OneToOne( mappedBy = "account",cascade = CascadeType.ALL)
    @JsonIgnore
    private Driver driver;

}
