package com.pbl6.VehicleBookingRental.user.domain.bookingcar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "booking")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Booking {

    @Id
    @Column(name = "order_id")
    private String id;

    private Date create_at;
    private String starting_location;
    private String destination;
    private String status;
    private double total;
    @Column(name = "distance")
    private double distance;
    private String vehicles_type;

    private double voucher_percentage;
    private double voucher_amount;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "driver_id")
    private Driver driver;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_id")
    private Orders order;


}
