package com.pbl6.VehicleBookingRental.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pbl6.VehicleBookingRental.util.constant.AccountEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
@Entity
@Table(name="Accounts")
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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

    @Column(columnDefinition = "MEDIUMTEXT")
    private String otp;
    private Instant otpExpirationTime;
    private boolean verified;

}
