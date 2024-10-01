package com.pbl6.VehicleBookingRental.user.domain.account;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.BankAccount;
import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.Voucher.VoucherAccount;
import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Booking;
import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Driver;
import com.pbl6.VehicleBookingRental.user.domain.notification.NotificationAccount;

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
    private String username;
    private String password;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String name;
    @Column(name = "birth_day")
    private Date birthDay;
    @Column(name = "gender")
    private String male;
    private String email;
    @Column(name = "is_active")
    private boolean active;
    @Column(name = "reason")
    private String lockReason;

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
