package com.pbl6.VehicleBookingRental.user.domain;

import com.pbl6.VehicleBookingRental.user.domain.bookingcar.Booking;
import com.pbl6.VehicleBookingRental.user.domain.bus_service.OrderBusTrip;
import com.pbl6.VehicleBookingRental.user.domain.car_rental.CarRentalOrders;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    @Id
    private String id;
    private String order_type;
    private Instant create_at;
    private String customerName;
    private String customerPhoneNumber;
    private String transactionCode;
    private Instant cancelTime;
    private Integer cancelUserId;

    @OneToOne(mappedBy = "order",cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private CarRentalOrders carRentalOrders;
    @OneToOne(mappedBy = "order",cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Booking booking;
    @OneToOne(mappedBy = "order",cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private OrderBusTrip orderBusTrip;

    // Mối quan hệ 1-1 với Payment
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    // Mối quan hệ 1-nhiều với Rating
    @OneToOne(mappedBy = "order")
    private Rating rating;

    @PrePersist
    public void prePersist(){
        this.create_at = Instant.now();
    }


}
