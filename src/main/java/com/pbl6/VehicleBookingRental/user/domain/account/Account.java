package com.pbl6.VehicleBookingRental.user.domain.account;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.BankAccount;
import com.pbl6.VehicleBookingRental.user.domain.BusinessPartner;
import com.pbl6.VehicleBookingRental.user.domain.Rating;
import com.pbl6.VehicleBookingRental.user.domain.Voucher.VoucherAccount;
import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Booking;
import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Driver;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import com.pbl6.VehicleBookingRental.user.domain.notification.NotificationAccount;

import com.pbl6.VehicleBookingRental.user.util.constant.AccountEnum;
import com.pbl6.VehicleBookingRental.user.util.constant.GenderEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="account")
@Getter
@Setter
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

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    private String avatar;

    private boolean active;

    private String lockReason;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;


    @Column(columnDefinition = "MEDIUMTEXT")
    private String otp;

    private Instant expirationTime;

    private boolean verified;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String token;

    @PrePersist
    public void onCreate() {
        this.active = true;
    }

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

    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL)
    private List<Rating> rating;

    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL)
    private List<CarRentalOrders> carRentalOrdersList;
}
