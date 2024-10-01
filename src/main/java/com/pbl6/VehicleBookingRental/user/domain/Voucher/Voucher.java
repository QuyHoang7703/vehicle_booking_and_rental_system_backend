package com.pbl6.VehicleBookingRental.user.domain.Voucher;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "voucher")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String description;
    private Date start_date;

    private Date end_date;

    @Column(name = "voucher_percentage")
    private double voucherPercentage;

    @Column(name = "voucher_amount")
    private double voucherAmount;
    private String status;
    private int number;
    @OneToMany(mappedBy = "voucher",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<VoucherAccount> voucherAccounts;
}
