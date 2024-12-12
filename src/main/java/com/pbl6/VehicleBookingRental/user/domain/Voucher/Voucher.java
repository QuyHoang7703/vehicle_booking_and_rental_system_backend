package com.pbl6.VehicleBookingRental.user.domain.Voucher;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "voucher")
@Getter
@Setter
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private double voucherPercentage;
    private double maxDiscountValue;
    private double minOrderValue;
    private int remainingQuantity;

    @OneToMany(mappedBy = "voucher")
    @JsonIgnore
    private List<AccountVoucher> accountVouchers;


}
