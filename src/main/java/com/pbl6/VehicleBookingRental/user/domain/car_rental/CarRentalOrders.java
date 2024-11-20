package com.pbl6.VehicleBookingRental.user.domain.car_rental;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pbl6.VehicleBookingRental.user.domain.Orders;
import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="car_rental_orders")
public class CarRentalOrders {
    @Id
    @Column(name = "order_id")
    private String id;
    private Date start_rental_time;
    private Date end_rental_time;
    private String pickup_location;
    private double total;
    private Instant created_at;
    private String status;
    private double voucher_value;
    private double voucher_percentage;
    private int amount;
    private double car_deposit;
    private double reservation_fee;
    private double price;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "car_rental_service_id")
    private CarRentalService carRentalService;

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "order_id")
    private Orders order;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name ="account_id")
    private Account account;
    

}
