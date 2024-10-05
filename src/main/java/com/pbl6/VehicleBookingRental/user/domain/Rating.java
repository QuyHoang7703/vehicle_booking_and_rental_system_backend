package com.pbl6.VehicleBookingRental.user.domain;

import com.pbl6.VehicleBookingRental.user.domain.account.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.sql.Timestamp;

@Entity
@Table(name = "rating")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Timestamp createAt;
    private String ratableValue;
    private String content;


    private String orderType;

    @OneToOne
    @JoinColumn(name = "ratable_id", unique = true)
    private Orders order; // Giả sử bạn đã có lớp Order

    @ManyToOne
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;
}

