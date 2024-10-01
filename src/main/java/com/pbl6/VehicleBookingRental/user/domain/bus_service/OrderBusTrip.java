package com.pbl6.VehicleBookingRental.user.domain.bus_service;

import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "OrderBusTrip")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderBusTrip {
    @Id
    @Column(name = "order_id")
    private int id;

    private int numberOfTicket;
    private double priceTotal;
    private String status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "bus_trip_id")
    private BusTrip busTrip;

    @OneToOne
    @MapsId
    @JoinColumn(name = "order_id")
    private Orders order;
}