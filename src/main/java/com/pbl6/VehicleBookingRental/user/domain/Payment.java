package com.pbl6.VehicleBookingRental.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Timestamp;

@Entity
@Table(name = "payment")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String status;
    private Timestamp paid_at;
    private String payment_type;
    private String order_type;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "order_id",unique = true)
    private Orders order;


}
